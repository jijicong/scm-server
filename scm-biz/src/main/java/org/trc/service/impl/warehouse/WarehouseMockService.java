package org.trc.service.impl.warehouse;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.alibaba.dubbo.common.utils.StringUtils;
import com.jcraft.jsch.HASH;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.trc.constants.SupplyConstants;
import org.trc.domain.order.OutboundDetail;
import org.trc.domain.order.OutboundOrder;
import org.trc.domain.warehouseNotice.WarehouseNotice;
import org.trc.domain.warehouseNotice.WarehouseNoticeDetails;
import org.trc.form.warehouse.*;
import org.trc.service.outbound.IOutBoundOrderService;
import org.trc.service.outbound.IOutboundDetailService;
import org.trc.service.purchase.IWarehouseNoticeService;
import org.trc.service.warehouse.IWarehouseMockService;
import org.trc.service.warehouseNotice.IWarehouseNoticeDetailsService;
import org.trc.util.AppResult;
import org.trc.util.AssertUtil;
import org.trc.util.GuidUtil;
import org.trc.util.ResponseAck;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service("warehouseMockService")
public class WarehouseMockService implements IWarehouseMockService {

    @Autowired
    private IWarehouseNoticeService warehouseNoticeService;
    @Autowired
    private IWarehouseNoticeDetailsService warehouseNoticeDetailsService;
    @Autowired
    private IOutBoundOrderService outBoundOrderService;
    @Autowired
    private IOutboundDetailService outboundDetailService;

    @Override
    public AppResult<List<ScmEntryOrderDetailResponse>> entryOrderDetail(ScmEntryOrderDetailRequest entryOrderDetailRequest) {
        AppResult appResult = new AppResult(ResponseAck.SUCCESS_CODE, "查询入库单详情成功", "");
        ScmEntryOrderDetailResponse response = new ScmEntryOrderDetailResponse();
        response.setEntryOrderCode(entryOrderDetailRequest.getEntryOrderCode());
        response.setWarehouseCode("110008192");
        response.setOwnerCode("EBU4418046542712");
        response.setStatus("20");
        response.setCreateTime(new Date());
        response.setSupplierNo("EMS4418046548568");
        response.setCreateUser("测试");
        WarehouseNotice warehouseNotice = new WarehouseNotice();
        warehouseNotice.setWarehouseNoticeCode(entryOrderDetailRequest.getEntryOrderCode());
        warehouseNotice = warehouseNoticeService.selectOne(warehouseNotice);
        AssertUtil.notNull(warehouseNotice, String.format("根据入库单编码%s查询入库单信息为空", entryOrderDetailRequest.getEntryOrderCode()));
        response.setEntryOrderId(warehouseNotice.getEntryOrderId());
        //查询对应的入库通知商品明细
        WarehouseNoticeDetails details = new WarehouseNoticeDetails();
        details.setWarehouseNoticeCode(entryOrderDetailRequest.getEntryOrderCode());
        List<WarehouseNoticeDetails> detailsList = warehouseNoticeDetailsService.select(details);
        if(CollectionUtils.isNotEmpty(detailsList)){
            List<ScmEntryOrderDetailResponseItem> scmEntryOrderDetailResponseItemList = new ArrayList<>();
            for(WarehouseNoticeDetails detail: detailsList){
                ScmEntryOrderDetailResponseItem detailResponseItem = new ScmEntryOrderDetailResponseItem();
                detailResponseItem.setItemCode(detail.getSkuCode());
                detailResponseItem.setItemId(detail.getItemId());
                detailResponseItem.setPlanQty(detail.getPurchasingQuantity());
                detailResponseItem.setGoodsStatus("1");
                scmEntryOrderDetailResponseItemList.add(detailResponseItem);
            }
            response.setScmEntryOrderDetailResponseItemList(scmEntryOrderDetailResponseItemList);
        }
        appResult.setResult(response);
        return appResult;
    }

