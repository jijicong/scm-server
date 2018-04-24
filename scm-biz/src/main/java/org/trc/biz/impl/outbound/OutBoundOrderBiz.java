package org.trc.biz.impl.outbound;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qimen.api.request.DeliveryorderCreateRequest;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.trc.biz.order.IScmOrderBiz;
import org.trc.biz.outbuond.IOutBoundOrderBiz;
import org.trc.common.RequsetUpdateStock;
import org.trc.constant.RequestFlowConstant;
import org.trc.constants.SupplyConstants;
import org.trc.domain.System.LogisticsCompany;
import org.trc.domain.config.RequestFlow;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.order.*;
import org.trc.domain.warehouseInfo.WarehouseInfo;
import org.trc.enums.*;
import org.trc.exception.OutboundOrderException;
import org.trc.form.*;
import org.trc.form.order.OutboundForm;
import org.trc.form.outbound.OutBoundOrderForm;
import org.trc.form.warehouse.*;
import org.trc.model.ToGlyResultDO;
import org.trc.service.config.ILogInfoService;
import org.trc.service.goods.ISkuStockService;
import org.trc.service.impl.TrcService;
import org.trc.service.impl.config.RequestFlowService;
import org.trc.service.impl.outbound.OutBoundOrderService;
import org.trc.service.impl.system.LogisticsCompanyService;
import org.trc.service.order.IOrderItemService;
import org.trc.service.outbound.IOutBoundOrderService;
import org.trc.service.outbound.IOutboundDetailLogisticsService;
import org.trc.service.outbound.IOutboundDetailService;
import org.trc.service.outbound.IOutboundPackageInfoService;
import org.trc.service.warehouse.IWarehouseApiService;
import org.trc.service.warehouseInfo.IWarehouseInfoService;
import org.trc.util.*;
import org.trc.util.cache.OutboundOrderCacheEvict;
import org.trc.util.lock.RedisLock;
import tk.mybatis.mapper.entity.Example;

import javax.ws.rs.core.Response;
import java.text.SimpleDateFormat;
import java.util.*;

@Service("outBoundOrderBiz")
public class OutBoundOrderBiz implements IOutBoundOrderBiz {

    public final static String SUCCESS = "200";

    public final static String CREATE_OUTBOUND = "createOutbound";
    private static final Map<String, String> statusMap = new HashMap<String, String>();

    static {
    	// 状态转换
    	statusMap.put(SuccessFailureEnum.FAILURE.getCode(), RequestFlowStatusEnum.SEND_FAILED.getCode());
    	statusMap.put(SuccessFailureEnum.SOCKET_TIME_OUT.getCode(), RequestFlowStatusEnum.SEND_TIME_OUT.getCode());
    	statusMap.put(SuccessFailureEnum.SUCCESS.getCode(), RequestFlowStatusEnum.SEND_SUCCESS.getCode());
    	statusMap.put(SuccessFailureEnum.ERROR.getCode(), RequestFlowStatusEnum.SEND_ERROR.getCode());
    }

    private Logger logger = LoggerFactory.getLogger(OutBoundOrderBiz.class);
    @Autowired
    private IOutBoundOrderService outBoundOrderService;
    @Autowired
    private IOutboundDetailService outboundDetailService;
    @Autowired
    private IOutboundDetailLogisticsService outboundDetailLogisticsService;
    @Autowired
    private ILogInfoService logInfoService;
    @Autowired
    private IWarehouseApiService warehouseApiService;
    @Autowired
    private ISkuStockService skuStockService;
    @Autowired
    private IScmOrderBiz scmOrderBiz;
    @Autowired
    private IOrderItemService orderItemService;
    @Autowired
    private TrcConfig trcConfig;
    @Autowired
    private TrcService trcService;
    @Autowired
    private RequestFlowService requestFlowService;
    @Autowired
    private LogisticsCompanyService logisticsCompanyService;
    @Autowired
    private IWarehouseInfoService warehouseInfoService;
    @Autowired
    private RedisLock redisLock;
    @Autowired
    private IOutboundPackageInfoService outboundPackageInfoService;

    @Override
    public Pagenation<OutboundOrder> outboundOrderPage(OutBoundOrderForm form, Pagenation<OutboundOrder> page, AclUserAccreditInfo aclUserAccreditInfo) throws Exception {
        AssertUtil.notNull(aclUserAccreditInfo, "获取用户信息失败!");
        //获得业务线编码
        String channelCode = aclUserAccreditInfo.getChannelCode();
        AssertUtil.notBlank(channelCode, "业务线编码为空!");

        //创建查询条件
        Example example = new Example(OutboundOrder.class);
        this.setQueryParam(example, form, channelCode);

        //查询数据
        Pagenation<OutboundOrder> pagenation = outBoundOrderService.pagination(example, page, form);
        List<OutboundOrder> outboundOrderList = pagenation.getResult();
        for(OutboundOrder order : outboundOrderList){
            order.setWarehouseName(warehouseInfoService.selectByPrimaryKey(order.getWarehouseId()).getWarehouseName());
            if((StringUtils.equals(order.getIsCancel(), ZeroToNineEnum.ONE.getCode())
                    || StringUtils.equals(order.getIsClose(), ZeroToNineEnum.ONE.getCode())) &&
                    this.checkDate(order.getUpdateTime())){
                order.setIsTimeOut(ZeroToNineEnum.ONE.getCode());
            }else{
                order.setIsTimeOut(ZeroToNineEnum.ZERO.getCode());
            }
        }
        return pagenation;
    }

    //调用获取商品详情接口
    private void deliveryOrderDetail (List<ScmDeliveryOrderDetailRequest> requests){
        try{
            for (ScmDeliveryOrderDetailRequest request : requests) {
                new Thread(() -> {
                    //调用接口
                    AppResult<ScmDeliveryOrderDetailResponse> responseAppResult =
                            warehouseApiService.deliveryOrderDetail(request);

                    //回写数据
                    try {
                        this.updateDeliveryOrderDetail(responseAppResult, request.getOrderCode(), request.getOrderId());
                    } catch (Exception e) {
                        e.printStackTrace();
                        logger.error("发货单号:{},物流信息获取异常{}", request.getOrderCode(),e.getMessage());
                    }
                }).start();
            }

        }catch(Exception e){
            logger.error("获取商品详情失败", e);
        }
    }

    @Override
    public void updateOutboundDetail(){
        //获取所有为等待仓库发货发货单信息
        Example example = new Example(OutboundOrder.class);
        Example.Criteria criteria = example.createCriteria();
        List<String> list = new ArrayList<>();
        list.add(OutboundOrderStatusEnum.WAITING.getCode());
        list.add(OutboundOrderStatusEnum.PART_OF_SHIPMENT.getCode());
        criteria.andIn("status", list);
        List<OutboundOrder> outboundOrders = outBoundOrderService.selectByExample(example);

        //组装信息
        List<ScmDeliveryOrderDetailRequest> requests = new ArrayList<>();
        for(OutboundOrder order : outboundOrders){
            //获取仓库信息
            WarehouseInfo warehouseInfo = warehouseInfoService.selectByPrimaryKey(order.getWarehouseId());
            if(warehouseInfo == null || StringUtils.isEmpty(warehouseInfo.getCode())){
                continue;
            }

            //组装请求信息
            ScmDeliveryOrderDetailRequest request = new ScmDeliveryOrderDetailRequest();
            request.setOrderCode(order.getOutboundOrderCode());
            request.setOrderId(order.getWmsOrderCode());
            request.setOwnerCode(warehouseInfo.getWarehouseOwnerId());
            request.setWarehouseCode(warehouseInfo.getCode());
            requests.add(request);
        }

        //调用接口
        this.deliveryOrderDetail(requests);

    }

    private boolean isCompound(String num){
        List<String> list = new ArrayList<>();
        list.add("10017");
        list.add(num);
        Collections.sort(list);
        if(StringUtils.equals("10017", list.get(1))){
            return false;
        }else{
            return true;
        }
    }

