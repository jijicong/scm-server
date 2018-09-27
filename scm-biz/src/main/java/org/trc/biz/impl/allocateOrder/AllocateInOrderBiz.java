package org.trc.biz.impl.allocateOrder;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.trc.biz.allocateOrder.IAllocateInOrderBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.allocateOrder.AllocateInOrder;
import org.trc.domain.allocateOrder.AllocateOrder;
import org.trc.domain.allocateOrder.AllocateOrderBase;
import org.trc.domain.allocateOrder.AllocateSkuDetail;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.stock.JdStockInDetail;
import org.trc.domain.warehouseInfo.WarehouseInfo;
import org.trc.domain.warehouseInfo.WarehouseItemInfo;
import org.trc.enums.*;
import org.trc.enums.allocateOrder.AllocateInOrderStatusEnum;
import org.trc.enums.report.StockOperationTypeEnum;
import org.trc.enums.warehouse.CancelOrderType;
import org.trc.exception.ParamValidException;
import org.trc.form.AllocateOrder.AllocateInOrderForm;
import org.trc.form.AllocateOrder.AllocateInOrderParamForm;
import org.trc.form.JDWmsConstantConfig;
import org.trc.form.warehouse.*;
import org.trc.form.warehouse.allocateOrder.ScmAllocateOrderInRequest;
import org.trc.form.warehouse.allocateOrder.ScmAllocateOrderInResponse;
import org.trc.form.warehouse.allocateOrder.ScmAllocateOrderItem;
import org.trc.form.wms.WmsAllocateDetailRequest;
import org.trc.form.wms.WmsAllocateOutInRequest;
import org.trc.service.allocateOrder.IAllocateInOrderService;
import org.trc.service.allocateOrder.IAllocateOrderExtService;
import org.trc.service.allocateOrder.IAllocateOrderService;
import org.trc.service.allocateOrder.IAllocateSkuDetailService;
import org.trc.service.config.ILogInfoService;
import org.trc.service.jingdong.ICommonService;
import org.trc.service.stock.IJdStockInDetailService;
import org.trc.service.util.IRealIpService;
import org.trc.service.warehouse.IWarehouseApiService;
import org.trc.service.warehouse.IWarehouseExtService;
import org.trc.service.warehouse.IWarehouseMockService;
import org.trc.service.warehouseInfo.IWarehouseInfoService;
import org.trc.service.warehouseInfo.IWarehouseItemInfoService;
import org.trc.util.*;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.StringUtil;

import javax.ws.rs.core.Response;
import java.util.*;
import java.util.stream.Collectors;

import static org.trc.biz.impl.allocateOrder.AllocateOutOrderBiz.SUCCESS;

@Service("allocateInOrderBiz")
public class AllocateInOrderBiz implements IAllocateInOrderBiz {
	
	private Logger logger = LoggerFactory.getLogger(AllocateInOrderBiz.class);

    @Value("${mock.outer.interface}")
    private String mockOuterInterface;

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
    @Autowired
    private JDWmsConstantConfig jDWmsConstantConfig;
    @Autowired
    private IWarehouseExtService warehouseExtService;
    @Autowired
    private IWarehouseItemInfoService warehouseItemInfoService;
    @Autowired
    private IRealIpService realIpService;
    @Autowired
    private IWarehouseMockService warehouseMockService;
    @Autowired
    private IJdStockInDetailService jdStockInDetailService;
    

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
        
        AllocateInOrder allocateInOrder = new AllocateInOrder();
        allocateInOrder.setAllocateOrderCode(allocateOrderCode);
        allocateInOrder = allocateInOrderService.selectOne(allocateInOrder);
        AssertUtil.notNull(allocateInOrder, String.format("根据调拨单号%s查询调拨入库单信息为空", allocateOrderCode));
        
        AllocateSkuDetail allocateSkuDetail = new AllocateSkuDetail();
        allocateSkuDetail.setAllocateOrderCode(allocateOrderCode);
        List<AllocateSkuDetail> allocateSkuDetailList = allocateSkuDetailService.select(allocateSkuDetail);
        AssertUtil.notEmpty(allocateSkuDetailList, String.format("根据调拨单号%s查询调拨入库单明细信息为空", allocateInOrder));
        
