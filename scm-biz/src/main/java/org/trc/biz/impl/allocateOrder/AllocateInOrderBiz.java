package org.trc.biz.impl.allocateOrder;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.trc.biz.allocateOrder.IAllocateInOrderBiz;
import org.trc.domain.allocateOrder.AllocateInOrder;
import org.trc.domain.allocateOrder.AllocateOrder;
import org.trc.domain.allocateOrder.AllocateOrderBase;
import org.trc.domain.allocateOrder.AllocateSkuDetail;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.warehouseInfo.WarehouseInfo;
import org.trc.enums.AllocateOrderEnum;
import org.trc.enums.CommonExceptionEnum;
import org.trc.enums.LogOperationEnum;
import org.trc.enums.ZeroToNineEnum;
import org.trc.enums.allocateOrder.AllocateInOrderStatusEnum;
import org.trc.enums.warehouse.CancelOrderType;
import org.trc.exception.ParamValidException;
import org.trc.form.AllocateOrder.AllocateInOrderForm;
import org.trc.form.AllocateOrder.AllocateInOrderParamForm;
import org.trc.form.warehouse.ScmOrderCancelRequest;
import org.trc.form.warehouse.ScmOrderCancelResponse;
import org.trc.form.warehouse.allocateOrder.ScmAllocateOrderInRequest;
import org.trc.form.warehouse.allocateOrder.ScmAllocateOrderInResponse;
import org.trc.form.wms.WmsAllocateDetailRequest;
import org.trc.form.wms.WmsAllocateOutInRequest;
import org.trc.service.allocateOrder.IAllocateInOrderService;
import org.trc.service.allocateOrder.IAllocateOrderExtService;
import org.trc.service.allocateOrder.IAllocateOrderService;
import org.trc.service.allocateOrder.IAllocateSkuDetailService;
import org.trc.service.config.ILogInfoService;
import org.trc.service.jingdong.ICommonService;
import org.trc.service.warehouse.IWarehouseApiService;
import org.trc.service.warehouseInfo.IWarehouseInfoService;
import org.trc.util.AppResult;
import org.trc.util.AssertUtil;
import org.trc.util.DateCheckUtil;
import org.trc.util.DateUtils;
import org.trc.util.Pagenation;
import org.trc.util.ResponseAck;
import org.trc.util.ResultUtil;

import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.StringUtil;

@Service("allocateInOrderBiz")
public class AllocateInOrderBiz implements IAllocateInOrderBiz {
	
	private Logger logger = LoggerFactory.getLogger(AllocateInOrderBiz.class);
    @Autowired
    private IAllocateOrderExtService allocateOrderExtService;
    @Autowired
    private IAllocateInOrderService allocateInOrderService;
    @Autowired
    private IAllocateSkuDetailService allocateSkuDetailService;
    @Autowired
    private ILogInfoService logInfoService;
    @Autowired
    private IWarehouseInfoService warehouseInfoService;
    @Autowired
    private IWarehouseApiService warehouseApiService;
    @Autowired
    private IAllocateOrderService allocateOrderService;
    @Autowired
    private ICommonService commonService;

    @Override
    public Pagenation<AllocateInOrder> allocateInOrderPage(AllocateInOrderForm form, Pagenation<AllocateInOrder> page) {
        Example example = new Example(AllocateInOrder.class);
        Example.Criteria criteria = example.createCriteria();
        if (StringUtil.isNotEmpty(form.getAllocateOrderCode())) {//调拨单编号
            criteria.andLike("allocateOrderCode", "%" + form.getAllocateOrderCode() + "%");
        }
        if (StringUtil.isNotEmpty(form.getAllocateInOrderCode())) {//调拨入库单号
            criteria.andLike("allocateInOrderCode", "%" + form.getAllocateInOrderCode() + "%");
        }
        if (StringUtil.isNotEmpty(form.getInWarehouseCode())) {//调入仓库
            criteria.andEqualTo("inWarehouseCode", form.getInWarehouseCode() );
        }
        if (StringUtil.isNotEmpty(form.getStatus())) {//入库单状态
            criteria.andEqualTo("status", form.getStatus());
        }
        if (StringUtil.isNotEmpty(form.getCreateOperatorName())) {//入库单创建人
            allocateOrderExtService.setCreateOperator(form.getCreateOperatorName(), criteria);
        }
        if (StringUtil.isNotEmpty(form.getStartDate())) {//开始日期
            criteria.andGreaterThanOrEqualTo("createTime", DateUtils.parseDate(form.getStartDate()));
        }
        if (StringUtil.isNotEmpty(form.getEndDate())) {//截止日期
            Date endDate = DateUtils.parseDate(form.getEndDate());
            criteria.andLessThan("createTime", DateUtils.addDays(endDate, 1));
        }
        example.setOrderByClause("instr('0,2,1,4',`status`) DESC");
        example.orderBy("createTime").desc();
        page = allocateInOrderService.pagination(example, page, form);
        allocateOrderExtService.setAllocateOrderOtherNames(page);
        allocateOrderExtService.setIsTimeOut(page);
        return page;
    }