    //修改发货单详情
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Cacheable(value = SupplyConstants.Cache.OUTBOUND_ORDER)
    public void updateDeliveryOrderDetail(AppResult<ScmDeliveryOrderDetailResponse> responseAppResult,
                                           String outboundOrderCode, String orderId) throws Exception{
        if(StringUtils.equals("200", responseAppResult.getAppcode())){
            ScmDeliveryOrderDetailResponse response = (ScmDeliveryOrderDetailResponse) responseAppResult.getResult();
            //获取发货单
            OutboundOrder outboundOrder = new OutboundOrder();
            outboundOrder.setOutboundOrderCode(outboundOrderCode);
            outboundOrder = outBoundOrderService.selectOne(outboundOrder);

            //获取仓库名称
            Long warehouseId = outboundOrder.getWarehouseId();
            WarehouseInfo warehouse = warehouseInfoService.selectByPrimaryKey(warehouseId);

            if(response.getCurrentStatus() != null && StringUtils.equals("10028", response.getCurrentStatus())){
                this.updateDetailStatus(OutboundDetailStatusEnum.CANCELED.getCode(), outboundOrder.getOutboundOrderCode());
                this.updateOrderCancelInfo(outboundOrder, "线下取消",false);

                //更新库存
                skuStockService.updateSkuStock(this.getStock(outboundOrder.getOutboundOrderCode(),
                        outboundOrder.getWarehouseCode(), outboundOrder.getChannelCode(), false));

                //更新订单信息
                this.updateItemOrderSupplierOrderStatus(outboundOrder.getOutboundOrderCode(), outboundOrder.getWarehouseOrderCode());

                //记录日志
                logInfoService.recordLog(outboundOrder, String.valueOf(outboundOrder.getId()), warehouse.getWarehouseName(),
                        "取消发货", "仓库平台取消", null);
                return ;
            }

            //只获取复合过的发货单详情
            if(response != null && response.getCurrentStatus() != null && this.isCompound(response.getCurrentStatus()) ){
                //组装获取包裹信息
                ScmOrderPacksRequest request = new ScmOrderPacksRequest();
                request.setOrderIds(orderId);
                //调用京东接口获取包裹信息
                AppResult<ScmOrderPacksResponse> packageResponseAppResult = warehouseApiService.orderPack(request);

                if(StringUtils.equals("200", packageResponseAppResult.getAppcode())){
                    ScmOrderPacksResponse packsResponse = (ScmOrderPacksResponse) packageResponseAppResult.getResult();

                    if(!StringUtils.equals(packsResponse.getScmOrderDefaultResults().get(0).getOrderCode(), outboundOrderCode)){
                        logger.error("发货单号:{},物流信息获取异常", outboundOrderCode);
                        return;
                    }

//                    if(!StringUtils.equals(response.getDeliveryOrderCode(), outboundOrderCode)){
//                        logger.error("发货单号:{},物流信息获取异常", outboundOrderCode);
//                        return;
//                    }

                    //更新发货单信息
                    List<RequsetUpdateStock> list = this.updateOutboundDetailAndLogistics(packsResponse, outboundOrder.getWarehouseCode());

                    //更新发货单状态
                    this.setOutboundOrderStatus(outboundOrderCode, outboundOrder);

                    //更新库存
                    if(list.size() > 0){
                        skuStockService.updateSkuStock(list);
                    }

                    //更新订单信息
                    this.updateItemOrderSupplierOrderStatus(outboundOrderCode, outboundOrder.getWarehouseOrderCode());

                    // 发货单确认结果通知渠道
                    deliveryOrderConfirmNotice(outboundOrder, response);

                    //记录日志
                    logInfoService.recordLog(outboundOrder, String.valueOf(outboundOrder.getId()), warehouse.getWarehouseName(),
                            LogOperationEnum.SEND.getMessage(),this.getPartSkuInfo(list, outboundOrder.getOutboundOrderCode()), null);
                }

            }

        }else{
            logger.error("发货单号:{},物流信息获取异常：{}", outboundOrderCode, responseAppResult.getResult());
        }
    }
    
    private void deliveryOrderConfirmNotice(OutboundOrder outboundOrder,
                                            ScmDeliveryOrderDetailResponse response) {
    	try {
    		LogisticNoticeForm noitce = new LogisticNoticeForm();
    		//设置请求渠道的签名
    		TrcParam trcParam = ParamsUtil.generateTrcSign(trcConfig.getKey(), TrcActionTypeEnum.SEND_LOGISTIC);
    		BeanUtils.copyProperties(trcParam, noitce);
    		
    		// 获取店铺级订单号
    		noitce.setShopOrderCode(outboundOrder.getShopOrderCode());
    		
    		// 信息类型 0-物流单号,1-配送信息
    		noitce.setType(LogsticsTypeEnum.WAYBILL_NUMBER.getCode());

    		//物流公司名称
            String logisticsName = response.getLogisticsName();
            //物流公司编号
            String logisticsCode = response.getLogisticsCode();
            //物流单号
            String expressCode = response.getExpressCode();
            //所有商品详情
            List<ScmDeliveryOrderDetailResponseItem> items = response.getScmDeliveryOrderDetailResponseItemList();
    		
    		// 包裹信息列表
    		List<Logistic> logisticList = new ArrayList<>();
            Logistic lsc = new Logistic();
            lsc.setSupplierOrderCode(outboundOrder.getOutboundOrderCode());
            lsc.setWaybillNumber(expressCode);
            // 物流公司名称
            lsc.setLogisticsCorporation(logisticsName);
            // 物流公司编码
            lsc.setLogisticsCorporationCode(generateLogisticsCode(logisticsCode, outboundOrder.getChannelCode()));
            /**
             * 包裹对应商品信息
             **/
            List<SkuInfo> skuList = new ArrayList<>();
            if (!CollectionUtils.isEmpty(items)) {
                items.forEach(item -> {
                    SkuInfo sku = new SkuInfo();
                    sku.setSkuCode(item.getItemCode());
                    sku.setNum(item.getActualQty().intValue());
                    /**
                     * 获取skuName
                     **/
                    OutboundDetail detail = new OutboundDetail();
                    detail.setOutboundOrderCode(outboundOrder.getOutboundOrderCode());
                    detail.setSkuCode(item.getItemCode());
                    OutboundDetail od = outboundDetailService.selectOne(detail);
                    sku.setSkuName(od.getSkuName());
                    skuList.add(sku);
                });
            }
            lsc.setSkus(skuList);
            logisticList.add(lsc);
            noitce.setLogistics(logisticList);
    		//物流信息同步给渠道
    		String reqNum = requestFlowService.insertRequestFlow(RequestFlowConstant.GYL, RequestFlowConstant.TRC, 
    				RequestFlowTypeEnum.SEND_LOGISTICS_INFO_TO_CHANNEL.getCode(), 
    				RequestFlowStatusEnum.SEND_INITIAL.getCode(), JSONObject.toJSONString(noitce));
    		ToGlyResultDO toGlyResultDO = trcService.sendLogisticInfoNotice(noitce);
    		RequestFlow requestFlowUpdate = new RequestFlow();
    		requestFlowUpdate.setRequestNum(reqNum);
    		requestFlowUpdate.setResponseParam(JSONObject.toJSONString(toGlyResultDO));
    		requestFlowUpdate.setStatus(statusMap.get(toGlyResultDO.getStatus()));
    		requestFlowService.updateRequestFlowByRequestNum(requestFlowUpdate);
    		
    	} catch (Exception e) {
    		e.printStackTrace();
    		logger.error("发货单号:{},物流信息通知渠道异常：{}", 
    				outboundOrder.getOutboundOrderCode(), e.getMessage());
    	}	
    	
    }
    
    private String generateLogisticsCode(String logisticsCode, String channelCode) {
    	String retMsg = "物流公司编码未找到";
    	if (StringUtils.isBlank(logisticsCode)) {
    		return retMsg;
    	}
    	LogisticsCompany queryLc = new LogisticsCompany();
    	queryLc.setType("QIMEN");
    	queryLc.setCompanyCode(logisticsCode);
        LogisticsCompany lc = logisticsCompanyService.selectOne(queryLc);
        if (null == lc) {
        	return retMsg;
        }
        queryLc.setType(channelCode);
        queryLc.setCompanyName(lc.getCompanyName());
        LogisticsCompany lctmp = logisticsCompanyService.selectOne(queryLc);
        if (null == lctmp) {
        	return retMsg;
        }
		return lctmp.getCompanyCode();
	}

	//更新itemOrder
    private void updateItemOrderSupplierOrderStatus(String outboundOrderCode, String warehouseOrderCode){
        List<OutboundDetail> outboundDetailList = this.getOutboundDetailListByOutboundOrderCode(outboundOrderCode, null);
        String status = null;
        OrderItem orderItem = null;
        for(OutboundDetail outboundDetail : outboundDetailList){
            orderItem = this.getOrderItemByWarehouseOrderCodeAnd(warehouseOrderCode, outboundDetail.getSkuCode());
            AssertUtil.notNull(orderItem, String.format("未查询到要更新的订单信息,仓库订单编码为：%s,SKU编码为：%s",
                    warehouseOrderCode, outboundDetail.getSkuCode()));
            status = outboundDetail.getStatus();
            if(StringUtils.equals(status, OutboundDetailStatusEnum.PART_OF_SHIPMENT.getCode())){
                orderItem.setSupplierOrderStatus(OrderItemDeliverStatusEnum.PARTS_DELIVER.getCode());
            }else if(StringUtils.equals(status, OutboundDetailStatusEnum.ALL_GOODS.getCode())){
                orderItem.setSupplierOrderStatus(OrderItemDeliverStatusEnum.ALL_DELIVER.getCode());
            }else if(StringUtils.equals(status, OutboundDetailStatusEnum.CANCELED.getCode())){
                orderItem.setSupplierOrderStatus(OrderItemDeliverStatusEnum.ORDER_CANCEL.getCode());
            }else if(StringUtils.equals(status, OutboundDetailStatusEnum.WAITING.getCode())){
                orderItem.setSupplierOrderStatus(OrderItemDeliverStatusEnum.WAIT_WAREHOUSE_DELIVER.getCode());
            }else if(StringUtils.equals(status, OutboundDetailStatusEnum.RECEIVE_FAIL.getCode())){
                orderItem.setSupplierOrderStatus(OrderItemDeliverStatusEnum.WAREHOUSE_RECIVE_FAILURE.getCode());
            }else if(StringUtils.equals(status, OutboundDetailStatusEnum.ON_CANCELED.getCode())){
                orderItem.setSupplierOrderStatus(OrderItemDeliverStatusEnum.ORDER_CANCELING.getCode());
            }
            orderItemService.updateByPrimaryKey(orderItem);
        }
        scmOrderBiz.outboundConfirmNotice(warehouseOrderCode);
    }

