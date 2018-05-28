package org.trc.biz.impl.allocateOrder;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.trc.biz.allocateOrder.IAllocateOutOrderBiz;
import org.trc.domain.allocateOrder.AllocateOrder;
import org.trc.domain.allocateOrder.AllocateOrderBase;
import org.trc.domain.allocateOrder.AllocateOutOrder;
import org.trc.domain.allocateOrder.AllocateSkuDetail;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.warehouseInfo.WarehouseInfo;
import org.trc.enums.AllocateOrderEnum;
import org.trc.enums.AllocateOrderEnum.AllocateOutOrderStatusEnum;
import org.trc.enums.ExceptionEnum;
import org.trc.enums.LogOperationEnum;
import org.trc.enums.OperationalNatureEnum;
import org.trc.enums.ZeroToNineEnum;
import org.trc.exception.AllocateOutOrderException;
import org.trc.form.AllocateOrder.AllocateOutOrderForm;
import org.trc.form.warehouse.allocateOrder.ScmAllocateOrderItem;
import org.trc.form.warehouse.allocateOrder.ScmAllocateOrderOutRequest;
import org.trc.form.warehouse.allocateOrder.ScmAllocateOrderOutResponse;
import org.trc.form.wms.WmsAllocateDetailRequest;
import org.trc.form.wms.WmsAllocateOutInRequest;
import org.trc.service.allocateOrder.IAllocateOrderExtService;
import org.trc.service.allocateOrder.IAllocateOrderService;
import org.trc.service.allocateOrder.IAllocateOutOrderService;
import org.trc.service.allocateOrder.IAllocateSkuDetailService;
import org.trc.service.config.ILogInfoService;
import org.trc.service.warehouse.IWarehouseApiService;
import org.trc.service.warehouseInfo.IWarehouseInfoService;
import org.trc.util.AppResult;
import org.trc.util.AssertUtil;
import org.trc.util.DateCheckUtil;
import org.trc.util.Pagenation;
import org.trc.util.ResponseAck;
import org.trc.util.ResultUtil;
import org.trc.util.cache.AllocateOrderCacheEvict;

import tk.mybatis.mapper.entity.Example;

@Service("allocateOutOrderBiz")
public class AllocateOutOrderBiz implements IAllocateOutOrderBiz {

    private Logger logger = LoggerFactory.getLogger(AllocateOutOrderBiz.class);
    @Autowired
    private IAllocateOutOrderService allocateOutOrderService;
    @Autowired
    private IAllocateSkuDetailService allocateSkuDetailService;
    @Autowired
    private ILogInfoService logInfoService;
    @Autowired
    private IAllocateOrderExtService allocateOrderExtService;
    @Autowired
    private IWarehouseApiService warehouseApiService;
    @Autowired
    private IWarehouseInfoService warehouseInfoService;
	@Autowired
    private IAllocateOrderService allocateOrderService;