    @Override
    public AllocateInOrder queryDetail(String allocateOrderCode) {
        AssertUtil.notBlank(allocateOrderCode, "查询调拨入库单明细信息参数调拨单编码allocateOrderCode不能为空");
        AllocateInOrder allocateInOrder = new AllocateInOrder();
        allocateInOrder.setAllocateOrderCode(allocateOrderCode);
        allocateInOrder = allocateInOrderService.selectOne(allocateInOrder);
        AssertUtil.notNull(allocateInOrder, String.format("查询调拨单[%s]信息为空", allocateOrderCode));
        AllocateSkuDetail record = new AllocateSkuDetail();
        record.setAllocateOrderCode(allocateOrderCode);
        record.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
        List<AllocateSkuDetail> allocateSkuDetailList = allocateSkuDetailService.select(record);
        AssertUtil.notEmpty(allocateSkuDetailList, String.format("查询调拨单[%s]明细为空", allocateOrderCode));
        allocateInOrder.setSkuDetailList(allocateSkuDetailList);
        AllocateOrderBase allocateOrderBase = allocateInOrder;
        List<AllocateOrderBase> allocateOrderBaseList = new ArrayList<>();
        allocateOrderBaseList.add(allocateOrderBase);
        allocateOrderExtService.setAllocateOrderOtherNames(allocateOrderBaseList);
        allocateOrderExtService.setArea(allocateOrderBase);
        return allocateInOrder;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void orderCancel(String allocateOrderCode, String flag, String cancelReson, AclUserAccreditInfo aclUserAccreditInfo) {
        AssertUtil.notBlank(allocateOrderCode, "参数调拨单号allocateOrderCode不能为空");
        AssertUtil.notBlank(flag, "参数操作类型flag不能为空");
        if(StringUtils.equals(ZeroToNineEnum.ZERO.getCode(), flag)){//取消收货操作
            AssertUtil.notBlank(cancelReson, "参数关闭原因cancelReson不能为空");
        }
        AllocateInOrderParamForm form = allocateOrderExtService.updateAllocateInOrderByCancel(allocateOrderCode, ZeroToNineEnum.ONE.getCode(), flag, cancelReson);
        
        LogOperationEnum logOperationEnum = null;
        if (StringUtils.equals(ZeroToNineEnum.ZERO.getCode(), flag)) {//取消收货
            logOperationEnum = LogOperationEnum.CANCEL_RECIVE_GOODS;
            Map<String, String> map = new HashMap<>();
            if (!wmsCancelNotice(form.getAllocateInOrder(), map)) {
            	throw new RuntimeException("调拨入库单取消失败" +map.get("msg"));
            }
        } else if (StringUtils.equals(ZeroToNineEnum.ONE.getCode(), flag)) {//重新收货
        	
            if (AllocateInOrderStatusEnum.CANCEL.getCode().equals(form.getAllocateInOrder().getStatus())
            		&& DateCheckUtil.checkDate(form.getAllocateInOrder().getUpdateTime())) {
            	throw new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, "当前调拨入单取消时间过长，不能重新收货");
            }
            logOperationEnum = LogOperationEnum.RE_RECIVE_GOODS;
            wmsAllocateOrderInNotice(form.getAllocateInOrder(), aclUserAccreditInfo, false);
//            if (!wmsAllocateOrderInNotice(form.getAllocateInOrder(), aclUserAccreditInfo, false)) {
//            	throw new RuntimeException("调拨入库单重新收货失败");
//            }
        }
        
        //记录操作日志
        logInfoService.recordLog(form.getAllocateInOrder(),form.getAllocateInOrder().getId().toString(), aclUserAccreditInfo.getUserId(), logOperationEnum.getMessage(), cancelReson,null);
    }

