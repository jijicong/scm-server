package org.trc.biz.impl.allocateOrder;


import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
import org.trc.domain.allocateOrder.AllocateInOrder;
import org.trc.domain.allocateOrder.AllocateOrder;
import org.trc.domain.allocateOrder.AllocateOrderBase;
import org.trc.domain.allocateOrder.AllocateOutOrder;
import org.trc.domain.allocateOrder.AllocateSkuDetail;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.util.Area;
import org.trc.domain.warehouseInfo.WarehouseInfo;
import org.trc.domain.warehouseInfo.WarehouseItemInfo;
import org.trc.enums.AllocateOrderEnum;
import org.trc.enums.AllocateOrderEnum.AllocateOrderInventoryStatusEnum;
import org.trc.enums.AllocateOrderEnum.AllocateOutOrderStatusEnum;
import org.trc.enums.ExceptionEnum;
import org.trc.enums.JdDeliverOrderTypeEnum;
import org.trc.enums.LogOperationEnum;
import org.trc.enums.OperationalNatureEnum;
import org.trc.enums.OrderCancelResultEnum;
import org.trc.enums.ZeroToNineEnum;
import org.trc.enums.allocateOrder.AllocateInOrderStatusEnum;
import org.trc.enums.warehouse.CancelOrderType;
import org.trc.exception.AllocateOutOrderException;
import org.trc.form.JDWmsConstantConfig;
import org.trc.form.AllocateOrder.AllocateOutOrderForm;
import org.trc.form.warehouse.ScmDeliveryOrderDO;
import org.trc.form.warehouse.ScmDeliveryOrderItem;
import org.trc.form.warehouse.ScmOrderCancelRequest;
import org.trc.form.warehouse.ScmOrderCancelResponse;
import org.trc.form.warehouse.allocateOrder.ScmAllocateOrderItem;
import org.trc.form.warehouse.allocateOrder.ScmAllocateOrderOutRequest;
import org.trc.form.warehouse.allocateOrder.ScmAllocateOrderOutResponse;
import org.trc.form.wms.WmsAllocateDetailRequest;
import org.trc.form.wms.WmsAllocateOutInRequest;
import org.trc.service.allocateOrder.IAllocateInOrderService;
import org.trc.service.allocateOrder.IAllocateOrderExtService;
import org.trc.service.allocateOrder.IAllocateOrderService;
import org.trc.service.allocateOrder.IAllocateOutOrderService;
import org.trc.service.allocateOrder.IAllocateSkuDetailService;
import org.trc.service.config.ILogInfoService;
import org.trc.service.jingdong.ICommonService;
import org.trc.service.util.ILocationUtilService;
import org.trc.service.warehouse.IWarehouseApiService;
import org.trc.service.warehouseInfo.IWarehouseInfoService;
import org.trc.service.warehouseInfo.IWarehouseItemInfoService;
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
    private IAllocateInOrderService allocateInOrderService;
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
	@Autowired
    private ICommonService commonService;
    @Autowired
    private JDWmsConstantConfig jDWmsConstantConfig;
    @Autowired
    private ILocationUtilService locationUtilService;
    @Autowired
    private IWarehouseItemInfoService warehouseItemInfoService;

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

        example.setOrderByClause("field(status,2,0) desc");
        example.orderBy("createTime").desc();

        Pagenation<AllocateOutOrder> pagenation = allocateOutOrderService.pagination(example, page, form);

        allocateOrderExtService.setIsTimeOut(pagenation);

        allocateOrderExtService.setAllocateOrderOtherNames(page);
        return pagenation;
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
        allocateSkuDetail.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
        List<AllocateSkuDetail> allocateSkuDetails = allocateSkuDetailService.select(allocateSkuDetail);

        String logMessage = "";
        List<String> exceptionDetail = new ArrayList<>();

        List<WmsAllocateDetailRequest> wmsAllocateDetailRequests = req.getWmsAllocateDetailRequests();
        if(wmsAllocateDetailRequests != null && wmsAllocateDetailRequests.size() > 0){
            for(WmsAllocateDetailRequest detailRequest : wmsAllocateDetailRequests){
                for(AllocateSkuDetail skuDetail : allocateSkuDetails){
                    if(StringUtils.equals(detailRequest.getSkuCode(), skuDetail.getSkuCode())){
                        skuDetail.setRealOutNum(detailRequest.getRealOutNum());
                        if(detailRequest.getRealOutNum().longValue() == skuDetail.getPlanAllocateNum().longValue()){
                            skuDetail.setAllocateOutStatus(AllocateOrderEnum.AllocateOrderSkuOutStatusEnum.OUT_NORMAL.getCode());
                            skuDetail.setOutStatus(AllocateOrderEnum.AllocateOutOrderStatusEnum.OUT_SUCCESS.getCode());
                            skuDetail.setInStatus(AllocateInOrderStatusEnum.OUT_WMS_FINISH.getCode().toString());
                            logMessage += skuDetail.getSkuCode() + ":" + "出库完成<br>";
                        }else{
                            skuDetail.setAllocateOutStatus(AllocateOrderEnum.AllocateOrderSkuOutStatusEnum.OUT_EXCEPTION.getCode());
                            skuDetail.setOutStatus(AllocateOrderEnum.AllocateOutOrderStatusEnum.OUT_EXCEPTION.getCode());
                            skuDetail.setInStatus(AllocateInOrderStatusEnum.OUT_WMS_EXCEPTION.getCode().toString());
                            logMessage += skuDetail.getSkuCode() + ":" + "出库异常<br>";
                            exceptionDetail.add(skuDetail.getSkuCode());
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
        String outStatus = getAllocateOutOrderStatusByDetail(allocateSkuDetails);
        allocateOutOrder.setStatus(outStatus);
        if(exceptionDetail.size() > 0){
            allocateOutOrder.setFailedCause("["+StringUtils.join(exceptionDetail, ",")+"]实际出库数量不等于要求出库数量。");
        }
        allocateOutOrderService.updateByPrimaryKey(allocateOutOrder);
        //更新调拨入库单信息
        AllocateInOrder allocateInOrder = new AllocateInOrder();
        allocateInOrder.setAllocateOrderCode(allocateOrderCode);
        List<AllocateInOrder> allocateInOrders = allocateInOrderService.select(allocateInOrder);
        allocateInOrder = allocateInOrders.get(0);
        if(StringUtils.equals(outStatus, AllocateOrderEnum.AllocateOutOrderStatusEnum.OUT_EXCEPTION.getCode())){
            allocateInOrder.setStatus(AllocateInOrderStatusEnum.OUT_WMS_EXCEPTION.getCode().toString());
        }else if(StringUtils.equals(outStatus, AllocateOutOrderStatusEnum.OUT_SUCCESS.getCode())){
            allocateInOrder.setStatus(AllocateInOrderStatusEnum.OUT_WMS_FINISH.getCode().toString());
        }
        allocateInOrderService.updateByPrimaryKey(allocateInOrder);
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

        //记录日志
        WarehouseInfo warehouseInfo = new WarehouseInfo();
        warehouseInfo.setCode(allocateOutOrder.getOutWarehouseCode());
        warehouseInfo = warehouseInfoService.selectOne(warehouseInfo);
        logInfoService.recordLog(allocateOutOrder, allocateOutOrder.getId().toString(), warehouseInfo.getWarehouseName(),
                LogOperationEnum.ALLOCATE_OUT.getMessage(), logMessage, null);

        logInfoService.recordLog(allocateOrder, allocateOrder.getAllocateOrderCode(), warehouseInfo.getWarehouseName(),
                LogOperationEnum.ALLOCATE_OUT.getMessage(), logMessage, null);

        return ResultUtil.createSuccessResult("反填调拨出库信息成功！", "");
    }

    //获取状态
    private String getAllocateOutOrderStatusByDetail(List<AllocateSkuDetail> allocateSkuDetails){
        int outFinishNum = 0;//出库完成数
        int outExceptionNum = 0;//出库异常数
        for(AllocateSkuDetail detail : allocateSkuDetails){
            if(StringUtils.equals(AllocateOrderEnum.AllocateOutOrderStatusEnum.OUT_SUCCESS.getCode(), detail.getOutStatus()))
                outFinishNum++;
            else if(StringUtils.equals(AllocateOrderEnum.AllocateOutOrderStatusEnum.OUT_EXCEPTION.getCode(), detail.getOutStatus())){
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
        allocateOrderExtService.setArea(outOrder);
        allocateOrderExtService.setDistrictName(outOrder);
        allocateOrderExtService.setAllocateOrderWarehouseName(outOrder);
		if (!AllocateOutOrderStatusEnum.WAIT_NOTICE.getCode().equals(outOrder.getStatus())
				&& !AllocateOutOrderStatusEnum.OUT_RECEIVE_FAIL.getCode().equals(outOrder.getStatus())
					&& !(AllocateOutOrderStatusEnum.CANCEL.getCode().equals(outOrder.getStatus()) 
							&& !DateCheckUtil.checkDate(outOrder.getUpdateTime()))) {
			throw new AllocateOutOrderException(ExceptionEnum.ALLOCATE_OUT_ORDER_NOTICE_EXCEPTION, "当前状态不能通知仓库");
		}
		WarehouseInfo whi = new WarehouseInfo();
		whi.setCode(outOrder.getOutWarehouseCode());
		WarehouseInfo warehouse = warehouseInfoService.selectOne(whi);
		AssertUtil.notNull(warehouse, "调出仓库不存在");
		
		List<AllocateSkuDetail> detailList = allocateSkuDetailService.getDetailListByOrderCode(outOrder.getAllocateOrderCode());
		
		ScmAllocateOrderOutRequest request = new ScmAllocateOrderOutRequest();
		
		String reNoticeOrderCode = null;// 重新发货单号
		
		if (OperationalNatureEnum.SELF_SUPPORT.getCode().equals(warehouse.getOperationalNature())) {// 自营仓逻辑
			List<ScmAllocateOrderItem> allocateOrderItemList = new ArrayList<>();
			ScmAllocateOrderItem item = null;
			for (AllocateSkuDetail detail : detailList) {
				item = new ScmAllocateOrderItem();
				BeanUtils.copyProperties(detail, item);
				allocateOrderItemList.add(item);
			}
			BeanUtils.copyProperties(outOrder, request);
			request.setAllocateOrderItemList(allocateOrderItemList);
	        request.setCreateOperatorName(uerAccredit.getName());
	        request.setCreateOperatorNumber(uerAccredit.getPhone());
			request.setWarehouseType("TRC");
			
		} else {
			// 京东调拨单采用发货单的逻辑
			List<ScmDeliveryOrderDO> scmDeleveryOrderDOList = new ArrayList<>();
			ScmDeliveryOrderDO orderDo = new ScmDeliveryOrderDO();
			if (StringUtils.isNotBlank(outOrder.getWmsAllocateOutOrderCode())) { // 重新发货
				Integer orderSeq = (outOrder.getOutOrderSeq() == null ? 0 : outOrder.getOutOrderSeq()) + 1;
				reNoticeOrderCode = outOrder.getAllocateOutOrderCode() + "_" + orderSeq;
				orderDo.setDeliveryOrderCode(reNoticeOrderCode);//调拨出库单号
			} else {
				orderDo.setDeliveryOrderCode(outOrder.getAllocateOutOrderCode());//调拨出库单号
			}
			
			// 商品详情
			List<ScmDeliveryOrderItem> itemList = new ArrayList<>();
			ScmDeliveryOrderItem item = null;
			List<String> skuCodeList = detailList.stream().map(
					detail -> detail.getSkuCode()).collect(Collectors.toList());
	        Example example = new Example(WarehouseItemInfo.class);
	        Example.Criteria ca = example.createCriteria();
	        ca.andIn("skuCode", skuCodeList);
	        List<WarehouseItemInfo> warehouseItemInfoList = warehouseItemInfoService.selectByExample(example);
	        for (AllocateSkuDetail detail : detailList) {
	        	if (AllocateOrderInventoryStatusEnum.Quality.getCode().equals(detail.getInventoryType())) {
	        		throw new AllocateOutOrderException(ExceptionEnum.ALLOCATE_OUT_ORDER_NOTICE_EXCEPTION, "京东仓暂时不允许残品调拨"); 
	        	}
	        	for (WarehouseItemInfo info : warehouseItemInfoList) {
					if (StringUtils.equals(info.getSkuCode(), detail.getSkuCode())) {
						item = new ScmDeliveryOrderItem();
						item.setItemId(info.getWarehouseItemId());
						item.setPlanQty(detail.getPlanAllocateNum());
						itemList.add(item);
						break;
					}
				}
			}
	        orderDo.setScmDeleveryOrderItemList(itemList);
			
			orderDo.setIsvSource(jDWmsConstantConfig.getIsvSource());//开放平台的ISV编号，编号线下获取
			orderDo.setOwnerCode(jDWmsConstantConfig.getDeptNo());// 开放平台事业部编号
			orderDo.setShopNo(jDWmsConstantConfig.getShopNo());// 店铺编号
			orderDo.setWarehouseCode(warehouse.getWmsWarehouseCode());//开放平台库房编号
			orderDo.setShipperNo(jDWmsConstantConfig.getShipperNo()); // 承运商编号
			orderDo.setSalePlatformSource(jDWmsConstantConfig.getSalePlatformSource());//销售平台编号
			orderDo.setReciverName(outOrder.getReceiver());//客户姓名
			orderDo.setReciverMobile(outOrder.getReceiverMobile());//客户手机
			orderDo.setOrderType(JdDeliverOrderTypeEnum.B2B.getCode()); // b2b
			
			orderDo.setReciverProvince(getAreaName(warehouse.getProvince(), "Province"));// 省
			orderDo.setReciverCity(getAreaName(warehouse.getCity(), "City"));// 城市
			orderDo.setReciverCountry(getAreaName(warehouse.getArea(), "District"));// 区
			orderDo.setReciverDetailAddress(warehouse.getAddress()); // 地址取自仓库
			orderDo.setOrderMark(jDWmsConstantConfig.getOrderMark());// 订单标记位
			
			scmDeleveryOrderDOList.add(orderDo);
			request.setWarehouseType("JD");
			request.setScmDeleveryOrderDOList(scmDeleveryOrderDOList);
		}
		AppResult<ScmAllocateOrderOutResponse> response = warehouseApiService.allocateOrderOutNotice(request);

        logInfoService.recordLog(new AllocateOutOrder(), id.toString(), uerAccredit.getUserId(),
                LogOperationEnum.ALLOCATE_ORDER_OUT_NOTICE.getMessage(), reNoticeOrderCode, null);
        
        String status = null;
        String logOp = null;
        String resultMsg = null;
        String errMsg = null;
        String wmsAllocatOutCode = null;
        Integer orderSeq = null;
		if (StringUtils.equals(response.getAppcode(), ResponseAck.SUCCESS_CODE)) {
			status = AllocateOutOrderStatusEnum.OUT_RECEIVE_SUCC.getCode();
			logOp = LogOperationEnum.ALLOCATE_ORDER_OUT_NOTICE_SUCC.getMessage();
			ScmAllocateOrderOutResponse rep = (ScmAllocateOrderOutResponse) response.getResult();
			wmsAllocatOutCode = rep.getWmsAllocateOrderOutCode();
			orderSeq = (outOrder.getOutOrderSeq() == null ? 0 : outOrder.getOutOrderSeq()) + 1;
			resultMsg = "调拨出库通知成功！";
		} else {
			status = AllocateOutOrderStatusEnum.OUT_RECEIVE_FAIL.getCode();
			logOp = LogOperationEnum.ALLOCATE_ORDER_OUT_NOTICE_FAIL.getMessage();
			errMsg = response.getDatabuffer();
			resultMsg = "调拨出库通知失败！";
		}
        logInfoService.recordLog(new AllocateOutOrder(), id.toString(), warehouse.getWarehouseName(),
        		logOp, errMsg, null);
		
		allocateOutOrderService.updateOutOrderById(status, id, errMsg, wmsAllocatOutCode, orderSeq);
		allocateSkuDetailService.updateOutSkuStatusByOrderCode(status, outOrder.getAllocateOrderCode());
		return ResultUtil.createSuccessResult(resultMsg, "");
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private String getAreaName (String code, String areaFiledName) {
		try {
			Area area = new Area();
	        area.setCode(code);
	        area = locationUtilService.selectOne(area);
	        Class cls = Area.class;
	        Method md = cls.getDeclaredMethod("get" + areaFiledName);
	        
	        return (String) md.invoke(area);
		} catch (Exception e) {
			logger.error("getAreaName error :", e);
			return "";
		}

	}
	
	@Override
	public Response closeOrCancel(Long id, String remark, AclUserAccreditInfo aclUserAccreditInfo, boolean isClose) {
        AssertUtil.notNull(id, "调拨出库单主键不能为空");
        AssertUtil.notBlank(remark, "关闭原因不能为空");

        //获取出库单信息
        AllocateOutOrder allocateOutOrder = allocateOutOrderService.selectByPrimaryKey(id);
        // 商品详情初始化为已取消状态
        String skuDatailStatus = AllocateOutOrderStatusEnum.CANCEL.getCode();
        
        if(isClose){ // 手工关闭
            if(!StringUtils.equals(allocateOutOrder.getStatus(), AllocateOrderEnum.AllocateOutOrderStatusEnum.WAIT_NOTICE.getCode()) &&
                    !StringUtils.equals(allocateOutOrder.getStatus(), AllocateOrderEnum.AllocateOutOrderStatusEnum.OUT_RECEIVE_FAIL.getCode())){
                String msg = "调拨出库通知单状态必须为出库仓接收失败或待通知出库!";
                logger.error(msg);
                throw new AllocateOutOrderException(ExceptionEnum.ALLOCATE_OUT_ORDER_CLOSE_EXCEPTION, msg);
            }
        }else{// 取消出库
            if(!StringUtils.equals(allocateOutOrder.getStatus(), AllocateOrderEnum.AllocateOutOrderStatusEnum.OUT_RECEIVE_SUCC.getCode())){
                String msg = "调拨出库通知单状态必须为出库仓接收成功!";
                logger.error(msg);
                throw new AllocateOutOrderException(ExceptionEnum.ALLOCATE_OUT_ORDER_CLOSE_EXCEPTION, msg);
            }
            Map<String, String> map = new HashMap<>();
            OrderCancelResultEnum resultEnum = wmsCancelNotice(allocateOutOrder, map);
            if (OrderCancelResultEnum.CANCEL_FAIL.code.equals(resultEnum.code)) {
                throw new RuntimeException("调拨出库单取消失败:" + map.get("msg"));
            } else if (OrderCancelResultEnum.CANCELLING.code.equals(resultEnum.code)) {
            	skuDatailStatus = AllocateOutOrderStatusEnum.CANCELLING.getCode(); // 取消中
            }
        }

        //修改状态
        this.updateDetailStatus(skuDatailStatus, allocateOutOrder.getAllocateOrderCode(), 
        		AllocateOrderEnum.AllocateOrderSkuOutStatusEnum.WAIT_OUT.getCode(), false);

        allocateOrderExtService.updateOrderCancelInfo(allocateOutOrder, remark, isClose, skuDatailStatus);

        String userId = aclUserAccreditInfo.getUserId();
        if(isClose){
            logInfoService.recordLog(allocateOutOrder, String.valueOf(allocateOutOrder.getId()),userId,"手工关闭", remark,null);
            return ResultUtil.createSuccessResult("调拨出库通知单关闭成功！", "");
        }else{
            logInfoService.recordLog(allocateOutOrder, String.valueOf(allocateOutOrder.getId()),userId,"取消出库", remark,null);
            return ResultUtil.createSuccessResult("调拨出库通知单取消成功！", "");
        }
	}

    /**
     * 出库单取消通知
     */
    private OrderCancelResultEnum wmsCancelNotice (AllocateOutOrder outOder, Map<String, String> errMsg) {
    	OrderCancelResultEnum resultEnum = OrderCancelResultEnum.CANCEL_FAIL;// 取消失败
    	ScmOrderCancelRequest request = new ScmOrderCancelRequest();
        request.setOrderType(CancelOrderType.ALLOCATE_OUT.getCode());
        // 自营仓 和 三方仓库 统一取WmsAllocateOutOrderCode
        request.setOrderCode(outOder.getWmsAllocateOutOrderCode());
        commonService.getWarehoueType(outOder.getOutWarehouseCode(), request);

        AppResult<ScmOrderCancelResponse> response = warehouseApiService.orderCancel(request);
        if (StringUtils.equals(response.getAppcode(), ResponseAck.SUCCESS_CODE)) {
            ScmOrderCancelResponse respResult = (ScmOrderCancelResponse)response.getResult();
            if (OrderCancelResultEnum.CANCEL_SUCC.code.equals(respResult.getFlag())) { // 取消成功
            	resultEnum = OrderCancelResultEnum.CANCEL_SUCC;
            } else if (OrderCancelResultEnum.CANCELLING.code.equals(respResult.getFlag())) { // 取消中
            	resultEnum = OrderCancelResultEnum.CANCELLING;
            } else {
            	errMsg.put("msg", respResult.getMessage());
            }
        } else {
        	errMsg.put("msg", response.getDatabuffer());
        	logger.error("调拨出库单取消失败:{}", response.getDatabuffer());
        }
        return resultEnum;
    }

}