        // 取消结果
        String cancelResult = "";
        LogOperationEnum logOperationEnum = null;
        if (StringUtils.equals(ZeroToNineEnum.ZERO.getCode(), flag)) {//取消收货
            
            if(StringUtils.equals(AllocateInOrderStatusEnum.CANCEL.getCode().toString(), allocateInOrder.getStatus())){
                throw new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, "调拨单当前已经是取消状态!");
            }
            
            logOperationEnum = LogOperationEnum.CANCEL_RECIVE_GOODS;
            
            Map<String, String> map = new HashMap<>();
            
            OrderCancelResultEnum resultEnum = wmsCancelNotice(allocateInOrder, map);
            
            if (OrderCancelResultEnum.CANCEL_FAIL.code.equals(resultEnum.code)) {
                throw new RuntimeException("调拨入库单取消失败:" + map.get("msg"));
            } else if (OrderCancelResultEnum.CANCELLING.code.equals(resultEnum.code)) {
            	cancelResult = AllocateInOrderStatusEnum.CANCELLING.getCode().toString(); // 取消中
            } else if (OrderCancelResultEnum.CANCEL_SUCC.code.equals(resultEnum.code)) {
            	cancelResult = AllocateInOrderStatusEnum.CANCEL.getCode().toString();// 已取消
            }
            
        	if (AllocateInOrderStatusEnum.CANCEL.getCode().toString().equals(cancelResult)) { // 已取消状态
        		allocateInOrder.setIsCancel(ZeroToNineEnum.ONE.getCode());
        	}
        	allocateInOrder.setOldStatus(allocateInOrder.getStatus());
            allocateInOrder.setStatus(cancelResult);
            
            for (AllocateSkuDetail detail: allocateSkuDetailList) {
            	detail.setOldInStatus(detail.getInStatus()); 
            	detail.setInStatus(cancelResult);
            	
            }
            
            allocateInOrderService.updateByPrimaryKeySelective(allocateInOrder);
            allocateSkuDetailService.updateSkuDetailList(allocateSkuDetailList);
            
        } else if (StringUtils.equals(ZeroToNineEnum.ONE.getCode(), flag)) {//重新收货
        	
            if (!StringUtils.equals(AllocateInOrderStatusEnum.CANCEL.getCode().toString(), allocateInOrder.getStatus())
            		&& !StringUtils.equals(AllocateInOrderStatusEnum.RECIVE_WMS_RECIVE_FAILURE.getCode().toString(), allocateInOrder.getStatus())) {
                throw new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, "调拨单当前状态不能进行重新发货");
            }
            
            if (AllocateInOrderStatusEnum.CANCEL.getCode().toString().equals(allocateInOrder.getStatus())
            		&& DateCheckUtil.checkDate(allocateInOrder.getUpdateTime())) {
            	throw new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, "当前调拨入单取消时间过长，不能重新收货");
            }
            
            logOperationEnum = LogOperationEnum.RE_RECIVE_GOODS;
            
            //allocateInOrder.setStatus(allocateInOrder.getOldStatus());
            
            wmsAllocateOrderInNotice(allocateInOrder, aclUserAccreditInfo, false);
            //allocateInOrder.setIsCancel(ZeroToNineEnum.ZERO.getCode());
//            if (!wmsAllocateOrderInNotice(form.getAllocateInOrder(), aclUserAccreditInfo, false)) {
//            	throw new RuntimeException("调拨入库单重新收货失败");
//            }
        }
       
        