    @Override
    public void orderClose(String allocateOrderCode, String flag, String cancelReson, AclUserAccreditInfo aclUserAccreditInfo) {
        AssertUtil.notBlank(allocateOrderCode, "参数调拨单号allocateOrderCode不能为空");
        AssertUtil.notBlank(flag, "参数操作类型flag不能为空");
        if(StringUtils.equals(ZeroToNineEnum.ZERO.getCode(), flag)){//关闭操作
            AssertUtil.notBlank(cancelReson, "参数关闭原因cancelReson不能为空");
        }
        AllocateInOrderParamForm form = allocateOrderExtService.updateAllocateInOrderByCancel(allocateOrderCode, ZeroToNineEnum.ZERO.getCode(), flag, cancelReson);
        LogOperationEnum logOperationEnum = null;
        if(StringUtils.equals(ZeroToNineEnum.ZERO.getCode(), flag)){//关闭
            logOperationEnum = LogOperationEnum.HAND_CLOSE;
        }else if(StringUtils.equals(ZeroToNineEnum.ONE.getCode(), flag)){//取消关闭
            logOperationEnum = LogOperationEnum.CANCEL_CLOSE;
        }
        //记录操作日志
        logInfoService.recordLog(form.getAllocateInOrder(),form.getAllocateInOrder().getId().toString(), aclUserAccreditInfo.getUserId(), logOperationEnum.getMessage(), cancelReson,null);
    }
    
    /**
     * 入库单取消通知
     */
    private boolean wmsCancelNotice (AllocateInOrder inOder,  Map<String, String> errMsg) {
    	boolean succ = false;
    	ScmOrderCancelRequest request = new ScmOrderCancelRequest();
    	request.setOrderType(CancelOrderType.ALLOCATE_IN.getCode());
    	request.setAllocateInOrderCode(inOder.getAllocateInOrderCode());
		//BeanUtils.copyProperties(allocateInOrder, request);
        commonService.getWarehoueType(inOder.getInWarehouseCode(), request);
    	
    	AppResult<ScmOrderCancelResponse> response = warehouseApiService.orderCancel(request);
//        String status = null;
		if (StringUtils.equals(response.getAppcode(), ResponseAck.SUCCESS_CODE)) {
			ScmOrderCancelResponse respResult = (ScmOrderCancelResponse)response.getResult();
			if (ZeroToNineEnum.ONE.getCode().equals(respResult.getFlag())) { // 取消成功
//				status = AllocateInOrderStatusEnum.CANCEL.getCode().toString();
				succ = true;
			}
		} else {
			errMsg.put("msg", response.getDatabuffer());
			logger.error("调拨入库单取消失败:", response.getDatabuffer());
		}
		//allocateInOrderService.updateOutOrderStatusById(status, inOder.getId());
		//allocateSkuDetailService.updateOutSkuStatusByOrderCode(status, inOder.getAllocateOrderCode());
    	return succ;
    	
     }
    