    /**
     * 调拨单分页查询
     */
    @Override
    //@Cacheable(value = SupplyConstants.Cache.ALLOCATE_OUT_ORDER)
    public Pagenation<AllocateOutOrder> allocateOutOrderPage(AllocateOutOrderForm form,
															 Pagenation<AllocateOutOrder> page) {

        AssertUtil.notNull(page.getPageNo(),"分页查询参数pageNo不能为空");
        AssertUtil.notNull(page.getPageSize(),"分页查询参数pageSize不能为空");
        AssertUtil.notNull(page.getStart(),"分页查询参数start不能为空");

        Example example = new Example(AllocateOutOrder.class);
        Example.Criteria criteria = example.createCriteria();

        //调拨单编号
        if (!StringUtils.isBlank(form.getAllocateOrderCode())) {
            criteria.andLike("allocateOrderCode","%"+form.getAllocateOrderCode()+"%");
        }

        //调拨出库单编号
        if (!StringUtils.isBlank(form.getAllocateOutOrderCode())) {
            criteria.andLike("allocateOutOrderCode","%"+form.getAllocateOutOrderCode()+"%");
        }

        //单据创建人
        if (!StringUtils.isBlank(form.getCreateOperatorName())) {
            allocateOrderExtService.setCreateOperator(form.getCreateOperatorName(), criteria);
        }

        //调出仓库
        if (!StringUtils.isBlank(form.getOutWarehouseCode())) {
            criteria.andEqualTo("outWarehouseCode", form.getOutWarehouseCode());
        }

        //出入库状态
        if (!StringUtils.isBlank(form.getStatus())) {
            criteria.andEqualTo("status", form.getStatus());
        }

        //创建日期开始
        if (!StringUtils.isBlank(form.getStartDate())) {
            criteria.andGreaterThanOrEqualTo("createTime", form.getStartDate());
        }

        //创建日期结束
        if (!StringUtils.isBlank(form.getEndDate())) {
            criteria.andLessThanOrEqualTo("createTime", form.getEndDate());
        }

        example.setOrderByClause("field(status,0,2,1,3,4,5)");
        example.orderBy("updateTime").desc();

        Pagenation<AllocateOutOrder> pagenation = allocateOutOrderService.pagination(example, page, form);

        allocateOrderExtService.setIsTimeOut(pagenation);

        allocateOrderExtService.setAllocateOrderOtherNames(page);
        return pagenation;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @AllocateOrderCacheEvict
    public Response close(Long id, String remark, AclUserAccreditInfo aclUserAccreditInfo) {
        AssertUtil.notNull(id, "调拨出库单主键不能为空");
        AssertUtil.notBlank(remark, "关闭原因不能为空");

        //获取出库单信息
        AllocateOutOrder allocateOutOrder = allocateOutOrderService.selectByPrimaryKey(id);

        if(!StringUtils.equals(allocateOutOrder.getStatus(), AllocateOrderEnum.AllocateOutOrderStatusEnum.WAIT_NOTICE.getCode()) &&
                !StringUtils.equals(allocateOutOrder.getStatus(), AllocateOrderEnum.AllocateOutOrderStatusEnum.OUT_RECEIVE_FAIL.getCode())){
            String msg = "调拨出库通知单状态必须为出库仓接收失败或待通知出库!";
            logger.error(msg);
            throw new AllocateOutOrderException(ExceptionEnum.ALLOCATE_OUT_ORDER_CLOSE_EXCEPTION, msg);
        }

        //修改状态
        this.updateDetailStatus(AllocateOrderEnum.AllocateOutOrderStatusEnum.CANCEL.getCode(),
                allocateOutOrder.getAllocateOrderCode(), AllocateOrderEnum.AllocateOrderSkuOutStatusEnum.WAIT_OUT.getCode(), false);
        allocateOrderExtService.updateOrderCancelInfo(allocateOutOrder, remark, true,
                AllocateOrderEnum.AllocateOutOrderStatusEnum.CANCEL.getCode());

        //仓库接受失败插入一条日志
        String userId = aclUserAccreditInfo.getUserId();
        logInfoService.recordLog(allocateOutOrder, String.valueOf(allocateOutOrder.getId()),userId,"手工关闭", remark,null);
        return ResultUtil.createSuccessResult("调拨出库通知单关闭成功！", "");
        
    }

    @Override
    @AllocateOrderCacheEvict
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Response cancelClose(Long id, AclUserAccreditInfo aclUserAccreditInfo) {
        AssertUtil.notNull(id, "调拨出库单主键不能为空");

        //获取出库单信息
        AllocateOutOrder allocateOutOrder = allocateOutOrderService.selectByPrimaryKey(id);

        if(!StringUtils.equals(allocateOutOrder.getIsClose(), ZeroToNineEnum.ONE.getCode())){
            String msg = "调拨出库通知单没有关闭!";
            logger.error(msg);
            throw new AllocateOutOrderException(ExceptionEnum.ALLOCATE_OUT_ORDER_CLOSE_EXCEPTION, msg);
        }

        if(DateCheckUtil.checkDate(allocateOutOrder.getUpdateTime())){
            String msg = "调拨出库通知单已经超过7天，不允许取消关闭!";
            logger.error(msg);
            throw new AllocateOutOrderException(ExceptionEnum.ALLOCATE_OUT_ORDER_CLOSE_EXCEPTION, msg);
        }

        //修改状态
        this.updateDetailStatus("", allocateOutOrder.getAllocateOrderCode(),
                AllocateOrderEnum.AllocateOrderSkuOutStatusEnum.WAIT_OUT.getCode(), true);
        allocateOrderExtService.updateOrderCancelInfoExt(allocateOutOrder, true);

        String userId = aclUserAccreditInfo.getUserId();
        logInfoService.recordLog(allocateOutOrder, String.valueOf(allocateOutOrder.getId()), userId,"取消关闭", "",null);
        return ResultUtil.createSuccessResult("取消关闭成功！", "");
    }

    @Override
    public AllocateOutOrder queryDetail(Long id) {
        AssertUtil.notNull(id, "查询调拨出库单详情信息参数调拨单id不能为空");
        AllocateOutOrder allocateOutOrder = allocateOutOrderService.selectByPrimaryKey(id);
        allocateOrderExtService.setArea(allocateOutOrder);
        AllocateOrderBase allocateOrderBase = allocateOutOrder;
        List<AllocateOrderBase> allocateOrderBaseList = new ArrayList<>();
        allocateOrderBaseList.add(allocateOrderBase);
        allocateOrderExtService.setAllocateOrderOtherNames(allocateOrderBaseList);
        return allocateOutOrder;
    }

    @Override
    public Response outFinishCallBack(WmsAllocateOutInRequest req) {
        AssertUtil.notNull(req, "调拨出库回调信息不能为空");
        String allocateOrderCode = req.getAllocateOrderCode();
        //获取所有调拨出库详情明细
        AllocateSkuDetail allocateSkuDetail = new AllocateSkuDetail();
        allocateSkuDetail.setAllocateOrderCode(allocateOrderCode);
        List<AllocateSkuDetail> allocateSkuDetails = allocateSkuDetailService.select(allocateSkuDetail);

        List<WmsAllocateDetailRequest> wmsAllocateDetailRequests = req.getWmsAllocateDetailRequests();
        if(wmsAllocateDetailRequests != null && wmsAllocateDetailRequests.size() > 0){
            for(WmsAllocateDetailRequest detailRequest : wmsAllocateDetailRequests){
                for(AllocateSkuDetail skuDetail : allocateSkuDetails){
                    if(StringUtils.equals(detailRequest.getSkuCode(), skuDetail.getSkuCode())){
                        skuDetail.setRealOutNum(detailRequest.getRealOutNum());
                        if(detailRequest.getRealOutNum() == skuDetail.getPlanAllocateNum()){
                            skuDetail.setAllocateOutStatus(AllocateOrderEnum.AllocateOrderSkuOutStatusEnum.OUT_NORMAL.getCode());
                            skuDetail.setOutStatus(AllocateOrderEnum.AllocateOutOrderStatusEnum.OUT_SUCCESS.getCode());
                        }else{
                            skuDetail.setAllocateOutStatus(AllocateOrderEnum.AllocateOrderSkuOutStatusEnum.OUT_EXCEPTION.getCode());
                            skuDetail.setOutStatus(AllocateOrderEnum.AllocateOutOrderStatusEnum.OUT_EXCEPTION.getCode());
                        }
                    }
                }
            }
        }
        allocateSkuDetailService.updateSkuDetailList(allocateSkuDetails);
        //更新调拨出库单状态
        AllocateOutOrder allocateOutOrder = new AllocateOutOrder();
        allocateOutOrder.setAllocateOrderCode(allocateOrderCode);
        List<AllocateOutOrder> allocateOutOrders = allocateOutOrderService.select(allocateOutOrder);
        allocateOutOrder = allocateOutOrders.get(0);
        allocateOutOrder.setStatus(getAllocateOutOrderStatusByDetail(allocateSkuDetails));
        allocateOutOrderService.updateByPrimaryKey(allocateOutOrder);
        //更新调拨入库单信息

        //更新调拨单状态
        AllocateOrder allocateOrder = new AllocateOrder();
        allocateOrder.setAllocateOrderCode(allocateOrderCode);
        List<AllocateOrder> allocateOrders = allocateOrderService.select(allocateOrder);
        allocateOrder = allocateOrders.get(0);
        if(StringUtils.equals(allocateOutOrder.getStatus(), AllocateOrderEnum.AllocateOutOrderStatusEnum.OUT_EXCEPTION.getCode())){
            allocateOrder.setInOutStatus(AllocateOrderEnum.AllocateOrderInOutStatusEnum.OUT_EXCEPTION.getCode());
        }else if(StringUtils.equals(allocateOutOrder.getStatus(), AllocateOrderEnum.AllocateOutOrderStatusEnum.OUT_SUCCESS.getCode())){
            allocateOrder.setInOutStatus(AllocateOrderEnum.AllocateOrderInOutStatusEnum.OUT_NORMAL.getCode());
        }
        allocateOrderService.updateByPrimaryKey(allocateOrder);
        return ResultUtil.createSuccessResult("反填调拨出库信息成功！", "");
    }

    //获取状态
    private String getAllocateOutOrderStatusByDetail(List<AllocateSkuDetail> allocateSkuDetails){
        int outFinishNum = 0;//出库完成数
        int outExceptionNum = 0;//出库异常数
        for(AllocateSkuDetail detail : allocateSkuDetails){
            if(StringUtils.equals(AllocateOrderEnum.AllocateOrderSkuOutStatusEnum.OUT_NORMAL.getCode(), detail.getOutStatus()))
                outFinishNum++;
            else if(StringUtils.equals(AllocateOrderEnum.AllocateOrderSkuOutStatusEnum.OUT_EXCEPTION.getCode(), detail.getOutStatus())){
                outExceptionNum++;
            }
        }
        //出库异常：存在“出库异常”的商品时，此处就为出库异常；
        if(outExceptionNum > 0){
            return AllocateOrderEnum.AllocateOutOrderStatusEnum.OUT_EXCEPTION.getCode();
        }
        //出库完成：所有商品的“出库状态”均为“出库完成”，此处就更新为出库完成
        if(outFinishNum == allocateSkuDetails.size()){
            return AllocateOrderEnum.AllocateOutOrderStatusEnum.OUT_SUCCESS.getCode();
        }
        return AllocateOrderEnum.AllocateOutOrderStatusEnum.WAIT_NOTICE.getCode();
    }

    //修改详情状态
    private void updateDetailStatus(String code, String allocateOrderCode, String allocateStatus, boolean cancel){
        AllocateSkuDetail allocateSkuDetail = new AllocateSkuDetail();
        allocateSkuDetail.setAllocateOrderCode(allocateOrderCode);
        List<AllocateSkuDetail> allocateSkuDetails = allocateSkuDetailService.select(allocateSkuDetail);
        List<AllocateSkuDetail> allocateSkuDetailsUpdate = new ArrayList<>();

        if(allocateSkuDetails != null && allocateSkuDetails.size() > 0){
            for(AllocateSkuDetail allocateSkuDetailTemp : allocateSkuDetails){
                if(cancel){
                    allocateSkuDetailTemp.setOutStatus(allocateSkuDetailTemp.getOldOutStatus());
                    allocateSkuDetailTemp.setOldOutStatus("");
                }else{
                    allocateSkuDetailTemp.setOldOutStatus(allocateSkuDetailTemp.getOutStatus());
                    allocateSkuDetailTemp.setOutStatus(code);
                }
                allocateSkuDetailTemp.setAllocateOutStatus(allocateStatus);
                allocateSkuDetailTemp.setUpdateTime(Calendar.getInstance().getTime());
                allocateSkuDetailsUpdate.add(allocateSkuDetailTemp);
            }
            allocateSkuDetailService.updateSkuDetailList(allocateSkuDetailsUpdate);
        }
    }

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Response allocateOrderOutNotice(Long id, AclUserAccreditInfo uerAccredit) {
		AssertUtil.notNull(id, "调拨出库单主键不能为空");
		AllocateOutOrder outOrder = allocateOutOrderService.selectByPrimaryKey(id);
		AssertUtil.notNull(outOrder, "调拨出库单不存在");
		if (!AllocateOutOrderStatusEnum.WAIT_NOTICE.getCode().equals(outOrder.getStatus())
				&& !AllocateOutOrderStatusEnum.OUT_RECEIVE_FAIL.getCode().equals(outOrder.getStatus())) {
			throw new AllocateOutOrderException(ExceptionEnum.ALLOCATE_OUT_ORDER_NOTICE_EXCEPTION, "当前状态不能通知仓库");
		}
		WarehouseInfo whi = new WarehouseInfo();
		whi.setCode(outOrder.getOutWarehouseCode());
		WarehouseInfo warehouse = warehouseInfoService.selectOne(whi);
		AssertUtil.notNull(warehouse, "调出仓库不存在");
		
		List<AllocateSkuDetail> detailList = allocateSkuDetailService.getDetailListByOrderCode(outOrder.getAllocateOrderCode());
		List<ScmAllocateOrderItem> allocateOrderItemList = new ArrayList<>();
		ScmAllocateOrderItem item = null;
		for (AllocateSkuDetail detail : detailList) {
			item = new ScmAllocateOrderItem();
			BeanUtils.copyProperties(detail, item);
			allocateOrderItemList.add(item);
		}
		ScmAllocateOrderOutRequest request = new ScmAllocateOrderOutRequest();
		BeanUtils.copyProperties(outOrder, request);
		request.setAllocateOrderItemList(allocateOrderItemList);
		
		if (OperationalNatureEnum.SELF_SUPPORT.getCode().equals(warehouse.getOperationalNature())) {
			request.setWarehouseType("TRC");
		} else {
			request.setWarehouseType("JD");
		}
		AppResult<ScmAllocateOrderOutResponse> response = warehouseApiService.allocateOrderOutNotice(request);
		
        logInfoService.recordLog(new AllocateOutOrder(), id.toString(), uerAccredit.getUserId(),
                LogOperationEnum.ALLOCATE_ORDER_OUT_NOTICE.getMessage(), null, null);
        
        String status = null;
        String logOp = null;
        String resultMsg = null;
		if (StringUtils.equals(response.getAppcode(), ResponseAck.SUCCESS_CODE)) {
			status = AllocateOutOrderStatusEnum.OUT_RECEIVE_SUCC.getCode();
			logOp = LogOperationEnum.ALLOCATE_ORDER_OUT_NOTICE_FAIL.getMessage();
			resultMsg = "调拨出库通知成功！";
		} else {
			status = AllocateOutOrderStatusEnum.OUT_RECEIVE_FAIL.getCode();
			logOp = LogOperationEnum.ALLOCATE_ORDER_OUT_NOTICE_FAIL.getMessage();
			resultMsg = "调拨出库通知失败！";
		}
        logInfoService.recordLog(new AllocateOutOrder(), id.toString(), warehouse.getWarehouseName(),
        		logOp, null, null);
		
		allocateOutOrderService.updateOutOrderStatusById(status,id);
		allocateSkuDetailService.updateOutSkuStatusByOrderCode(status, outOrder.getAllocateOrderCode());
		return ResultUtil.createSuccessResult(resultMsg, "");
	}

	@Override
	public void orderCancel(String allocateOrderCode, String flag, String cancelReson,
			AclUserAccreditInfo aclUserAccreditInfo) {
		
	}

}