//        AllocateInOrderParamForm form = allocateOrderExtService.
//        		updateAllocateInOrderByCancel(allocateOrderCode, ZeroToNineEnum.ONE.getCode(), flag, cancelReson, cancelResult);

        //记录操作日志
        logInfoService.recordLog(allocateInOrder,
        		allocateInOrder.getId().toString(), aclUserAccreditInfo.getUserId(), logOperationEnum.getMessage(), cancelReson, null);
    }

    @Override
    @Transactional
    public void orderClose(String allocateOrderCode, String flag, String cancelReson, AclUserAccreditInfo aclUserAccreditInfo) {
        AssertUtil.notBlank(allocateOrderCode, "参数调拨单号allocateOrderCode不能为空");
        AssertUtil.notBlank(flag, "参数操作类型flag不能为空");
        if(StringUtils.equals(ZeroToNineEnum.ZERO.getCode(), flag)){//关闭操作
            AssertUtil.notBlank(cancelReson, "参数关闭原因cancelReson不能为空");
        }
        AllocateInOrderParamForm form = allocateOrderExtService.updateAllocateInOrderByCancel(allocateOrderCode, 
        		ZeroToNineEnum.ZERO.getCode(), flag, cancelReson, null);
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
    private OrderCancelResultEnum wmsCancelNotice (AllocateInOrder inOder,  Map<String, String> errMsg) {
    	
    	OrderCancelResultEnum resultEnum = OrderCancelResultEnum.CANCEL_FAIL;// 取消失败
    	
    	ScmOrderCancelRequest request = new ScmOrderCancelRequest();
    	request.setOrderType(CancelOrderType.ALLOCATE_IN.getCode());
    	// 自营仓 和 三方仓库 统一取WmsAllocateInOrderCode
    	request.setOrderCode(inOder.getWmsAllocateInOrderCode());
		//BeanUtils.copyProperties(allocateInOrder, request);
        commonService.getWarehoueType(inOder.getInWarehouseCode(), request);
    	
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
			logger.error("调拨入库单取消失败:", response.getDatabuffer());
		}
    	return resultEnum;
    	
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
		WarehouseInfo whi = commonService.getWarehoueType(allocateInOrder.getInWarehouseCode(), request);
		
		List<AllocateSkuDetail> detailList = allocateSkuDetailService
				.getDetailListByOrderCode(allocateInOrder.getAllocateOrderCode());
		
		if (WarehouseTypeEnum.Jingdong.getCode().equals(request.getWarehouseType())) {
			/**
			 * 京东仓处理逻辑 
			 **/
			List<ScmEntryOrderItem> list = new ArrayList<>();
			ScmEntryOrderItem scmEntryOrderItem = null;
			List<String> skuCodeList = detailList.stream().map(
					detail -> detail.getSkuCode()).collect(Collectors.toList());
	        Example example = new Example(WarehouseItemInfo.class);
	        Example.Criteria ca = example.createCriteria();
	        ca.andIn("skuCode", skuCodeList);
	        ca.andEqualTo("isDelete", ZeroToNineEnum.ZERO.getCode());
	        ca.andEqualTo("warehouseCode", allocateInOrder.getInWarehouseCode());
	        List<WarehouseItemInfo> whiList = warehouseItemInfoService.selectByExample(example);
	        for (AllocateSkuDetail detail : detailList) {
	        	for (WarehouseItemInfo info : whiList) {
					if (StringUtils.equals(info.getSkuCode(), detail.getSkuCode())) {
						scmEntryOrderItem = new ScmEntryOrderItem();
						scmEntryOrderItem.setItemId(info.getWarehouseItemId());
						scmEntryOrderItem.setPlanQty(detail.getPlanAllocateNum());
						list.add(scmEntryOrderItem);
						break;
					}
				}
	        }
	        request.setEntryOrderItemList(list);
	        String entryOrderCode = allocateInOrder.getAllocateInOrderCode();// 入库单号
			if (StringUtils.isNotBlank(allocateInOrder.getWmsAllocateInOrderCode())) { // 重新入库
				Integer orderSeq = (allocateInOrder.getInOrderSeq() == null ? 0 : allocateInOrder.getInOrderSeq()) + 1;
				entryOrderCode = allocateInOrder.getAllocateInOrderCode() + "_" + orderSeq;
				request.setEntryOrderCode(entryOrderCode);//SCM采购重新入库通知单编号
			} else {
				request.setEntryOrderCode(entryOrderCode);//SCM采购入库通知单编号
			}
			request.setPoType(JdPurchaseOrderTypeEnum.B2B.getCode());
			request.setOwnerCode(jDWmsConstantConfig.getDeptNo());//开放平台事业部编号, 货主id
			request.setWarehouseCode(warehouseExtService.getWmsWarehouseCode(allocateInOrder.getInWarehouseCode()));// 开放平台库房编号
			request.setSupplierCode(jDWmsConstantConfig.getSupplierNo());
			
		} else if (WarehouseTypeEnum.Zy.getCode().equals(request.getWarehouseType())) {
			List<ScmAllocateOrderItem> allocateOrderItemList = new ArrayList<>();
			ScmAllocateOrderItem item = null;
			for (AllocateSkuDetail detail : detailList) {
				item = new ScmAllocateOrderItem();
				BeanUtils.copyProperties(detail, item);
				allocateOrderItemList.add(item);
			}
			
			BeanUtils.copyProperties(allocateInOrder, request);
			request.setAllocateOrderItemList(allocateOrderItemList);
			request.setCreateOperatorName(aclUserAccreditInfo.getName());
			request.setCreateOperatorNumber(aclUserAccreditInfo.getPhone());
		}
	
		AppResult<ScmAllocateOrderInResponse> response = warehouseApiService.allocateOrderInNotice(request);
        
        //记录操作日志
        logInfoService.recordLog(allocateInOrder,allocateInOrder.getId().toString(),
        		aclUserAccreditInfo.getUserId(), LogOperationEnum.NOTICE_RECIVE_GOODS.getMessage(), "",null);

        String status = null;
        String logOp = null;
        String resultMsg = null;
        String errMsg = null;
        String wmsAllocatInCode = null;
        Integer orderSeq = null;
		if (StringUtils.equals(response.getAppcode(), ResponseAck.SUCCESS_CODE)) {
			status = AllocateInOrderStatusEnum.RECIVE_WMS_RECIVE_SUCCESS.getCode().toString();
			logOp = LogOperationEnum.ALLOCATE_ORDER_IN_NOTICE_SUCC.getMessage();
			resultMsg = "调拨入库通知成功！";
			ScmAllocateOrderInResponse rep = (ScmAllocateOrderInResponse) response.getResult();
			wmsAllocatInCode = rep.getWmsAllocateOrderInCode();
			if (StringUtils.isNotBlank(allocateInOrder.getWmsAllocateInOrderCode())) {// 重新入货
				orderSeq = (allocateInOrder.getInOrderSeq() == null ? 0 : allocateInOrder.getInOrderSeq()) + 1;
			}
			succ = true;
		} else {
			status = AllocateInOrderStatusEnum.RECIVE_WMS_RECIVE_FAILURE.getCode().toString();
			logOp = LogOperationEnum.ALLOCATE_ORDER_IN_NOTICE_FAIL.getMessage();
			resultMsg = "调拨入库通知失败！";
			errMsg = response.getDatabuffer();
		}
//		if (needUpdate) {
			allocateInOrderService.updateInOrderById(status, allocateInOrder.getId(), errMsg, wmsAllocatInCode, orderSeq);
			allocateSkuDetailService.updateInSkuStatusByOrderCode(status, allocateInOrder.getAllocateOrderCode());
//		}
        logInfoService.recordLog(new AllocateInOrder(), allocateInOrder.getId().toString(), whi.getWarehouseName(),
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
                        if(checkIsException(skuDetail)){
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

    private boolean checkIsException(AllocateSkuDetail skuDetail){
        if(StringUtils.equals(skuDetail.getInventoryType(), ZeroToNineEnum.ONE.getCode())){
            if(skuDetail.getRealOutNum().longValue() == skuDetail.getNornalInNum().longValue() &&
                    skuDetail.getDefectInNum().longValue() == 0 ){
                return true;
            }
        }else{
            if(skuDetail.getRealOutNum().longValue() == skuDetail.getDefectInNum().longValue() &&
                    skuDetail.getNornalInNum().longValue() == 0 ){
                return true;
            }
        }
        return false;
    }

    private Map<String,Long> delStock(List<ScmEntryOrderDetailResponseItem> scmEntryOrderDetailResponseItemList, AllocateSkuDetail detail, String warehouseCode) {
        //用Map存库存信息
        Map<String,Long> stockMap = new HashMap<>();
        //残次品入库数量
        Long defectiveQuantity = 0L;
        //正品入库数量
        Long normalQuantity = 0L;
        for (ScmEntryOrderDetailResponseItem entryOrderDetailOrder : scmEntryOrderDetailResponseItemList) {

            //获取发货详情
            WarehouseItemInfo warehouseItemInfo = new WarehouseItemInfo();
            warehouseItemInfo.setWarehouseItemId(entryOrderDetailOrder.getItemId());
            warehouseItemInfo.setWarehouseCode(warehouseCode);
            List<WarehouseItemInfo> warehouseItemInfos = warehouseItemInfoService.select(warehouseItemInfo);
            if(warehouseItemInfos == null || warehouseItemInfos.size() < 1){
                continue;
            }
            warehouseItemInfo = warehouseItemInfos.get(0);
            String skuCode = warehouseItemInfo.getSkuCode();

            if (StringUtils.equals(skuCode, detail.getSkuCode())){
                //计算反馈库存
                if (StringUtils.equals(entryOrderDetailOrder.getGoodsStatus(), EntryOrderDetailItemStateEnum.QUALITY_PRODUCTS.getCode())){
                    normalQuantity = (detail.getNornalInNum() == null ? 0 : detail.getNornalInNum()) + (entryOrderDetailOrder.getActualQty()) + (normalQuantity);
                }else if (StringUtils.equals(entryOrderDetailOrder.getGoodsStatus(),EntryOrderDetailItemStateEnum.DEFECTIVE_PRODUCTS.getCode())){
                    defectiveQuantity = (detail.getDefectInNum()==null ? 0 : detail.getDefectInNum()) + entryOrderDetailOrder.getActualQty() + defectiveQuantity;
                }
            }
        }
        stockMap.put("normalQ",normalQuantity);
        stockMap.put("defectiveQ",defectiveQuantity);

        return  stockMap;
    }

    @Override
    public void updateAllocateInDetail() {
        if (!realIpService.isRealTimerService()){
            return;
        }

        WarehouseInfo warehouseInfoTemp = new WarehouseInfo();
        warehouseInfoTemp.setOperationalNature(OperationalNatureEnum.SELF_SUPPORT.getCode());
        List<WarehouseInfo> warehouseInfoTempList = warehouseInfoService.select(warehouseInfoTemp);
        List<String> warehouseCodeList = new ArrayList<>();
        for(WarehouseInfo info : warehouseInfoTempList){
            warehouseCodeList.add(info.getCode());
        }

        Example example = new Example(AllocateInOrder.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("status", AllocateInOrderStatusEnum.RECIVE_WMS_RECIVE_SUCCESS.getCode());
        criteria.andNotIn("inWarehouseCode", warehouseCodeList);
        List<AllocateInOrder> allocateInOrderList = allocateInOrderService.selectByExample(example);
        if (!AssertUtil.collectionIsEmpty(allocateInOrderList)){
            List<List<AllocateInOrder>> splitAllocateInOrderList = ListSplit.split(allocateInOrderList,10);
            for (List<AllocateInOrder> orderList:splitAllocateInOrderList) {
                scmEntryOrder(orderList);
            }
        }else {
            logger.info("未查询到符合条件的调拨入库通知单！");
        }
    }

    @Override
    public void retryCancelOrder() {
        if (!realIpService.isRealTimerService()) return;
        AllocateInOrder orderTemp = new AllocateInOrder();
        orderTemp.setStatus(AllocateInOrderStatusEnum.CANCELLING.getCode().toString());
        List<AllocateInOrder> list = allocateInOrderService.select(orderTemp);

        //组装信息
        List<ScmOrderCancelRequest> requests = new ArrayList<>();
        for(AllocateInOrder order : list){
            WarehouseInfo warehouseInfo = new WarehouseInfo();
            warehouseInfo.setCode(order.getInWarehouseCode());
            List<WarehouseInfo> warehouseList =warehouseInfoService.select(warehouseInfo);
            if(warehouseList == null || warehouseList.size() < 1){
                continue;
            }
            warehouseInfo = warehouseList.get(0);
            ScmOrderCancelRequest scmOrderCancelRequest = new ScmOrderCancelRequest();
            scmOrderCancelRequest.setOrderCode(order.getWmsAllocateInOrderCode());
            scmOrderCancelRequest.setOwnerCode(warehouseInfo.getWarehouseOwnerId());
            scmOrderCancelRequest.setWarehouseType("JD");
            scmOrderCancelRequest.setOrderType(CancelOrderType.ALLOCATE_IN.getCode());
            requests.add(scmOrderCancelRequest);
        }

        //调用接口
        this.retryCancelOrder(requests);
    }

    private void retryCancelOrder (List<ScmOrderCancelRequest> requests){
        try{
            for (ScmOrderCancelRequest request : requests) {
                new Thread(() -> {
                    //调用接口
                    AppResult<ScmOrderCancelResponse> responseAppResult =
                            warehouseApiService.orderCancel(request);

                    //回写数据
                    try {
                        this.updateCancelOrder(responseAppResult, request.getOrderCode());
                    } catch (Exception e) {
                        e.printStackTrace();
                        logger.error("调拨入库单号:{},取消调拨入库异常：{}", request.getOrderCode(), responseAppResult.getResult());
                    }
                }).start();
            }
        }catch(Exception e){
            logger.error("取消调拨入库失败", e);
        }
    }

    private void updateCancelOrder(AppResult<ScmOrderCancelResponse> appResult, String orderCode){
        //处理信息
        try{
            if (StringUtils.equals(appResult.getAppcode(), SUCCESS)) { // 成功
                AllocateInOrder allocateInOrder = new AllocateInOrder();
                allocateInOrder.setWmsAllocateInOrderCode(orderCode);
                allocateInOrder = allocateInOrderService.selectOne(allocateInOrder);

                ScmOrderCancelResponse response = (ScmOrderCancelResponse)appResult.getResult();
                String flag = response.getFlag();

                if(StringUtils.equals(flag, ZeroToNineEnum.ONE.getCode())){
                    AllocateSkuDetail allocateSkuDetail = new AllocateSkuDetail();
                    allocateSkuDetail.setAllocateOrderCode(allocateInOrder.getAllocateOrderCode());
                    List<AllocateSkuDetail> allocateSkuDetailList = allocateSkuDetailService.select(allocateSkuDetail);
                    //修改状态
                    allocateInOrder.setStatus(AllocateInOrderStatusEnum.CANCEL.getCode().toString());
                    allocateInOrder.setIsCancel(ZeroToNineEnum.ONE.getCode());

                    for (AllocateSkuDetail detail: allocateSkuDetailList) {
                        detail.setInStatus(AllocateInOrderStatusEnum.CANCEL.getCode().toString());
                    }

                    allocateInOrderService.updateByPrimaryKeySelective(allocateInOrder);
                    allocateSkuDetailService.updateSkuDetailList(allocateSkuDetailList);

                    logInfoService.recordLog(allocateInOrder, String.valueOf(allocateInOrder.getId()),"admin",
                            "取消入库", "取消结果:取消成功",
                            null);
                }else if(StringUtils.equals(flag, ZeroToNineEnum.TWO.getCode())){
                    allocateInOrder.setStatus(AllocateInOrderStatusEnum.RECIVE_WMS_RECIVE_SUCCESS.getCode().toString());
                    allocateInOrder.setUpdateTime(Calendar.getInstance().getTime());
                    allocateInOrder.setIsCancel(ZeroToNineEnum.ZERO.getCode());
                    allocateInOrder.setOldStatus("");
                    allocateInOrderService.updateByPrimaryKey(allocateInOrder);

                    AllocateSkuDetail allocateSkuDetail = new AllocateSkuDetail();
                    allocateSkuDetail.setOutStatus(AllocateInOrderStatusEnum.RECIVE_WMS_RECIVE_SUCCESS.getCode().toString());
                    allocateSkuDetail.setUpdateTime(Calendar.getInstance().getTime());
                    allocateSkuDetail.setOldInStatus("");
                    Example example = new Example(AllocateSkuDetail.class);
                    Example.Criteria criteria = example.createCriteria();
                    criteria.andEqualTo("allocateOrderCode", allocateInOrder.getAllocateOrderCode());
                    allocateSkuDetailService.updateByExampleSelective(allocateSkuDetail, example);

                    logInfoService.recordLog(allocateInOrder, String.valueOf(allocateInOrder.getId()),"admin",
                            "取消入库", "取消结果:取消失败,"+response.getMessage(),
                            null);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
            logger.error("调拨入库单号:{},取消调拨入库异常：{}", orderCode, e.getMessage());
        }
    }

    private void judgeWarehouseNoticeDetailState(Map<String,Long> stockMap, AllocateSkuDetail detail, StringBuilder logMessage,
                                                 List<String> exceptionDetail) {
        //正品入库
        Long normalQuantity = stockMap.get("normalQ");
        //残次品入库
        Long defectiveQuantity = stockMap.get("defectiveQ");
        //判断收货状态
        detail.setDefectInNum(defectiveQuantity);
        detail.setNornalInNum(normalQuantity);
        Long realInNum = normalQuantity + defectiveQuantity;
        detail.setRealInNum(realInNum);
        if(detail.getRealOutNum().longValue() == detail.getNornalInNum().longValue() &&
                detail.getDefectInNum().longValue() == 0){
            detail.setAllocateInStatus(AllocateOrderEnum.AllocateOrderSkuInStatusEnum.IN_NORMAL.getCode());
            detail.setInStatus(String.valueOf(AllocateInOrderStatusEnum.IN_WMS_FINISH.getCode()));
            logMessage.append(detail.getSkuCode() + ":" + "入库完成<br>");
        }else{
            detail.setAllocateInStatus(AllocateOrderEnum.AllocateOrderSkuInStatusEnum.IN_EXCEPTION.getCode());
            detail.setInStatus(String.valueOf(AllocateInOrderStatusEnum.IN_WMS_EXCEPTION.getCode()));
            logMessage.append(detail.getSkuCode() + ":" + "入库异常<br>");
            exceptionDetail.add(detail.getSkuCode());
        }
        //设置入库时间
        detail.setUpdateTime(Calendar.getInstance().getTime());
        //更新入库通知单
        int count = allocateSkuDetailService.updateByPrimaryKey(detail);
        if (count == 0) {
            String msg = "修改入库通知单详情" + JSON.toJSONString(detail) + "数据库操作失败";
            throw new RuntimeException(msg);
        }
    }

    private void scmEntryOrder(List<AllocateInOrder> allocateInOrderList) {
        List<String> wmsOrderCodeList = new ArrayList<>();
        for (AllocateInOrder allocateInOrder : allocateInOrderList) {
            wmsOrderCodeList.add(allocateInOrder.getWmsAllocateInOrderCode());
        }
        ScmEntryOrderDetailRequest entryOrderDetailRequest = new ScmEntryOrderDetailRequest();
        entryOrderDetailRequest.setEntryOrderCode(StringUtils.join(wmsOrderCodeList, SupplyConstants.Symbol.COMMA));
        AppResult appResult = null;
        if(StringUtils.equals(mockOuterInterface, ZeroToNineEnum.ONE.getCode())){//仓库接口mock
            appResult = warehouseMockService.entryOrderDetail(entryOrderDetailRequest);
        }else{
            appResult = warehouseApiService.entryOrderDetail(entryOrderDetailRequest);
        }
        List<ScmEntryOrderDetailResponse> scmEntryOrderDetailResponseListRequest =
                (List<ScmEntryOrderDetailResponse>) appResult.getResult();
        for (AllocateInOrder allocateInOrder : allocateInOrderList) {
            String warehouseCode = allocateInOrder.getInWarehouseCode();
            //获取当前入库单对应的入库查询结果
            List<ScmEntryOrderDetailResponse> scmEntryOrderDetailResponseList = new ArrayList<>();
            for (ScmEntryOrderDetailResponse entryOrderDetail : scmEntryOrderDetailResponseListRequest) {
                if (StringUtils.equals(entryOrderDetail.getPoOrderNo(), allocateInOrder.getWmsAllocateInOrderCode())){
                    scmEntryOrderDetailResponseList.add(entryOrderDetail);
                }
            }
            //处理库存信息
            if (!AssertUtil.collectionIsEmpty(scmEntryOrderDetailResponseList)) {
                for (ScmEntryOrderDetailResponse entryOrderDetail : scmEntryOrderDetailResponseList) {
                    if (!StringUtils.equals(entryOrderDetail.getStatus(), "70")) {
                        //如果不是70的完成状态,当前采购单入库详情就跳过处理
                        continue;
                    }
                    /* 定位到入库通知单 */
                    AllocateInOrder allocateInOrderTemp = new AllocateInOrder();
                    allocateInOrderTemp.setWmsAllocateInOrderCode(entryOrderDetail.getPoOrderNo());
                    allocateInOrderTemp = allocateInOrderService.selectOne(allocateInOrderTemp);
                    //查询到入库通知单,查询到关联的入库通知单详情
                    if (null != allocateInOrderTemp) {
                        //异常入库的通知单详情
                        Set<String> exceptionSku = new HashSet<>();
                        Set<String> exceptionSkuCount = new HashSet<>();
                        //查询入库通知单编号为entryOrderDetail.getEntryOrderCode()的入库通知单详情
                        AllocateSkuDetail allocateSkuDetail = new AllocateSkuDetail();
                        allocateSkuDetail.setAllocateOrderCode(allocateInOrder.getAllocateOrderCode());
                        List<AllocateSkuDetail> allocateSkuDetailList = allocateSkuDetailService.select(allocateSkuDetail);
                        List<ScmEntryOrderDetailResponseItem> scmEntryOrderDetailResponseItemList = entryOrderDetail.getScmEntryOrderDetailResponseItemList();
                        if (!AssertUtil.collectionIsEmpty(allocateSkuDetailList) && !AssertUtil.collectionIsEmpty(scmEntryOrderDetailResponseItemList)) {
                            //String logMessage = "";
                            StringBuilder logMessage = new StringBuilder();
                            List<String> exceptionDetail = new ArrayList<>();

                            for (AllocateSkuDetail detail : allocateSkuDetailList) {
                                //获取当前入库单详情的库存情况,目前只有两种状态
                                Map<String, Long> stockMap = delStock(scmEntryOrderDetailResponseItemList, detail, warehouseCode);
                                //判断入库为0的时候 直接跳过
                                if (stockMap.get("defectiveQ").longValue() == 0L && stockMap.get("normalQ").longValue() == 0L) {
                                    continue;
                                }
                                //更新状态
                                judgeWarehouseNoticeDetailState(stockMap, detail, logMessage, exceptionDetail);

                                //记录库存变动明细
                                try {
                                    insertStockDetail(detail, allocateInOrder, stockMap);
                                } catch (Exception e) {
                                    logger.error("JD调拨入库，记录库存变动明细失败， 入库单号:{}, e:", allocateInOrder.getAllocateInOrderCode(), e);
                                }
                            }

                            allocateSkuDetailService.updateSkuDetailList(allocateSkuDetailList);
                            //更新调拨入库单状态
                            if(StringUtils.isNotEmpty(getAllocateInOrderStatusByDetail(allocateSkuDetailList))){
                                allocateInOrder.setStatus(getAllocateInOrderStatusByDetail(allocateSkuDetailList));
                            }
                            if(exceptionDetail.size() > 0){
                                allocateInOrder.setFailedCause("["+StringUtils.join(exceptionDetail, ",")+"]实际入库信息与实际出库信息不符。");
                            }
                            allocateInOrderService.updateByPrimaryKey(allocateInOrder);
                            //更新调拨单状态
                            AllocateOrder allocateOrder = new AllocateOrder();
                            allocateOrder.setAllocateOrderCode(allocateInOrder.getAllocateOrderCode());
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
                                    LogOperationEnum.ALLOCATE_IN.getMessage(), logMessage.toString(), null);

                            logInfoService.recordLog(allocateOrder, allocateOrder.getAllocateOrderCode(), warehouseInfo.getWarehouseName(),
                                    LogOperationEnum.ALLOCATE_IN.getMessage(), logMessage.toString(), null);
                        } else {
                            logger.error("本地未查询到通知单编号为" + entryOrderDetail.getEntryOrderCode() + "的调拨入库通知单详情,反馈的通知单详情为空");
                        }
                    }
                }
            }
        }
    }

    private void insertStockDetail(AllocateSkuDetail detail, AllocateInOrder allocateInOrder, Map<String,Long> stockMap) {

        logger.info("JD调拨入库记录库存变动明， 订单编号:{}，变动详情:{}", allocateInOrder.getInWarehouseCode(), JSON.toJSONString(stockMap));

        JdStockInDetail jdStockInDetail = new JdStockInDetail();
        jdStockInDetail.setWarehouseCode(allocateInOrder.getInWarehouseCode());
        jdStockInDetail.setStockType(detail.getInventoryType());
        jdStockInDetail.setOperationType(StockOperationTypeEnum.ALLALLOCATE_IN.getCode());
        jdStockInDetail.setSupplierCode(allocateInOrder.getSupplierCode());
        jdStockInDetail.setOrderCode(allocateInOrder.getAllocateInOrderCode());
        jdStockInDetail.setWarehouseOrderCode(allocateInOrder.getWmsAllocateInOrderCode());
        jdStockInDetail.setWarehouseOrderCode("");
        jdStockInDetail.setSkuCode(detail.getSkuCode());
        jdStockInDetail.setBarCode(detail.getBarCode());
        jdStockInDetail.setGoodsType("");
        jdStockInDetail.setSpecInfo(detail.getSpecNatureInfo());
        jdStockInDetail.setPlannedQuantity(detail.getRealOutNum());
        jdStockInDetail.setQuantity(stockMap.get("normalQ") + stockMap.get("defectiveQ"));
        jdStockInDetail.setNormalQuantity(stockMap.get("normalQ"));
        jdStockInDetail.setDefectiveQuantity(stockMap.get("defectiveQ"));
        int insert = jdStockInDetailService.insert(jdStockInDetail);
        if(insert == 0){
            logger.error("JD调拨入库，记录库存变动明细失败， 入库单号:{}", allocateInOrder.getAllocateInOrderCode());
        }
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