    @Override
    public AppResult<ScmDeliveryOrderDetailResponse> deliveryOrderDetail(ScmDeliveryOrderDetailRequest deliveryOrderDetailRequest) {
        AppResult appResult = new AppResult(ResponseAck.SUCCESS_CODE, "查询出库单详情成功", "");
        OutboundOrder outboundOrder = new OutboundOrder();
        outboundOrder.setOutboundOrderCode(deliveryOrderDetailRequest.getOrderCode());
        outboundOrder = outBoundOrderService.selectOne(outboundOrder);
        AssertUtil.notNull(outboundOrder, String.format("根据发货单编码%s查询发货单信息为空", deliveryOrderDetailRequest.getOrderCode()));
        OutboundDetail detail = new OutboundDetail();
        detail.setOutboundOrderCode(deliveryOrderDetailRequest.getOrderCode());
        List<OutboundDetail> outboundDetailList = outboundDetailService.select(detail);
        ScmDeliveryOrderDetailResponse response = new ScmDeliveryOrderDetailResponse();
        response.setWarehouseCode(deliveryOrderDetailRequest.getWarehouseCode());
        response.setOwnerCode(deliveryOrderDetailRequest.getOwnerCode());
        response.setDeliveryOrderCode(deliveryOrderDetailRequest.getOrderCode());
        if(!StringUtils.isBlank(deliveryOrderDetailRequest.getOrderId())){
            response.setDeliveryOrderId(deliveryOrderDetailRequest.getOrderId());
        }else{
            response.setDeliveryOrderId(outboundOrder.getWmsOrderCode());
        }
        response.setLogisticsCode("CYS0000010");
        response.setLogisticsName("京东配送");
        response.setExpressCode(GuidUtil.getNextUid("EX-"));
        response.setConsigneeName(outboundOrder.getReceiverName());
        response.setConsigneeMobile(outboundOrder.getReceiverPhone());
        response.setReciverName(outboundOrder.getReceiverName());
        response.setReciverMobile(outboundOrder.getReceiverPhone());
        response.setReciverProvince(outboundOrder.getReceiverProvince());
        response.setReciverCity(outboundOrder.getReceiverCity());
        response.setReciverCountry(outboundOrder.getReceiverDistrict());
        response.setReciverDetailAddress(outboundOrder.getReceiverAddress());
        response.setCurrentStatus("10019");
        response.setSplitFlag("0");
        if(CollectionUtils.isNotEmpty(outboundDetailList)){
            List<ScmDeliveryOrderDetailResponseItem> responseItemList = new ArrayList<>();
            for(OutboundDetail detail2: outboundDetailList){
                ScmDeliveryOrderDetailResponseItem item = new ScmDeliveryOrderDetailResponseItem();
                item.setOwnerCode(deliveryOrderDetailRequest.getOwnerCode());
                item.setItemCode(detail2.getSkuCode());
                item.setItemId(detail2.getWarehouseItemId());
                item.setActualQty(detail2.getShouldSentItemNum());
                responseItemList.add(item);
            }
            response.setScmDeliveryOrderDetailResponseItemList(responseItemList);
        }
        appResult.setResult(response);
        return appResult;
    }

    @Override
    public AppResult<ScmOrderPacksResponse> orderPack(ScmOrderPacksRequest orderPacksRequest) {
        AppResult appResult = new AppResult(ResponseAck.SUCCESS_CODE, "查询包裹信息成功", "");
        String[] orderIds = orderPacksRequest.getOrderIds().split(SupplyConstants.Symbol.COMMA);
        Example example = new Example(OutboundOrder.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("wmsOrderCode", Arrays.asList(orderIds));
        List<OutboundOrder> outboundOrderList = outBoundOrderService.selectByExample(example);
        List<String> outboundOrderCodes = new ArrayList<>();
        for(OutboundOrder outboundOrder: outboundOrderList){
            outboundOrderCodes.add(outboundOrder.getOutboundOrderCode());
        }
        Example example2 = new Example(OutboundDetail.class);
        Example.Criteria criteria2 = example2.createCriteria();
        criteria2.andIn("outboundOrderCode", outboundOrderCodes);
        List<OutboundDetail> detailList = outboundDetailService.selectByExample(example2);
        ScmOrderPacksResponse response  = new ScmOrderPacksResponse();
        for(OutboundOrder outboundOrder: outboundOrderList){
            ScmOrderDefaultResult defaultResult = new ScmOrderDefaultResult();
            defaultResult.setOrderCode(outboundOrder.getOutboundOrderCode());
            defaultResult.setOrderId(outboundOrder.getWmsOrderCode());
            defaultResult.setWayBill(GuidUtil.getNextUid("WAY-"));
            defaultResult.setLogisticsCode("CYS0000010");
            defaultResult.setLogisticsName("京东配送");
            List<ScmOrderPackage> scmOrderPackageList = new ArrayList<>();
            ScmOrderPackage scmOrderPackage = new ScmOrderPackage();
            String pakageNo = GuidUtil.getNextUid("PKG-");
            scmOrderPackage.setPackageNo(pakageNo);
            List<ScmDeliveryOrderDetailResponseItem> orderDetailResponseItems = new ArrayList<>();
            for(OutboundDetail detail: detailList){
                ScmDeliveryOrderDetailResponseItem responseItem = new ScmDeliveryOrderDetailResponseItem();
                responseItem.setItemCode(detail.getSkuCode());
                responseItem.setItemId(detail.getWarehouseItemId());
                responseItem.setActualQty(detail.getShouldSentItemNum());
                orderDetailResponseItems.add(responseItem);
            }
            scmOrderPackage.setScmDeliveryOrderDetailResponseItems(orderDetailResponseItems);
            scmOrderPackageList.add(scmOrderPackage);
            defaultResult.setScmOrderPackageList(scmOrderPackageList);
            List<ScmOrderDefaultResult> scmOrderDefaultResults = new ArrayList<>();
            scmOrderDefaultResults.add(defaultResult);
            response.setScmOrderDefaultResults(scmOrderDefaultResults);
        }
        appResult.setResult(response);
        return appResult;
    }
}