    /**
     * 入库单通知
     * @param allocateInOrder
     * @param aclUserAccreditInfo
     */
    private boolean wmsAllocateOrderInNotice (AllocateInOrder allocateInOrder, AclUserAccreditInfo aclUserAccreditInfo, boolean needUpdate) {
		boolean succ = false;

        allocateOrderExtService.setArea(allocateInOrder);
        allocateOrderExtService.setDistrictName(allocateInOrder);
        allocateOrderExtService.setAllocateOrderWarehouseName(allocateInOrder);

		ScmAllocateOrderInRequest request = new ScmAllocateOrderInRequest();
		BeanUtils.copyProperties(allocateInOrder, request);
        request.setCreateOperatorName(aclUserAccreditInfo.getName());
        request.setCreateOperatorNumber(aclUserAccreditInfo.getPhone());

		String whName = commonService.getWarehoueType(allocateInOrder.getInWarehouseCode(), request);
	
		AppResult<ScmAllocateOrderInResponse> response = warehouseApiService.allocateOrderInNotice(request);
        
        //记录操作日志
        logInfoService.recordLog(allocateInOrder,allocateInOrder.getId().toString(),
        		aclUserAccreditInfo.getUserId(), LogOperationEnum.NOTICE_RECIVE_GOODS.getMessage(), "",null);

        String status = null;
        String logOp = null;
        String resultMsg = null;
        String errMsg = null;
		if (StringUtils.equals(response.getAppcode(), ResponseAck.SUCCESS_CODE)) {
			status = AllocateInOrderStatusEnum.RECIVE_WMS_RECIVE_SUCCESS.getCode().toString();
			logOp = LogOperationEnum.ALLOCATE_ORDER_IN_NOTICE_SUCC.getMessage();
			resultMsg = "调拨入库通知成功！";
			succ = true;
		} else {
			status = AllocateInOrderStatusEnum.RECIVE_WMS_RECIVE_FAILURE.getCode().toString();
			logOp = LogOperationEnum.ALLOCATE_ORDER_IN_NOTICE_FAIL.getMessage();
			resultMsg = "调拨入库通知失败！";
			errMsg = response.getDatabuffer();
		}
//		if (needUpdate) {
			allocateInOrderService.updateInOrderStatusById(status, allocateInOrder.getId(), errMsg);
			allocateSkuDetailService.updateInSkuStatusByOrderCode(status, allocateInOrder.getAllocateOrderCode());
//		}
        logInfoService.recordLog(new AllocateInOrder(), allocateInOrder.getId().toString(), whName,
        		logOp, errMsg, null);
        return succ;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void noticeReciveGoods(String allocateOrderCode, AclUserAccreditInfo aclUserAccreditInfo) {
        AssertUtil.notBlank(allocateOrderCode, "参数调拨单号allocateOrderCode不能为空");
        AllocateInOrder allocateInOrder = new AllocateInOrder();
        allocateInOrder.setAllocateOrderCode(allocateOrderCode);
        allocateInOrder = allocateInOrderService.selectOne(allocateInOrder);
        AssertUtil.notNull(allocateInOrder, String.format("根据调拨单号%s查询调拨入库单信息为空", allocateInOrder));
        //校验订单是否已经是取消状态
        if(StringUtils.equals(AllocateInOrderStatusEnum.CANCEL.getCode().toString(), allocateInOrder.getStatus())){
            throw new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, "调拨单当前已经是取消状态！请刷新页面查看最新数据！");
        }
        //FIXME 调用仓库接口逻辑待实现
        wmsAllocateOrderInNotice(allocateInOrder, aclUserAccreditInfo, true);
        
        
    }