    //查询orderItem
    private OrderItem getOrderItemByWarehouseOrderCodeAnd(String warehouseOrderCode, String skuCode){
        OrderItem orderItem = new OrderItem();
        orderItem.setWarehouseOrderCode(warehouseOrderCode);
        orderItem.setSkuCode(skuCode);
        return orderItemService.selectOne(orderItem);
    }

    //根据发货单查询发货详情
    private List<OutboundDetail> getOutboundDetailListByOutboundOrderCode(String outboundOrderCode, String status){
        OutboundDetail outboundDetail = new OutboundDetail();
        outboundDetail.setOutboundOrderCode(outboundOrderCode);
        outboundDetail.setStatus(status);
        return outboundDetailService.select(outboundDetail);
    }

    //拼接部分发货明细
    private String getPartSkuInfo(List<RequsetUpdateStock> list, String outboundOrderCode){
        if(list.size() < 1){
            return "";
        }
        List<String> skuCodes = new ArrayList<>();
        for(RequsetUpdateStock stock : list ){
            skuCodes.add(stock.getSkuCode());
        }

        Example example = new Example(OutboundDetail.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("skuCode", skuCodes);
        criteria.andEqualTo("outboundOrderCode", outboundOrderCode);
        List<OutboundDetail> outboundDetailList = outboundDetailService.selectByExample(example);
        StringBuffer sb = new StringBuffer("");
        for(OutboundDetail detail : outboundDetailList){
            if(StringUtils.equals(detail.getStatus(), OutboundDetailStatusEnum.PART_OF_SHIPMENT.getCode())){
                sb.append(detail.getSkuCode()).append(":").append("部分发货").append("<br/>");
            }
            if(StringUtils.equals(detail.getStatus(), OutboundDetailStatusEnum.ALL_GOODS.getCode())){
                sb.append(detail.getSkuCode()).append(":").append("全部发货").append("<br/>");
            }
        }
        return sb.toString();
    }

    //更新发货单状态
    private void setOutboundOrderStatus(String outboundOrderCode, OutboundOrder outboundOrder){
        List<OutboundDetail> outboundDetailList = null;
        OutboundDetail outboundDetail = new OutboundDetail();
        outboundDetail.setOutboundOrderCode(outboundOrderCode);
        outboundDetailList = outboundDetailService.select(outboundDetail);
        String outboundOrderStatus = this.getOutboundOrderStatusByDetail(outboundDetailList);
        outboundOrder.setStatus(outboundOrderStatus);
        outBoundOrderService.updateByPrimaryKey(outboundOrder);
    }

    //更新发货单
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public List<RequsetUpdateStock> updateOutboundDetailAndLogistics(ScmDeliveryOrderDetailResponse response, String warehouseCode){
        OutboundDetail outboundDetail = null;
        OutboundDetailLogistics outboundDetailLogistics = null;
        List<OutboundDetailLogistics> outboundDetailLogisticsList = null;
        List<RequsetUpdateStock> updateStockList = new ArrayList<RequsetUpdateStock>();

        //发货单号
        String outboundOrderCode = response.getDeliveryOrderCode();
        //物流公司名称
        String logisticsName = response.getLogisticsName();
        //物流公司编号
        String logisticsCode = response.getLogisticsCode();
        //运单号
        String expressCode = response.getExpressCode();

        List<ScmDeliveryOrderDetailResponseItem> items = response.getScmDeliveryOrderDetailResponseItemList();

        //获取当前商品的包裹号
        outboundDetailLogistics = new OutboundDetailLogistics();
        outboundDetailLogistics.setWaybillNumber(expressCode);
        outboundDetailLogisticsList = outboundDetailLogisticsService.select(outboundDetailLogistics);

        //判断是否已存储物流信息，如果没有新增
        if(outboundDetailLogisticsList == null || outboundDetailLogisticsList.size() < 1) {
            //遍历所有商品详情
            if(items != null && items.size() > 0){
                for (ScmDeliveryOrderDetailResponseItem item : items) {
                    Long sentNum = item.getActualQty();
                    //获取发货详情
                    outboundDetail = new OutboundDetail();
                    outboundDetail.setOutboundOrderCode(outboundOrderCode);
                    outboundDetail.setWarehouseItemId(item.getItemId());
                    outboundDetail = outboundDetailService.selectOne(outboundDetail);

                    outboundDetailLogistics = new OutboundDetailLogistics();
                    outboundDetailLogistics.setOutboundDetailId(outboundDetail.getId());
                    outboundDetailLogistics.setLogisticsCorporation(logisticsName);
                    outboundDetailLogistics.setLogisticsCode(logisticsCode);
                    outboundDetailLogistics.setItemNum(sentNum);
                    outboundDetailLogistics.setWaybillNumber(expressCode);
                    outboundDetailLogistics.setCreateTime(Calendar.getInstance().getTime());
                    outboundDetailLogistics.setUpdateTime(Calendar.getInstance().getTime());
                    //保存信息
                    outboundDetailLogisticsService.insert(outboundDetailLogistics);
                    //获取实际到货数量
                    Long count = this.getItemNum(outboundDetail.getId());
                    outboundDetail.setRealSentItemNum(count);
                    if (count >= outboundDetail.getShouldSentItemNum()) {
                        outboundDetail.setStatus(OutboundDetailStatusEnum.ALL_GOODS.getCode());
                    } else {
                        outboundDetail.setStatus(OutboundDetailStatusEnum.PART_OF_SHIPMENT.getCode());
                    }
                    outboundDetail.setUpdateTime(Calendar.getInstance().getTime());
                    outboundDetailService.updateByPrimaryKey(outboundDetail);

                    RequsetUpdateStock requsetUpdateStock = new RequsetUpdateStock();
                    Map<String, String> stockType = new HashMap<>();
                    stockType.put("frozen_inventory", String.valueOf((-1 * sentNum)));
                    stockType.put("real_inventory", String.valueOf((-1 * sentNum)));
                    requsetUpdateStock.setStockType(stockType);
                    requsetUpdateStock.setChannelCode("TRMALL");
                    requsetUpdateStock.setSkuCode(outboundDetail.getSkuCode());
                    requsetUpdateStock.setWarehouseCode(warehouseCode);
                    updateStockList.add(requsetUpdateStock);
                }
            }
        }

        return updateStockList;
    }

    //更新发货单
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public List<RequsetUpdateStock> updateOutboundDetailAndLogistics(ScmOrderPacksResponse response, String warehouseCode){
        OutboundDetail outboundDetail = null;
        OutboundDetailLogistics outboundDetailLogistics = null;
        OutboundPackageInfo outboundPackageInfo = null;
        List<OutboundPackageInfo> outboundPackageInfoList = null;
        List<RequsetUpdateStock> updateStockList = new ArrayList<>();
        List<ScmOrderDefaultResult> results = response.getScmOrderDefaultResults();

        //遍历获取包裹信息
        for(ScmOrderDefaultResult result : results){
            //发货单号
            String outboundOrderCode = result.getOrderCode();
            //物流公司名称
            String logisticsName = result.getLogisticsName();
            //物流公司编号
            String logisticsCode = result.getLogisticsCode();

            String wayBill = result.getWayBill();

            List<ScmOrderPackage> scmOrderPackageList = result.getScmOrderPackageList();

            if(scmOrderPackageList == null || scmOrderPackageList.size() < 1){
                return updateStockList;
            }

            boolean flag = true;
            //遍历所有包裹
            for(ScmOrderPackage pack : scmOrderPackageList){
                String packageNo = pack.getPackageNo();
                List<ScmDeliveryOrderDetailResponseItem> items = pack.getScmDeliveryOrderDetailResponseItems();

                //获取当前商品的包裹号
                outboundPackageInfo = new OutboundPackageInfo();
                outboundPackageInfo.setPackageNumber(packageNo);
                outboundPackageInfoList = outboundPackageInfoService.select(outboundPackageInfo);

                //获取运单号
                String expressCode = packageNo.split("-")[0];

                if((items != null && items.size() > 0) || (outboundPackageInfoList != null && outboundPackageInfoList.size() > 0)){
                    flag = false;
                }

                //判断是否已存储物流信息，如果没有新增
                if(outboundPackageInfoList == null || outboundPackageInfoList.size() < 1) {
                    outboundPackageInfo = new OutboundPackageInfo();
                    outboundPackageInfo.setPackageNumber(packageNo);
                    outboundPackageInfo.setCreateTime(Calendar.getInstance().getTime());
                    outboundPackageInfo.setUpdateTime(Calendar.getInstance().getTime());
                    outboundPackageInfo.setWaybillNumber(expressCode);
                    outboundPackageInfoService.insert(outboundPackageInfo);

                    //遍历所有商品详情
                    if(items != null && items.size() > 0){
                        for (ScmDeliveryOrderDetailResponseItem item : items) {
                            Long sentNum = item.getActualQty();
                            //获取发货详情
                            outboundDetail = new OutboundDetail();
                            outboundDetail.setOutboundOrderCode(outboundOrderCode);
                            outboundDetail.setWarehouseItemId(item.getItemId());
                            outboundDetail = outboundDetailService.selectOne(outboundDetail);

                            outboundDetailLogistics = new OutboundDetailLogistics();
                            outboundDetailLogistics.setOutboundDetailId(outboundDetail.getId());
                            outboundDetailLogistics.setLogisticsCorporation(logisticsName);
                            outboundDetailLogistics.setLogisticsCode(logisticsCode);
                            outboundDetailLogistics.setItemNum(sentNum);
                            outboundDetailLogistics.setWaybillNumber(expressCode);
                            outboundDetailLogistics.setCreateTime(Calendar.getInstance().getTime());
                            outboundDetailLogistics.setUpdateTime(Calendar.getInstance().getTime());
                            outboundDetailLogistics.setDeliverTime(Calendar.getInstance().getTime());
                            //保存信息
                            outboundDetailLogisticsService.insert(outboundDetailLogistics);
                            //获取实际到货数量
                            Long count = this.getItemNum(outboundDetail.getId());
                            outboundDetail.setRealSentItemNum(count);
                            if (count >= outboundDetail.getShouldSentItemNum()) {
                                outboundDetail.setStatus(OutboundDetailStatusEnum.ALL_GOODS.getCode());
                            } else {
                                outboundDetail.setStatus(OutboundDetailStatusEnum.PART_OF_SHIPMENT.getCode());
                            }
                            outboundDetail.setUpdateTime(Calendar.getInstance().getTime());
                            outboundDetailService.updateByPrimaryKey(outboundDetail);

                            RequsetUpdateStock requsetUpdateStock = new RequsetUpdateStock();
                            Map<String, String> stockType = new HashMap<>();
                            stockType.put("frozen_inventory", String.valueOf((-1 * sentNum)));
                            stockType.put("real_inventory", String.valueOf((-1 * sentNum)));
                            requsetUpdateStock.setStockType(stockType);
                            requsetUpdateStock.setChannelCode("TRMALL");
                            requsetUpdateStock.setSkuCode(outboundDetail.getSkuCode());
                            requsetUpdateStock.setWarehouseCode(warehouseCode);
                            updateStockList.add(requsetUpdateStock);
                        }
                    }
                }
            }

            if(flag){
                OutboundDetail outboundDetail1 = new OutboundDetail();
                outboundDetail1.setOutboundOrderCode(outboundOrderCode);
                List<OutboundDetail> outboundDetailList = outboundDetailService.select(outboundDetail);
                for(OutboundDetail o : outboundDetailList){
                    o.setRealSentItemNum(o.getShouldSentItemNum());
                    o.setStatus(OutboundDetailStatusEnum.ALL_GOODS.getCode());
                    o.setUpdateTime(Calendar.getInstance().getTime());
                    outboundDetailService.updateByPrimaryKey(o);

                    outboundDetailLogistics = new OutboundDetailLogistics();
                    outboundDetailLogistics.setOutboundDetailId(o.getId());
                    outboundDetailLogistics.setLogisticsCorporation(logisticsName);
                    outboundDetailLogistics.setLogisticsCode(logisticsCode);
                    outboundDetailLogistics.setWaybillNumber(wayBill);
                    outboundDetailLogistics.setCreateTime(Calendar.getInstance().getTime());
                    outboundDetailLogistics.setUpdateTime(Calendar.getInstance().getTime());
                    //保存信息
                    outboundDetailLogisticsService.insert(outboundDetailLogistics);

                    RequsetUpdateStock requsetUpdateStock = new RequsetUpdateStock();
                    Map<String, String> stockType = new HashMap<>();
                    stockType.put("frozen_inventory", String.valueOf((-1 * o.getShouldSentItemNum())));
                    stockType.put("real_inventory", String.valueOf((-1 * o.getShouldSentItemNum())));
                    requsetUpdateStock.setStockType(stockType);
                    requsetUpdateStock.setChannelCode("TRMALL");
                    requsetUpdateStock.setSkuCode(o.getSkuCode());
                    requsetUpdateStock.setWarehouseCode(warehouseCode);
                    updateStockList.add(requsetUpdateStock);
                }
            }
        }

        return updateStockList;
    }

    private Long getItemNum(Long outboundDetailId){
        OutboundDetailLogistics logistics = new OutboundDetailLogistics();
        logistics.setOutboundDetailId(outboundDetailId);
        List<OutboundDetailLogistics> logisticsList = outboundDetailLogisticsService.select(logistics);
        long count = 0;
        for(OutboundDetailLogistics log : logisticsList){
            count += log.getItemNum();
        }
        return count;
    }

    //获取时间
    private Date getTime(String operateTime) throws Exception{
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.parse(operateTime);
    }

    //比较时间
    private boolean compareDeliverTime(Date oldDate, String newTime) throws Exception{
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date newDate = sdf.parse(newTime);
        if(oldDate.getTime() > newDate.getTime()){
            return false;
        }
        return true;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @OutboundOrderCacheEvict
    public Response createOutbound(String outboundOrderId,AclUserAccreditInfo aclUserAccreditInfo) throws Exception {
        AssertUtil.notBlank(outboundOrderId,"ID不能为空");
        //根据id获取到发货通知单
        OutboundOrder outboundOrder = outBoundOrderService.selectByPrimaryKey(Long.valueOf(outboundOrderId));
        String outboundOrderCode = outboundOrder.getOutboundOrderCode();
        Long id = outboundOrder.getId();
        AssertUtil.notNull(outboundOrder,"根据发货通知单id获取发货通知单记录为空");
        Long warehouseId = outboundOrder.getWarehouseId();
        WarehouseInfo warehouse = warehouseInfoService.selectByPrimaryKey(warehouseId);
        Example example = new Example(OutboundDetail.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("outboundOrderCode",outboundOrder.getOutboundOrderCode());
        List<OutboundDetail> outboundDetails = outboundDetailService.selectByExample(example);
        AssertUtil.isTrue(outboundDetails.size()!=0,"发货通知单详情记录不能为空");
        //参数校验
        logger.info("发货通知单验参开始------->");
        verifyParam(warehouse,outboundOrder,outboundDetails);
        logger.info("发货通知单验参完成，开始给参数赋值------->");
        if(StringUtils.equals(outboundOrder.getStatus(), OutboundOrderStatusEnum.CANCELED.getCode())){
            //更新库存
            skuStockService.updateSkuStock(this.getStock(outboundOrder.getOutboundOrderCode(),
                    outboundOrder.getWarehouseCode(), outboundOrder.getChannelCode(), true));
        }
        //设置发货通知单参数
        Map<String, OutboundForm> outboundMap = new HashMap<>();
        OutboundForm outboundForm = new OutboundForm();
        outboundForm.setOutboundOrder(outboundOrder);
        outboundForm.setOutboundDetailList(outboundDetails);
        outboundMap.put(outboundOrder.getOutboundOrderCode(), outboundForm);

        logger.info("请求参数赋值完成，开始调用奇门接口-------->");
        // 重新发货
        String identifier = "";
        String msg = "";
        AppResult<List<ScmDeliveryOrderCreateResponse>> result=null;
        try{
            identifier = redisLock.Lock(DistributeLockEnum.DELIVERY_ORDER_CREATE.getCode() + CREATE_OUTBOUND+outboundOrderCode, 500, 3000);
            if (StringUtils.isNotBlank(identifier)) {
                //查询发货通知单状态，如果是成功，则返回，不在掉用接口
                OutboundOrder outboundOrder01 = outBoundOrderService.selectByPrimaryKey(Long.valueOf(outboundOrderId));
                if (StringUtils.equals(outboundOrder01.getStatus(),ZeroToNineEnum.TWO.getCode())){
                    msg = "已经通知仓库重新发货成功";
                    return ResultUtil.createSuccessResult("已经通知仓库重新发货成功","");
                }
                result = scmOrderBiz.deliveryOrderCreate(outboundMap);

                logger.info("调用奇门接口结束<--------");
                String code = result.getAppcode();
                msg = result.getDatabuffer();
                //调用重新发货接口插入一条日志记录
                logInfoService.recordLog(outboundOrder,outboundOrder.getId().toString(),aclUserAccreditInfo.getUserId(),"发送",null,null);
                if (StringUtils.equals(code,SUCCESS)){
                    List<ScmDeliveryOrderCreateResponse> responses = (List<ScmDeliveryOrderCreateResponse>)result.getResult();
                    if(StringUtils.equals(SUCCESS, responses.get(0).getCode())){
                        updateOutboundDetailState(outboundOrder.getOutboundOrderCode(),OutboundDetailStatusEnum.WAITING.getCode(),id, responses.get(0).getWmsOrderCode());
                        logInfoService.recordLog(outboundOrder,outboundOrder.getId().toString(),warehouse.getWarehouseName(),"仓库接收成功","",null);
                    }else{
                        //仓库接受失败插入一条日志
                        msg = responses.get(0).getMessage();
                        logInfoService.recordLog(outboundOrder,outboundOrder.getId().toString(),warehouse.getWarehouseName(),"仓库接收失败",msg,null);
                        updateOutboundDetailState(outboundOrder.getOutboundOrderCode(),OutboundDetailStatusEnum.RECEIVE_FAIL .getCode(),id, "");
                        logger.error(msg);
                        throw new OutboundOrderException(ExceptionEnum.OUTBOUND_ORDER_EXCEPTION, msg);
                    }
                }else {
                    //仓库接受失败插入一条日志
                    logInfoService.recordLog(outboundOrder,outboundOrder.getId().toString(),warehouse.getWarehouseName(),"仓库接收失败",msg,null);
                    updateOutboundDetailState(outboundOrder.getOutboundOrderCode(),OutboundDetailStatusEnum.RECEIVE_FAIL .getCode(),id, "");
                    logger.error(msg);
                    throw new OutboundOrderException(ExceptionEnum.OUTBOUND_ORDER_EXCEPTION, msg);
                }
                //更新订单信息
                this.updateItemOrderSupplierOrderStatus(outboundOrder.getOutboundOrderCode(), outboundOrder.getWarehouseOrderCode());
                logger.info("outboundOrderCode:{} 发货通知单发送，加锁成功，identifier:{}", outboundOrderCode, identifier);
                return ResultUtil.createSuccessResult("重新发货成功","");
            } else {
                //获取锁失败
                logger.error("重新发货失败:{} 发货通知单发送，获取锁失败，skuStockId:{}，identifier:{}",
                        JSON.toJSONString(outboundMap), outboundOrderCode, identifier);
                return ResultUtil.createfailureResult(Integer.parseInt(ExceptionEnum.OUTBOUND_ORDER_EXCEPTION.getCode()),"操作失败，请重试");
            }
        }catch (Exception e){
            e.printStackTrace();
            logger.error("重新发货失败:{} 发送发货通知单后，更新更新状态失败，outboundOrderCode:{}，identifier:{}, err:{}",
                    JSON.toJSONString(outboundMap), outboundOrderCode, identifier, e.getMessage());
            return ResultUtil.createfailureResult(Integer.parseInt(ExceptionEnum.OUTBOUND_ORDER_EXCEPTION.getCode()),"重新发货失败");
        }finally {
            if (redisLock.releaseLock(DistributeLockEnum.DELIVERY_ORDER_CREATE.getCode() + outboundOrderCode, identifier)) {
                logger.info("outboundOrderCode:{} 发货通知单发送，解锁成功，identifier:{}", outboundOrderCode, identifier);
            } else {
                logger.info("outboundOrderCode:{} 发货通知单发送，解锁失败，identifier:{}", outboundOrderCode, identifier);
            }
        }
    }

    @Override
    public Response getOutboundOrderDetail(Long id) {
        try{
            AssertUtil.notNull(id, "发货单主键不能为空!");
            OutboundOrder outboundOrder = outBoundOrderService.selectByPrimaryKey(id);
            OutboundDetail outboundDetail = new OutboundDetail();
            outboundDetail.setOutboundOrderCode(outboundOrder.getOutboundOrderCode());
            List<OutboundDetail> outboundDetailList = outboundDetailService.select(outboundDetail);
            for(OutboundDetail detail : outboundDetailList){
                OutboundDetailLogistics outboundDetailLogistics = new OutboundDetailLogistics();
                outboundDetailLogistics.setOutboundDetailId(detail.getId());
                List<OutboundDetailLogistics> outboundDetailLogisticsList = outboundDetailLogisticsService.select(outboundDetailLogistics);
                detail.setOutboundDetailLogisticsList(outboundDetailLogisticsList);
            }
            outboundOrder.setOutboundDetailList(outboundDetailList);
            outboundOrder.setWarehouseName(warehouseInfoService.selectByPrimaryKey(outboundOrder.getWarehouseId()).getWarehouseName());
            return ResultUtil.createSuccessResult("获取发货通知单详情成功！", outboundOrder);
        }catch(Exception e){
            logger.error(e.getMessage(), e);
            return ResultUtil.createfailureResult(Response.Status.BAD_REQUEST.getStatusCode(), "获取发货通知单详情失败！", "");
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @OutboundOrderCacheEvict
    public Response close(Long id, String remark,  AclUserAccreditInfo aclUserAccreditInfo) {
        try{
            AssertUtil.notNull(id, "发货单主键不能为空");
            AssertUtil.notBlank(remark, "关闭不能为空");

            //获取发货单信息
            OutboundOrder outboundOrder = outBoundOrderService.selectByPrimaryKey(id);

            if(!StringUtils.equals(outboundOrder.getStatus(), OutboundOrderStatusEnum.RECEIVE_FAIL.getCode())){
                String msg = "发货通知单状态必须为仓库接收失败!";
                logger.error(msg);
                throw new OutboundOrderException(ExceptionEnum.OUTBOUND_ORDER_EXCEPTION, msg);
            }

            //修改状态
            this.updateDetailStatus(OutboundDetailStatusEnum.CANCELED.getCode(), outboundOrder.getOutboundOrderCode());
            this.updateOrderCancelInfo(outboundOrder, remark, true);

            //更新库存
            skuStockService.updateSkuStock(this.getStock(outboundOrder.getOutboundOrderCode(),
                    outboundOrder.getWarehouseCode(), outboundOrder.getChannelCode(), false));

            //更新订单信息
            this.updateItemOrderSupplierOrderStatus(outboundOrder.getOutboundOrderCode(), outboundOrder.getWarehouseOrderCode());

            //仓库接受失败插入一条日志
            String userId = aclUserAccreditInfo.getUserId();
            logInfoService.recordLog(outboundOrder, String.valueOf(outboundOrder.getId()),userId,"手工关闭", remark,null);
            return ResultUtil.createSuccessResult("发货通知单关闭成功！", "");
        }catch(Exception e){
            String msg = e.getMessage();
            logger.error(msg, e);
            return ResultUtil.createfailureResult(Response.Status.BAD_REQUEST.getStatusCode(), msg, "");
        }
    }

    //获取取消详情
    private List<RequsetUpdateStock> getStock(String outboundOrderCode, String warehouseCode, String channelCode, boolean isChanel) throws Exception{
        OutboundDetail outboundDetail = new OutboundDetail();
        outboundDetail.setOutboundOrderCode(outboundOrderCode);
        List<OutboundDetail> outboundDetailList = outboundDetailService.select(outboundDetail);
        List<RequsetUpdateStock> updateStockList = new ArrayList<RequsetUpdateStock>();

        for(OutboundDetail detail : outboundDetailList){
            RequsetUpdateStock requsetUpdateStock = new RequsetUpdateStock();
            Map<String, String> stockType = new HashMap<String, String>();
            if(isChanel){
                stockType.put("frozen_inventory", String.valueOf((detail.getShouldSentItemNum())));
            }else{
                stockType.put("frozen_inventory", String.valueOf((-1*detail.getShouldSentItemNum())));
            }
            requsetUpdateStock.setStockType(stockType);
            requsetUpdateStock.setChannelCode("TRMALL");
            requsetUpdateStock.setSkuCode(detail.getSkuCode());
            requsetUpdateStock.setWarehouseCode(warehouseCode);
            updateStockList.add(requsetUpdateStock);
        }
        return updateStockList;
    }

    @Override
    @OutboundOrderCacheEvict
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Response cancelClose(Long id, AclUserAccreditInfo aclUserAccreditInfo) {
        try{
            AssertUtil.notNull(id, "发货单主键不能为空");

            //获取发货单信息
            OutboundOrder outboundOrder = outBoundOrderService.selectByPrimaryKey(id);

            if(!StringUtils.equals(outboundOrder.getIsClose(), ZeroToNineEnum.ONE.getCode())){
                String msg = "发货通知单没有关闭!";
                logger.error(msg);
                throw new OutboundOrderException(ExceptionEnum.OUTBOUND_ORDER_EXCEPTION, msg);
            }

            if(this.checkDate(outboundOrder.getUpdateTime())){
                String msg = "发货通知单已经超过7天，不允许取消关闭!";
                logger.error(msg);
                throw new OutboundOrderException(ExceptionEnum.OUTBOUND_ORDER_EXCEPTION, msg);
            }

            //修改状态
            this.updateDetailStatus(OutboundDetailStatusEnum.RECEIVE_FAIL.getCode(), outboundOrder.getOutboundOrderCode());
            this.updateOrderCancelInfoExt(outboundOrder, true, OutboundOrderStatusEnum.RECEIVE_FAIL.getCode());

            //更新库存
            skuStockService.updateSkuStock(this.getStock(outboundOrder.getOutboundOrderCode(),
                    outboundOrder.getWarehouseCode(), outboundOrder.getChannelCode(), true));

            //更新订单信息
            this.updateItemOrderSupplierOrderStatus(outboundOrder.getOutboundOrderCode(), outboundOrder.getWarehouseOrderCode());

            String userId = aclUserAccreditInfo.getUserId();
            logInfoService.recordLog(outboundOrder, String.valueOf(outboundOrder.getId()), userId,"取消关闭", "",null);
            return ResultUtil.createSuccessResult("取消关闭成功！", "");
        }catch(Exception e){
            String msg = e.getMessage();
            logger.error(msg, e);
            return ResultUtil.createfailureResult(Response.Status.BAD_REQUEST.getStatusCode(), msg, "");
        }
    }

    private void updateOrderCancelInfoExt(OutboundOrder outboundOrder, boolean isClose, String code){
        outboundOrder.setStatus(code);
        if(isClose){
            outboundOrder.setIsClose(ZeroToNineEnum.ZERO.getCode());
        }else{
            outboundOrder.setIsCancel(ZeroToNineEnum.ZERO.getCode());
        }
        outboundOrder.setUpdateTime(Calendar.getInstance().getTime());
        outboundOrder.setRemark("");
        outBoundOrderService.updateByPrimaryKey(outboundOrder);
    }

    private boolean checkDate(Date updateTime){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(updateTime);
        calendar.add(Calendar.DATE, Integer.parseInt(ZeroToNineEnum.SEVEN.getCode()));
        if(calendar.compareTo(Calendar.getInstance()) == 1){
            return false;
        }
        return true;
    }

    @Override
    @OutboundOrderCacheEvict
    public void checkTimeOutTimer() {
        logger.info("检查出库通知单是否超过七天的定时任务启动----->");
        Example example = new Example(OutboundOrder.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("status", OutboundOrderStatusEnum.CANCELED.getCode());
        List<OutboundOrder> list = outBoundOrderService.selectByExample(example);
        if (list.size() == 0) {
            logger.info("没有超过七天的出库的已取消发货通知单");
            return;
        }
        for (OutboundOrder outboundOrder : list) {
            //比较时间是否超过7天
            Boolean checkResult = checkDate(outboundOrder.getUpdateTime());
            if (checkResult){
                //超过7天的则将is_timeOut更新为1
                OutboundOrder update = new OutboundOrder();
                update.setIsTimeOut(ZeroToNineEnum.ONE.getCode());
                update.setId(outboundOrder.getId());
                int cout = outBoundOrderService.updateByPrimaryKeySelective(update);
                if (cout==0){
                    logger.info("更新数据库超过七天的发货通知单%s失败，",outboundOrder.getOutboundOrderCode());
                }
            }
        }
    }


    private void updateOutboundDetailState(String outboundOrderCode,String state,Long id, String deliveryOrderCode){
        logger.info("开始更新发货通知单详情表状态");
        Example example = new Example(OutboundDetail.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("outboundOrderCode",outboundOrderCode);
        OutboundDetail outboundDetail = new OutboundDetail();
        outboundDetail.setStatus(state);
        int count = outboundDetailService.updateByExampleSelective(outboundDetail,example);
        if (count == 0){
            String msg = String.format("创建发货单%s后，更新发货通知单详情表状态失败",outboundOrderCode);
            logger.error(msg);
            throw new OutboundOrderException(ExceptionEnum.OUTBOUND_ORDER_EXCEPTION, msg);
        }
        logger.info("更新仓库详情表状态完成<---------");
        OutboundOrder outboundOrder = new OutboundOrder();
        outboundOrder.setId(id);
        outboundOrder.setIsCancel(ZeroToNineEnum.ZERO.getCode());
        //找出发货通知单编号下所有记录，更新出库通知单状态
        List<OutboundDetail> list = outboundDetailService.selectByExample(example);
        String outboundOrderStatus = this.getOutboundOrderStatusByDetail(list);
        outboundOrder.setStatus(outboundOrderStatus);
        outboundOrder.setWmsOrderCode(deliveryOrderCode);
        count = outBoundOrderService.updateByPrimaryKeySelective(outboundOrder);
        if (count == 0){
            String msg = String.format("创建发货单%s后，更新发货通知表状态失败",outboundOrderCode);
            logger.error(msg);
            throw new OutboundOrderException(ExceptionEnum.OUTBOUND_ORDER_EXCEPTION, msg);
        }
    }

    private ScmDeliveryOrderCreateRequest setParam(WarehouseInfo warehouse,OutboundOrder outboundOrder,List<OutboundDetail> outboundDetails){
        ScmDeliveryOrderCreateRequest request = new ScmDeliveryOrderCreateRequest();
        List<ScmDeliveryOrderDO> scmDeleveryOrderDOList = new ArrayList<>();


        DeliveryorderCreateRequest.DeliveryOrder deliveryOrder =  new DeliveryorderCreateRequest.DeliveryOrder();
        deliveryOrder.setDeliveryOrderCode(outboundOrder.getOutboundOrderCode());
        deliveryOrder.setOrderType(outboundOrder.getOrderType());
        deliveryOrder.setWarehouseCode(outboundOrder.getWarehouseCode());
        deliveryOrder.setCreateTime(DateUtils.formatDateTime(outboundOrder.getCreateTime()));
        deliveryOrder.setPlaceOrderTime(DateUtils.formatDateTime(outboundOrder.getPayTime()));
        deliveryOrder.setOperateTime(DateUtils.formatDateTime(outboundOrder.getCreateTime()));
        deliveryOrder.setShopNick(outboundOrder.getShopName());
        deliveryOrder.setSourcePlatformCode(SupplyConstants.SourcePlatformCodeType.OTHER);
        DeliveryorderCreateRequest.SenderInfo senderInfo = new DeliveryorderCreateRequest.SenderInfo();
        senderInfo.setName(warehouse.getWarehouseName());
        senderInfo.setMobile(warehouse.getSenderPhoneNumber());
        senderInfo.setProvince(warehouse.getProvince());
        senderInfo.setCity(warehouse.getCity());
        senderInfo.setDetailAddress(warehouse.getAddress());
        deliveryOrder.setSenderInfo(senderInfo);
        DeliveryorderCreateRequest.ReceiverInfo receiverInfo = new DeliveryorderCreateRequest.ReceiverInfo();
        receiverInfo.setName(outboundOrder.getReceiverName());
        receiverInfo.setMobile(outboundOrder.getReceiverPhone());
        receiverInfo.setProvince(outboundOrder.getReceiverProvince());
        receiverInfo.setCity(outboundOrder.getReceiverCity());
        receiverInfo.setDetailAddress(outboundOrder.getReceiverAddress());
        deliveryOrder.setReceiverInfo(receiverInfo);
        deliveryOrder.setSellerMessage(outboundOrder.getSellerMessage());
        deliveryOrder.setBuyerMessage(outboundOrder.getBuyerMessage());
        List<DeliveryorderCreateRequest.OrderLine> orderLines = new ArrayList<>();
        for (OutboundDetail outboundDetail : outboundDetails){
            DeliveryorderCreateRequest.OrderLine orderLine = new DeliveryorderCreateRequest.OrderLine();
            orderLine.setOwnerCode(outboundOrder.getChannelCode());
            orderLine.setItemCode(outboundDetail.getSkuCode());
            orderLine.setInventoryType(outboundDetail.getInventoryType());
            orderLine.setPlanQty(String.valueOf(outboundDetail.getShouldSentItemNum()));
            orderLine.setActualPrice(String.valueOf(CommonUtil.fenToYuan(outboundDetail.getActualAmount())));
            orderLines.add(orderLine);
        }
        deliveryOrder.setOrderLines(orderLines);
//        request.setDeliveryOrder(deliveryOrder);
        return request;
    }

    @Override
    @OutboundOrderCacheEvict
    @Transactional(rollbackFor = Exception.class)
    public Response orderCancel(Long id, String remark, AclUserAccreditInfo aclUserAccreditInfo) {
        AssertUtil.notNull(id, "发货单主键不能为空");
        AssertUtil.notBlank(remark, "取消原因不能为空");

        //上锁防止重复调用奇门接口
        String identifier = redisLock.Lock(DistributeLockEnum.DELIVERY_ORDER_CREATE.getCode() +"orderCancel"+ id, 50000, 100000);

        if (StringUtils.isBlank(identifier)){
            String msg = "发货单Id为"+id+"未获取到锁！";
            logger.error(msg);
            return ResultUtil.createfailureResult(Response.Status.BAD_REQUEST.getStatusCode(), msg, "");
        }
        try{
            //获取发货单信息
            OutboundOrder outboundOrder = outBoundOrderService.selectByPrimaryKey(id);

            if(!StringUtils.equals(outboundOrder.getStatus(), OutboundOrderStatusEnum.WAITING.getCode())){
                String msg = "发货通知单状态必须为等待仓库发货!";
                logger.error(msg);
                throw new OutboundOrderException(ExceptionEnum.OUTBOUND_ORDER_EXCEPTION, msg);
            }

            //获取仓库信息
            WarehouseInfo warehouse =warehouseInfoService.selectByPrimaryKey(outboundOrder.getWarehouseId());

            //组装请求
            ScmOrderCancelRequest scmOrderCancelRequest = new ScmOrderCancelRequest();
            scmOrderCancelRequest.setCancelReason(remark);
            scmOrderCancelRequest.setOrderCode(outboundOrder.getWmsOrderCode());
            scmOrderCancelRequest.setOwnerCode(warehouse.getWarehouseOwnerId());


            //组装请求信息
            ScmDeliveryOrderDetailRequest request = new ScmDeliveryOrderDetailRequest();
            request.setOrderCode(outboundOrder.getOutboundOrderCode());
            request.setOrderId(outboundOrder.getWmsOrderCode());
            request.setOwnerCode(warehouse.getWarehouseOwnerId());
            request.setWarehouseCode(warehouse.getCode());
            AppResult<ScmDeliveryOrderDetailResponse>  result = warehouseApiService.deliveryOrderDetail(request);
            String userId = aclUserAccreditInfo.getUserId();
            if(StringUtils.equals(result.getAppcode(), SUCCESS)){
                ScmDeliveryOrderDetailResponse response = (ScmDeliveryOrderDetailResponse)result.getResult();
                if(response == null || StringUtils.isEmpty(response.getCurrentStatus())){
                    String msg = "发货通知单状态查询为空，无法取消!";
                    logger.error(msg);
                    logInfoService.recordLog(outboundOrder, String.valueOf(outboundOrder.getId()),userId,
                            "取消发货", "取消原因:"+remark+"<br>取消结果:取消失败,"+msg,
                            null);
                    throw new OutboundOrderException(ExceptionEnum.OUTBOUND_ORDER_EXCEPTION, msg);
                }else if(response.getCurrentStatus() != null && this.isCompound(response.getCurrentStatus())){
                    String msg = "订单已完成复核流程，无法取消!";
                    logger.error(msg);
                    logInfoService.recordLog(outboundOrder, String.valueOf(outboundOrder.getId()),userId,
                            "取消发货", "取消原因:"+remark+"<br>取消结果:取消失败,"+msg,
                            null);
                    throw new OutboundOrderException(ExceptionEnum.OUTBOUND_ORDER_EXCEPTION, msg);
                }
            }else{
                String msg = "获取发货单状态失败!";
                logger.error(msg);
                logInfoService.recordLog(outboundOrder, String.valueOf(outboundOrder.getId()),userId,
                        "取消发货", "取消原因:"+remark+"<br>取消结果:取消失败,"+msg,
                        null);
                throw new OutboundOrderException(ExceptionEnum.OUTBOUND_ORDER_EXCEPTION, msg);
            }

            //调用奇门接口
            AppResult<ScmOrderCancelResponse> appResult = warehouseApiService.orderCancel(scmOrderCancelRequest);

            //处理信息
            if (StringUtils.equals(appResult.getAppcode(), SUCCESS)) { // 成功
                ScmOrderCancelResponse response = (ScmOrderCancelResponse)appResult.getResult();
                String flag = response.getFlag();
                if(StringUtils.equals(flag, ZeroToNineEnum.ONE.getCode())){
                    this.updateDetailStatus(OutboundDetailStatusEnum.CANCELED.getCode(), outboundOrder.getOutboundOrderCode());
                    this.updateOrderCancelInfo(outboundOrder, remark,false);

                    //更新库存
                    skuStockService.updateSkuStock(this.getStock(outboundOrder.getOutboundOrderCode(),
                            outboundOrder.getWarehouseCode(), outboundOrder.getChannelCode(), false));

                    logInfoService.recordLog(outboundOrder, String.valueOf(outboundOrder.getId()),userId,
                            "取消发货", "取消原因:"+remark+"<br>取消结果:取消成功",
                            null);

                    //更新订单信息
                    this.updateItemOrderSupplierOrderStatus(outboundOrder.getOutboundOrderCode(), outboundOrder.getWarehouseOrderCode());
                    return ResultUtil.createSuccessResult("发货通知单取消成功！", "");
                }else if(StringUtils.equals(flag, ZeroToNineEnum.THREE.getCode())){
                    //修改发货单信息
                    outboundOrder.setStatus(OutboundOrderStatusEnum.ON_CANCELED.getCode());
                    outboundOrder.setUpdateTime(Calendar.getInstance().getTime());
                    outboundOrder.setRemark(remark);
                    outBoundOrderService.updateByPrimaryKey(outboundOrder);

                    OutboundDetail outboundDetail = new OutboundDetail();
                    outboundDetail.setStatus(OutboundDetailStatusEnum.ON_CANCELED.getCode());
                    outboundDetail.setUpdateTime(Calendar.getInstance().getTime());
                    Example example = new Example(OutboundDetail.class);
                    Example.Criteria criteria = example.createCriteria();
                    criteria.andEqualTo("outboundOrderCode", outboundOrder.getOutboundOrderCode());
                    outboundDetailService.updateByExampleSelective(outboundDetail, example);

                    //更新订单信息
                    this.updateItemOrderSupplierOrderStatus(outboundOrder.getOutboundOrderCode(), outboundOrder.getWarehouseOrderCode());

                    return ResultUtil.createSuccessResult("发货通知单取消中！", "");
                }else{
                    logInfoService.recordLog(outboundOrder, String.valueOf(outboundOrder.getId()),userId,
                            "取消发货", "取消原因:"+remark+"<br>取消结果:取消失败,"+response.getMessage(),
                            null);
                    return ResultUtil.createfailureResult(Response.Status.BAD_REQUEST.getStatusCode(), "发货通知单取消失败！", "");
                }
            } else {
                return ResultUtil.createfailureResult(Response.Status.BAD_REQUEST.getStatusCode(), "发货通知单取消失败！", "");
            }
        }catch(Exception e){
            String msg = e.getMessage();
            logger.error(msg, e);
            return ResultUtil.createfailureResult(Response.Status.BAD_REQUEST.getStatusCode(), msg, "");
        }finally {
            //释放锁
            if (redisLock.releaseLock(DistributeLockEnum.DELIVERY_ORDER_CREATE.getCode() +"orderCancel"+ id, identifier)) {
                logger.info(DistributeLockEnum.DELIVERY_ORDER_CREATE.getCode() +"orderCancel"+ id + "已释放！");
            } else {
                logger.error(DistributeLockEnum.DELIVERY_ORDER_CREATE.getCode() +"orderCancel"+ id + "解锁失败！");
            }
        }

    }

    @Override
    public void retryCancelOrder() {
        //获取所有取消中的发货单
        OutboundOrder orderTemp = new OutboundOrder();
        orderTemp.setStatus(OutboundOrderStatusEnum.ON_CANCELED.getCode());
        List<OutboundOrder> list = outBoundOrderService.select(orderTemp);

        //组装信息
        List<ScmOrderCancelRequest> requests = new ArrayList<>();
        for(OutboundOrder order : list){
            WarehouseInfo warehouse =warehouseInfoService.selectByPrimaryKey(order.getWarehouseId());
            ScmOrderCancelRequest scmOrderCancelRequest = new ScmOrderCancelRequest();
            scmOrderCancelRequest.setCancelReason(order.getRemark());
            scmOrderCancelRequest.setOrderCode(order.getWmsOrderCode());
            scmOrderCancelRequest.setOwnerCode(warehouse.getWarehouseOwnerId());
            requests.add(scmOrderCancelRequest);
        }

        //调用接口
        this.retryCancelOrder(requests);
    }

    //调用获取商品详情接口
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
                        logger.error("仓库发货单号:{},取消发货单异常：{}", request.getOrderCode(), responseAppResult.getResult());
                    }
                }).start();
            }
        }catch(Exception e){
            logger.error("取消发货单失败", e);
        }
    }

    private void updateCancelOrder(AppResult<ScmOrderCancelResponse> appResult, String orderCode){
        //处理信息
        try{
            if (StringUtils.equals(appResult.getAppcode(), SUCCESS)) { // 成功
                //获取发货单
                OutboundOrder outboundOrderTemp = new OutboundOrder();
                outboundOrderTemp.setWmsOrderCode(orderCode);
                OutboundOrder outboundOrder = outBoundOrderService.selectOne(outboundOrderTemp);

                ScmOrderCancelResponse response = (ScmOrderCancelResponse)appResult.getResult();
                String flag = response.getFlag();
                if(StringUtils.equals(flag, ZeroToNineEnum.ONE.getCode())){
                    this.updateDetailStatus(OutboundDetailStatusEnum.CANCELED.getCode(), outboundOrder.getOutboundOrderCode());
                    this.updateOrderCancelInfo(outboundOrder, outboundOrder.getRemark(),false);

                    //更新库存
                    skuStockService.updateSkuStock(this.getStock(outboundOrder.getOutboundOrderCode(),
                            outboundOrder.getWarehouseCode(), outboundOrder.getChannelCode(), false));

                    logInfoService.recordLog(outboundOrder, String.valueOf(outboundOrder.getId()),"admin",
                            "取消发货", "取消原因:"+outboundOrder.getRemark()+"<br>取消结果:取消成功",
                            null);

                    //更新订单信息
                    this.updateItemOrderSupplierOrderStatus(outboundOrder.getOutboundOrderCode(), outboundOrder.getWarehouseOrderCode());
                }else if(StringUtils.equals(flag, ZeroToNineEnum.TWO.getCode())){
                    outboundOrder.setStatus(OutboundOrderStatusEnum.WAITING.getCode());
                    outboundOrder.setUpdateTime(Calendar.getInstance().getTime());
                    outBoundOrderService.updateByPrimaryKey(outboundOrder);

                    OutboundDetail outboundDetail = new OutboundDetail();
                    outboundDetail.setStatus(OutboundDetailStatusEnum.WAITING.getCode());
                    outboundDetail.setUpdateTime(Calendar.getInstance().getTime());
                    Example example = new Example(OutboundDetail.class);
                    Example.Criteria criteria = example.createCriteria();
                    criteria.andEqualTo("outboundOrderCode", outboundOrder.getOutboundOrderCode());
                    outboundDetailService.updateByExampleSelective(outboundDetail, example);

                    //更新订单信息
                    this.updateItemOrderSupplierOrderStatus(outboundOrder.getOutboundOrderCode(), outboundOrder.getWarehouseOrderCode());

                    logInfoService.recordLog(outboundOrder, String.valueOf(outboundOrder.getId()),"admin",
                            "取消发货", "取消原因:"+outboundOrder.getRemark()+"<br>取消结果:取消失败,"+response.getMessage(),
                            null);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
            logger.error("仓库发货单号:{},取消发货单异常：{}", orderCode, e.getMessage());
        }
    }

    //修改详情状态
    private void updateDetailStatus(String code, String outboundOrderCode){
        OutboundDetail outboundDetail = new OutboundDetail();
        outboundDetail.setStatus(code);
        outboundDetail.setUpdateTime(Calendar.getInstance().getTime());
        Example exampleOrder = new Example(OutboundDetail.class);
        Example.Criteria criteriaOrder = exampleOrder.createCriteria();
        criteriaOrder.andEqualTo("outboundOrderCode", outboundOrderCode);
        outboundDetailService.updateByExampleSelective(outboundDetail, exampleOrder);
    }

    //修改取消发货单信息
    private void updateOrderCancelInfo(OutboundOrder outboundOrder, String remark, boolean isClose){
        outboundOrder.setStatus(OutboundOrderStatusEnum.CANCELED.getCode());
        if(isClose){
            outboundOrder.setIsClose(ZeroToNineEnum.ONE.getCode());
        }else {
            outboundOrder.setIsCancel(ZeroToNineEnum.ONE.getCode());
        }
        outboundOrder.setUpdateTime(Calendar.getInstance().getTime());
        outboundOrder.setRemark(remark);
        outBoundOrderService.updateByPrimaryKey(outboundOrder);
    }

    private void verifyParam(WarehouseInfo warehouse,OutboundOrder outboundOrder,List<OutboundDetail> outboundDetails){
        AssertUtil.notBlank(outboundOrder.getOutboundOrderCode(),"出库通知单编号不能为空");
        AssertUtil.notBlank(outboundOrder.getOrderType(),"出库单类型不能为空");
        AssertUtil.notBlank(warehouse.getCode(),"仓库编码不能为空");
        AssertUtil.notNull(outboundOrder.getCreateTime(),"发货单创建时间不能为空");
        AssertUtil.notNull(outboundOrder.getPayTime(),"付款时间不能为空");
        AssertUtil.notBlank(outboundOrder.getShopName(),"店铺名称不能为空");
        AssertUtil.notBlank(warehouse.getWarehouseName(),"发货仓库名称不能为空");
        AssertUtil.notBlank(warehouse.getSenderPhoneNumber(),"运单发件人手机号不能为空");
        AssertUtil.notBlank(warehouse.getProvince(),"发货仓库省份不能为空");
        AssertUtil.notBlank(warehouse.getCity(),"发货仓库城市不能为空");
        AssertUtil.notBlank(warehouse.getAddress(),"发货仓库的详细地址不能为空");
        AssertUtil.notBlank(outboundOrder.getReceiverName(),"收件人姓名不能为空");
        AssertUtil.notBlank(outboundOrder.getReceiverPhone(),"收件人联系方式不能为空");
        AssertUtil.notBlank(outboundOrder.getReceiverProvince(),"收件人省份不能为空");
        AssertUtil.notBlank(outboundOrder.getReceiverCity(),"收件人城市不能为空");
        AssertUtil.notBlank(outboundOrder.getReceiverAddress(),"收件人详细地址不能为空");
        //AssertUtil.notBlank(outboundOrder.getBuyerMessage(),"买家留言不能为空");
        //AssertUtil.notBlank(outboundOrder.getSellerMessage(),"卖家留言不能为空");
        AssertUtil.notBlank(outboundOrder.getChannelCode(),"业务线编码不能为空");
        for (OutboundDetail outboundDetail : outboundDetails){
            AssertUtil.notBlank(outboundDetail.getSkuCode(),"商品sku编号不能为空");
            AssertUtil.notBlank(outboundDetail.getInventoryType(),"库存类型不能为空");
            AssertUtil.notNull(outboundDetail.getShouldSentItemNum(),"应发商品数量不能为空");
            AssertUtil.notNull(outboundDetail.getActualAmount(),"实付总金额不能为空");
        }
    }

    public void setQueryParam(Example example, OutBoundOrderForm form, String channelCode) {
        Example.Criteria criteria = example.createCriteria();
        //发货通知单编号
        if (!StringUtils.isBlank(form.getOutboundOrderCode())) {
            criteria.andLike("outboundOrderCode", "%" + form.getOutboundOrderCode() + "%");

        }
        //业务线
        if (!StringUtils.isBlank(channelCode)) {
            criteria.andEqualTo("channelCode", channelCode);

        }
        //店铺订单编号
        if (!StringUtils.isBlank(form.getShopOrderCode())) {
            criteria.andLike("shopOrderCode", "%" + form.getShopOrderCode() + "%");

        }
        //发货仓库id
        if (!StringUtils.isBlank(form.getWarehouseId())) {
            criteria.andEqualTo("warehouseId", form.getWarehouseId());
        }
        //状态
        if (!StringUtils.isBlank(form.getStatus())) {
            criteria.andEqualTo("status", String.valueOf(form.getStatus()));
        }
        //收货人
        if (!StringUtils.isBlank(form.getReceiverName())) {
            criteria.andLike("receiverName", "%" + form.getReceiverName() + "%");

        }
        //付款时间
        if (!StringUtils.isBlank(form.getStartPayDate())) {
            criteria.andGreaterThan("payTime", form.getStartPayDate());
        }
        if (!StringUtils.isBlank(form.getEndPayDate())) {
            criteria.andLessThan("payTime", DateUtils.formatDateTime(DateUtils.addDays(form.getEndPayDate(), DateUtils.NORMAL_DATE_FORMAT, 1)));
        }
        //平台订单编号
        if (!StringUtils.isBlank(form.getPlatformOrderCode())) {
            criteria.andLike("platformOrderCode", "%" + form.getPlatformOrderCode() + "%");

        }
        //发货单创建日期
        if (!StringUtils.isBlank(form.getStartCreateDate())) {
            criteria.andGreaterThan("createTime", form.getStartCreateDate());
        }
        if (!StringUtils.isBlank(form.getEndCreateDate())) {
            criteria.andLessThan("createTime", DateUtils.formatDateTime(DateUtils.addDays(form.getEndCreateDate(), DateUtils.NORMAL_DATE_FORMAT, 1)));
        }
        example.orderBy("status").asc();
        example.orderBy("createTime").desc();
    }

    //获取状态
    private String getOutboundOrderStatusByDetail(List<OutboundDetail> outboundDetailList){
        int failureNum = 0;//仓库接收失败数
        int waitDeliverNum = 0;//等待发货数
        int allDeliverNum = 0;//全部发货数
        int partsDeliverNum = 0;//部分发货数
        int cancelNum = 0;//已取消数
        for(OutboundDetail detail : outboundDetailList){
            if(StringUtils.equals(OutboundDetailStatusEnum.RECEIVE_FAIL.getCode(), detail.getStatus()))
                failureNum++;
            else if(StringUtils.equals(OutboundDetailStatusEnum.WAITING.getCode(), detail.getStatus()))
                waitDeliverNum++;
            else if(StringUtils.equals(OutboundDetailStatusEnum.ALL_GOODS.getCode(), detail.getStatus()))
                allDeliverNum++;
            else if(StringUtils.equals(OutboundDetailStatusEnum.PART_OF_SHIPMENT.getCode(), detail.getStatus()))
                partsDeliverNum++;
            else if(StringUtils.equals(OutboundDetailStatusEnum.CANCELED.getCode(), detail.getStatus()))
                cancelNum++;
        }
        //已取消：所有商品的发货状态均更新为“已取消”时，发货单的状态就更新为“已取消”；
        if(cancelNum == outboundDetailList.size()){
            return OutboundOrderStatusEnum.CANCELED.getCode();
        }
        //仓库接收失败：所有商品的发货状态均为“仓库接收失败”时，发货单的状态就为“仓库接收失败”
        if(failureNum == outboundDetailList.size()){
            return OutboundOrderStatusEnum.RECEIVE_FAIL.getCode();
        }
        //全部发货：所有商品的发货状态均为“全部发货”时，发货单的状态就为“全部发货”
        if(allDeliverNum == outboundDetailList.size()){
            return OutboundOrderStatusEnum.ALL_GOODS.getCode();
        }
        //部分发货：存在发货状态为“部分发货”的商品或者同时存在待发货和已发货(部分发货或全部发货)的商品，发货单的状态就为“部分发货”
        if(partsDeliverNum > 0 || (waitDeliverNum > 0 && (partsDeliverNum > 0 || allDeliverNum > 0))){
            return OutboundOrderStatusEnum.PART_OF_SHIPMENT.getCode();
        }
        return OutboundOrderStatusEnum.WAITING.getCode();
    }

    public void setQimenService(IWarehouseApiService service) {
        this.warehouseApiService = service;

    }



}
