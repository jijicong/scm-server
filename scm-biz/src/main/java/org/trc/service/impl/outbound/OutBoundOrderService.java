package org.trc.service.impl.outbound;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.trc.biz.impl.outbound.OutBoundOrderBiz;
import org.trc.biz.order.IOrderExtBiz;
import org.trc.biz.order.IScmOrderBiz;
import org.trc.common.RequsetUpdateStock;
import org.trc.constant.RequestFlowConstant;
import org.trc.constants.SupplyConstants;
import org.trc.domain.System.LogisticsCompany;
import org.trc.domain.config.RequestFlow;
import org.trc.domain.order.*;
import org.trc.domain.warehouseInfo.WarehouseInfo;
import org.trc.enums.*;
import org.trc.enums.warehouse.CancelOrderType;
import org.trc.exception.OutboundOrderException;
import org.trc.form.*;
import org.trc.form.warehouse.*;
import org.trc.model.ToGlyResultDO;
import org.trc.service.config.ILogInfoService;
import org.trc.service.goods.ISkuStockService;
import org.trc.service.impl.BaseService;
import org.trc.service.impl.TrcService;
import org.trc.service.impl.config.RequestFlowService;
import org.trc.service.impl.system.LogisticsCompanyService;
import org.trc.service.order.IOrderItemService;
import org.trc.service.outbound.IOutBoundOrderService;
import org.trc.service.outbound.IOutboundDetailLogisticsService;
import org.trc.service.outbound.IOutboundDetailService;
import org.trc.service.outbound.IOutboundPackageInfoService;
import org.trc.service.util.IRealIpService;
import org.trc.service.warehouse.IWarehouseApiService;
import org.trc.service.warehouse.IWarehouseExtService;
import org.trc.service.warehouse.IWarehouseMockService;
import org.trc.service.warehouseInfo.IWarehouseInfoService;
import org.trc.util.AppResult;
import org.trc.util.AssertUtil;
import org.trc.util.ParamsUtil;
import org.trc.util.ResponseAck;
import org.trc.util.ResultUtil;
import org.trc.util.lock.RedisLock;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

import javax.ws.rs.core.Response;

@Service("outBoundOrderService")
public class OutBoundOrderService extends BaseService<OutboundOrder, Long> implements IOutBoundOrderService {

    private Logger logger = LoggerFactory.getLogger(OutBoundOrderBiz.class);

    private static final Map<String, String> statusMap = new HashMap<String, String>();