    @Override
    public Response inFinishCallBack(WmsAllocateOutInRequest req) {
        AssertUtil.notNull(req, "调拨入库回调信息不能为空");
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
                        skuDetail.setDefectInNum(detailRequest.getDefectInNum());
                        skuDetail.setNornalInNum(detailRequest.getNornalInNum());
                        Long realInNum = detailRequest.getNornalInNum() + detailRequest.getDefectInNum();
                        skuDetail.setRealInNum(realInNum);
                        if(skuDetail.getRealOutNum().longValue() == skuDetail.getNornalInNum().longValue() &&
                                skuDetail.getDefectInNum().longValue() == 0){
                            skuDetail.setAllocateInStatus(AllocateOrderEnum.AllocateOrderSkuInStatusEnum.IN_NORMAL.getCode());
                            skuDetail.setInStatus(String.valueOf(AllocateInOrderStatusEnum.IN_WMS_FINISH.getCode()));
                            logMessage += skuDetail.getSkuCode() + ":" + "入库完成<br>";
                        }else{
                            skuDetail.setAllocateInStatus(AllocateOrderEnum.AllocateOrderSkuInStatusEnum.IN_EXCEPTION.getCode());
                            skuDetail.setInStatus(String.valueOf(AllocateInOrderStatusEnum.IN_WMS_EXCEPTION.getCode()));
                            logMessage += skuDetail.getSkuCode() + ":" + "入库异常<br>";
                            exceptionDetail.add(skuDetail.getSkuCode());
                        }
                    }
                }
            }
        }
        allocateSkuDetailService.updateSkuDetailList(allocateSkuDetails);
        //更新调拨入库单状态
        AllocateInOrder allocateInOrder = new AllocateInOrder();
        allocateInOrder.setAllocateOrderCode(allocateOrderCode);
        List<AllocateInOrder> allocateInOrders = allocateInOrderService.select(allocateInOrder);
        allocateInOrder = allocateInOrders.get(0);
        if(StringUtils.isNotEmpty(getAllocateInOrderStatusByDetail(allocateSkuDetails))){
            allocateInOrder.setStatus(getAllocateInOrderStatusByDetail(allocateSkuDetails));
        }
        if(exceptionDetail.size() > 0){
            allocateInOrder.setFailedCause("["+StringUtils.join(exceptionDetail, ",")+"]实际入库信息与实际出库信息不符。");
        }
        allocateInOrderService.updateByPrimaryKey(allocateInOrder);
        //更新调拨单状态
        AllocateOrder allocateOrder = new AllocateOrder();
        allocateOrder.setAllocateOrderCode(allocateOrderCode);
        List<AllocateOrder> allocateOrders = allocateOrderService.select(allocateOrder);
        allocateOrder = allocateOrders.get(0);
        if(StringUtils.equals(allocateInOrder.getStatus(), String.valueOf(AllocateInOrderStatusEnum.IN_WMS_EXCEPTION.getCode()))){
            allocateOrder.setInOutStatus(AllocateOrderEnum.AllocateOrderInOutStatusEnum.IN_EXCEPTION.getCode());
        }else if(StringUtils.equals(allocateInOrder.getStatus(), String.valueOf(AllocateInOrderStatusEnum.IN_WMS_FINISH.getCode()))){
            allocateOrder.setInOutStatus(AllocateOrderEnum.AllocateOrderInOutStatusEnum.IN_NORMAL.getCode());
        }
        allocateOrderService.updateByPrimaryKey(allocateOrder);

        //记录日志
        WarehouseInfo warehouseInfo = new WarehouseInfo();
        warehouseInfo.setCode(allocateInOrder.getInWarehouseCode());
        warehouseInfo = warehouseInfoService.selectOne(warehouseInfo);
        logInfoService.recordLog(allocateInOrder, allocateInOrder.getId().toString(), warehouseInfo.getWarehouseName(),
                LogOperationEnum.ALLOCATE_IN.getMessage(), logMessage, null);

        logInfoService.recordLog(allocateOrder, allocateOrder.getAllocateOrderCode(), warehouseInfo.getWarehouseName(),
                LogOperationEnum.ALLOCATE_IN.getMessage(), logMessage, null);

        return ResultUtil.createSuccessResult("反填调拨入库信息成功！", "");
    }

    //获取状态
    private String getAllocateInOrderStatusByDetail(List<AllocateSkuDetail> allocateSkuDetails){
        int inFinishNum = 0;//出库完成数
        int inExceptionNum = 0;//出库异常数
        for(AllocateSkuDetail detail : allocateSkuDetails){
            if(StringUtils.equals(AllocateInOrderStatusEnum.IN_WMS_FINISH.getCode().toString(), detail.getInStatus()))
                inFinishNum++;
            else if(StringUtils.equals(AllocateInOrderStatusEnum.IN_WMS_EXCEPTION.getCode().toString(), detail.getInStatus())){
                inExceptionNum++;
            }
        }
        //入库异常：存在“入库异常”的商品时，此处就为入库异常；
        if(inExceptionNum > 0){
            return String.valueOf(AllocateInOrderStatusEnum.IN_WMS_EXCEPTION.getCode());
        }
        //入库完成：所有商品的“入库状态”均为“入库完成”，此处就更新为入库完成
        if(inFinishNum == allocateSkuDetails.size()){
            return String.valueOf(AllocateInOrderStatusEnum.IN_WMS_FINISH.getCode());
        }
        return  "";
    }
}