    @Value("${mock.outer.interface}")
    private String mockOuterInterface;

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
    private IWarehouseInfoService warehouseInfoService;
    @Autowired
    private IOutboundPackageInfoService outboundPackageInfoService;
    @Autowired
    private IWarehouseMockService warehouseMockService;
    @Autowired
    private IOrderExtBiz orderExtBiz;
	@Autowired
	private IWarehouseExtService warehouseExtService;

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
                        "取消发货", "仓库平台取消发货", null);

                // 发货单确认结果通知渠道
                scmOrderBiz.outboundOrderSubmitResultNoticeChannel(outboundOrder.getShopOrderCode());
                return ;
            }

            //只获取复合过的发货单详情
            if(response != null && response.getCurrentStatus() != null && this.isCompound(response.getCurrentStatus()) ){
                //组装获取包裹信息
                ScmOrderPacksRequest request = new ScmOrderPacksRequest();
                request.setOrderIds(orderId);
                //调用京东接口获取包裹信息
                AppResult<ScmOrderPacksResponse> packageResponseAppResult = null;
                if(StringUtils.equals(mockOuterInterface, ZeroToNineEnum.ONE.getCode())){//仓库接口mock
                    packageResponseAppResult = warehouseMockService.orderPack(request);
                }else{
                    packageResponseAppResult = warehouseApiService.orderPack(request);
                }
                if(StringUtils.equals("200", packageResponseAppResult.getAppcode())){
                    ScmOrderPacksResponse packsResponse = (ScmOrderPacksResponse) packageResponseAppResult.getResult();

//                    if(!StringUtils.equals(packsResponse.getScmOrderDefaultResults().get(0).getOrderCode(), outboundOrderCode)){
//                        logger.error("发货单号:{},物流信息获取异常", outboundOrderCode);
//                        return;
//                    }

//                    if(!StringUtils.equals(response.getDeliveryOrderCode(), outboundOrderCode)){
//                        logger.error("发货单号:{},物流信息获取异常", outboundOrderCode);
//                        return;
//                    }

                    //更新发货单信息
                    List<RequsetUpdateStock> list = this.updateOutboundDetailAndLogistics(packsResponse,
                            outboundOrder.getWarehouseCode(), outboundOrderCode);

                    //更新发货单状态
                    this.setOutboundOrderStatus(outboundOrderCode, outboundOrder);

                    //更新库存
                    if(list.size() > 0){
                        skuStockService.updateSkuStock(list);
                    }

                    //更新订单信息
                    this.updateItemOrderSupplierOrderStatus(outboundOrderCode, outboundOrder.getWarehouseOrderCode());

                    //记录日志
                    logInfoService.recordLog(outboundOrder, String.valueOf(outboundOrder.getId()), warehouse.getWarehouseName(),
                            LogOperationEnum.SEND.getMessage(),this.getPartSkuInfo(list, outboundOrder.getOutboundOrderCode()), null);

                    // 发货单确认结果通知渠道
                    deliveryOrderConfirmNotice(outboundOrder, response);

                }

            }

        }else{
            logger.error("发货单号:{},物流信息获取异常：{}", outboundOrderCode, responseAppResult.getResult());
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
        criteriaOrder.andIsNull("cancelFlg");
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

    //更新itemOrder
    public void updateItemOrderSupplierOrderStatus(String outboundOrderCode, String warehouseOrderCode){
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

    //更新发货单
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public List<RequsetUpdateStock> updateOutboundDetailAndLogistics(ScmOrderPacksResponse response, String warehouseCode,
                                                                     String outboundOrderCode){
        OutboundDetail outboundDetail = null;
        OutboundDetailLogistics outboundDetailLogistics = null;
        OutboundPackageInfo outboundPackageInfo = null;
        List<OutboundPackageInfo> outboundPackageInfoList = null;
        List<RequsetUpdateStock> updateStockList = new ArrayList<RequsetUpdateStock>();
        List<ScmOrderDefaultResult> results = response.getScmOrderDefaultResults();

        //遍历获取包裹信息
        for(ScmOrderDefaultResult result : results){
//            //发货单号
//            String outboundOrderCode = result.getOrderCode();
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
	      	    Example example = new Example(OutboundDetail.class);
	    	    Example.Criteria cra = example.createCriteria();
	    	    cra.andEqualTo("outboundOrderCode", outboundOrderCode);
	    	    cra.andIsNull("cancelFlg");
	    	    List<OutboundDetail> outboundDetailList = outboundDetailService.selectByExample(example);
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

    //获取状态
    public String getOutboundOrderStatusByDetail(List<OutboundDetail> outboundDetailList){
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
        int detailSize = outboundDetailList.size() - cancelNum;
        //已取消：所有商品的发货状态均更新为“已取消”时，发货单的状态就更新为“已取消”；
        if(cancelNum == outboundDetailList.size()){
            return OutboundOrderStatusEnum.CANCELED.getCode();
        }
        //仓库接收失败：所有商品的发货状态均为“仓库接收失败”时，发货单的状态就为“仓库接收失败”
        if(failureNum == detailSize){
            return OutboundOrderStatusEnum.RECEIVE_FAIL.getCode();
        }
        //全部发货：所有商品的发货状态均为“全部发货”时，发货单的状态就为“全部发货”
        if(allDeliverNum == detailSize){
            return OutboundOrderStatusEnum.ALL_GOODS.getCode();
        }
        //部分发货：存在发货状态为“部分发货”的商品或者同时存在待发货和已发货(部分发货或全部发货)的商品，发货单的状态就为“部分发货”
        if(partsDeliverNum > 0 || (waitDeliverNum > 0 && (partsDeliverNum > 0 || allDeliverNum > 0))){
            return OutboundOrderStatusEnum.PART_OF_SHIPMENT.getCode();
        }
        return OutboundOrderStatusEnum.WAITING.getCode();
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
            lsc.setLogisticsCorporationCode(generateLogisticsCode(logisticsName, outboundOrder.getChannelCode()));
            lsc.setLogisticsStatus(SupplierOrderLogisticsStatusEnum.COMPLETE.getCode());//妥投
            lsc.setLogisticInfo(new ArrayList<>());
            //发货单明细信息
            OutboundDetail outboundDetail = new OutboundDetail();
            outboundDetail.setOutboundOrderCode(outboundOrder.getOutboundOrderCode());
            List<OutboundDetail> outboundDetailList = outboundDetailService.select(outboundDetail);
            AssertUtil.notEmpty(outboundDetailList, String.format("发货单[%s]对应的发货单明细信息为空", outboundOrder.getOutboundOrderCode()));
            /**
             * 包裹对应商品信息
             **/
            List<SkuInfo> skuList = new ArrayList<>();
            if (!CollectionUtils.isEmpty(items)) {
                items.forEach(item -> {
                    SkuInfo sku = new SkuInfo();
                    sku.setNum(item.getActualQty().intValue());
                    for(OutboundDetail detail1: outboundDetailList){
                        if(StringUtils.equals(detail1.getWarehouseItemId(), item.getItemId())){
                            sku.setSkuCode(detail1.getSkuCode());
                            sku.setSkuName(detail1.getSkuName());
                            break;
                        }
                    }
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

    private String generateLogisticsCode(String logisticsName, String channelCode) {
        String retMsg = "物流公司编码未找到";
        if (StringUtils.isBlank(logisticsName)) {
            return retMsg;
        }
        LogisticsCompany logisticsCompany = orderExtBiz.getLogisticsCompanyByName(LogisticsTypeEnum.TRC, logisticsName);
        return logisticsCompany.getCompanyCode();
    }

    //根据发货单查询发货详情
    private List<OutboundDetail> getOutboundDetailListByOutboundOrderCode(String outboundOrderCode, String status){
        OutboundDetail outboundDetail = new OutboundDetail();
        outboundDetail.setOutboundOrderCode(outboundOrderCode);
        outboundDetail.setStatus(status);
        return outboundDetailService.select(outboundDetail);
    }

    //查询orderItem
    private OrderItem getOrderItemByWarehouseOrderCodeAnd(String warehouseOrderCode, String skuCode){
        OrderItem orderItem = new OrderItem();
        orderItem.setWarehouseOrderCode(warehouseOrderCode);
        orderItem.setSkuCode(skuCode);
        return orderItemService.selectOne(orderItem);
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

	@Override
	@Transactional
	public Map<String, String> deliveryCancel(OutboundOrder order, String skuCode) {
		
        if (!OutboundOrderStatusEnum.WAITING.getCode().equals(order.getStatus())) {
        	throw new OutboundOrderException("发货单状态非等待仓库发货状态!");
        }

        String remark = "销售退货单取消发货";
        WarehouseTypeEnum warehouseType = warehouseExtService.getWarehouseType(order.getWarehouseCode());

        if (WarehouseTypeEnum.Jingdong == warehouseType) {
        	
        	return deliveryOrderCancel(order, warehouseType, skuCode, remark);
            
        } else if (WarehouseTypeEnum.Zy == warehouseType) {
        	
        	return selfWarehouseAfterSaleCancel(order, skuCode);
        	
        } else {
        	throw new OutboundOrderException("发货单的仓库类型错误!");
        }

	}
	

	private Map<String, String> selfWarehouseAfterSaleCancel (OutboundOrder order, String skuCode) {
		
		String orderCode = order.getOutboundOrderCode();
		
        //返回结果map
        Map<String, String> resultMap = new HashMap<>();
        
		/**
		 * 通知自营仓库取消发货
		 */
		ScmAfterSaleOrderCancelRequest req = new ScmAfterSaleOrderCancelRequest();
		req.setOutboundOrderCode(orderCode);
		req.setSkuCode(skuCode);
		req.setWarehouseType(WarehouseTypeEnum.Zy.getCode());
		
		AppResult<ScmAfterSaleOrderCancelResponse> appResult = warehouseApiService.afterSaleCancel(req);
		
		if (StringUtils.equals(appResult.getAppcode(), ResponseAck.SUCCESS_CODE)) { // 成功
			
			ScmAfterSaleOrderCancelResponse response = (ScmAfterSaleOrderCancelResponse) appResult.getResult();
            
			String flag = response.getFlag();
            
			if (StringUtils.equals(flag, OrderCancelResultEnum.CANCEL_SUCC.code)) { // 取消成功
				/**
				 * 更新发货单商品状态为已取消
				 */
				
				updateDetail(skuCode, ZeroToNineEnum.ONE.getCode(), order.getOutboundOrderCode());
				/**
				 * 更新发货单状态
				 */
				setOutboundOrderStatus(orderCode, order);
				/**
				 * 更新订单信息
				 */
				updateItemOrderSupplierOrderStatus(order.getOutboundOrderCode(), order.getWarehouseOrderCode());
				
				resultMap.put("flg", OrderCancelResultEnum.CANCEL_SUCC.code);
				
			} else {
	        	resultMap.put("flg", OrderCancelResultEnum.CANCEL_FAIL.code);
	        	resultMap.put("msg", response.getMessage());
	        	logger.error("通知自营仓库取消发货失败：{}", response.getMessage());
			}
			
		} else {
			logger.error("通知自营仓库取消发货错误：{}", appResult.getDatabuffer());
        	throw new OutboundOrderException("通知自营仓库取消发货错误,原因:" + appResult.getDatabuffer());
		}
		return resultMap;
	}

	/**
	 * @param order  待取消的发货单
	 * @param warehouseType 仓库类型 自营或者京东
	 * @param remark  取消的备注信息
	 * @return 取消结果
	 */
	public Map<String, String> deliveryOrderCancel (OutboundOrder order, WarehouseTypeEnum warehouseType, String skuCode, String remark) {
		
        //组装请求
        ScmOrderCancelRequest cancelReq = new ScmOrderCancelRequest();
        cancelReq.setOrderCode(order.getWmsOrderCode());
        cancelReq.setOrderType(CancelOrderType.DELIVERY.getCode());
        cancelReq.setWarehouseType(warehouseType.getCode());
        
        //调用仓库接口
        AppResult<ScmOrderCancelResponse> appResult = warehouseApiService.orderCancel(cancelReq);
        
        return resultProcess(appResult, order, skuCode, remark);
        
	}
	
	private Map<String, String> resultProcess (AppResult<ScmOrderCancelResponse> appResult, OutboundOrder order, String skuCode, String remark) {
		
        //返回结果map
        Map<String, String> resultMap = new HashMap<>();
        
        if (StringUtils.equals(appResult.getAppcode(), ResponseAck.SUCCESS_CODE)) { // 成功
        	
        	ScmOrderCancelResponse response = (ScmOrderCancelResponse) appResult.getResult();
            String flag = response.getFlag();
            
            String cancelResult = null;
            String detailCancelResult = null;
            if (StringUtils.equals(flag, OrderCancelResultEnum.CANCEL_SUCC.code)) { // 取消成功
            	
            	cancelResult = OutboundOrderStatusEnum.CANCELED.getCode();
            	detailCancelResult = OutboundDetailStatusEnum.CANCELED.getCode();
            	resultMap.put("flg", OrderCancelResultEnum.CANCEL_SUCC.code);
            	/**
            	 * 取消成功设置商品的cancelFlg为1
            	 */
            	updateDetail(skuCode, ZeroToNineEnum.ONE.getCode(), order.getOutboundOrderCode()); 
                
            } else if (StringUtils.equals(flag, OrderCancelResultEnum.CANCELLING.code)) {// 取消中
            	
            	cancelResult = OutboundOrderStatusEnum.ON_CANCELED.getCode();
            	detailCancelResult = OutboundDetailStatusEnum.ON_CANCELED.getCode();
            	resultMap.put("flg", OrderCancelResultEnum.CANCELLING.code);
            	/**
            	 * 取消中设置商品的cancelFlg为0，标识中间状态，如果在取消失败的时候需要重置 为null
            	 */
            	updateDetail(skuCode, ZeroToNineEnum.ZERO.getCode(), order.getOutboundOrderCode()); 

            } else {
            	
            	resultMap.put("flg", OrderCancelResultEnum.CANCEL_FAIL.code);
            	resultMap.put("msg", response.getMessage());
            	// 取消失败，直接返回，数据状态不用维护
            	return resultMap;
            }
            //更新发货单详情信息
            updateDetailStatus(detailCancelResult, order.getOutboundOrderCode());
            //更新发货单信息
            updateOutBoundOrder(order.getId(), cancelResult, remark);
            //更新订单信息
            updateItemOrderSupplierOrderStatus(order.getOutboundOrderCode(), order.getWarehouseOrderCode());
            
            return resultMap;
            
        } else {
        	throw new OutboundOrderException("发货单" + order.getOutboundOrderCode() + 
        			"取消异常，原因:" + appResult.getDatabuffer());
        }
	}
	
    /**
     * 修改发货单的商品详情状态
     * @param skuCode
     * @param cancelFlg 0：取消中  1：取消成功
     * @param outboundOrderCode
     */
    private void updateDetail(String skuCode, String cancelFlg, String outboundOrderCode){
        OutboundDetail outboundDetail = new OutboundDetail();
        outboundDetail.setCancelFlg(cancelFlg);// 用户取消
        outboundDetail.setUpdateTime(Calendar.getInstance().getTime());
        outboundDetail.setStatus(OutboundOrderStatusEnum.CANCELED.getCode());
        Example exampleOrder = new Example(OutboundDetail.class);
        Example.Criteria criteriaOrder = exampleOrder.createCriteria();
        criteriaOrder.andEqualTo("outboundOrderCode", outboundOrderCode);
        criteriaOrder.andEqualTo("skuCode", skuCode);
        outboundDetailService.updateByExampleSelective(outboundDetail, exampleOrder);
    }
	
    //修改取消发货单信息
    private void updateOutBoundOrder(Long orderId, String status, String remark){
    	OutboundOrder order = new OutboundOrder();
    	order.setId(orderId);
    	order.setStatus(status);
        if (OutboundOrderStatusEnum.CANCELED.getCode().equals(status)) { // 取消成功
        	order.setIsCancel(ZeroToNineEnum.ONE.getCode());
        }
        order.setUpdateTime(Calendar.getInstance().getTime());
        order.setRemark(remark); // 取消备注
        outBoundOrderService.updateByPrimaryKeySelective(order);
    }

}
