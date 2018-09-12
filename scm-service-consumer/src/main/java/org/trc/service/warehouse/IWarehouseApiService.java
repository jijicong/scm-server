package org.trc.service.warehouse;

import org.trc.form.warehouse.*;
import org.trc.form.warehouse.ScmOrderCancelResponse;
import org.trc.form.warehouse.allocateOrder.*;
import org.trc.form.warehouse.entryReturnOrder.*;
import org.trc.util.AppResult;

import java.util.List;

/**
 * 外部仓库接口
 */
public interface IWarehouseApiService {

    /**
     * 商品信息同步
     * @param scmItemSyncRequest
     * @return 新增时data里面值是仓库商品编号,修改时里面是修改的结果true/false
     */
    AppResult<List<ScmItemSyncResponse>> itemSync(ScmItemSyncRequest scmItemSyncRequest);

    /**
     * 商品库存查询
     * @param inventoryQueryRequest
     * @return
     */
    AppResult<List<ScmInventoryQueryResponse>> inventoryQuery(ScmInventoryQueryRequest inventoryQueryRequest);

    /**
     * 入库单创建
     * @param entryOrderCreateRequest
     * @return
     */
    AppResult<String> entryOrderCreate(ScmEntryOrderCreateRequest entryOrderCreateRequest);

    /**
     * 发货单创建
     * @param deliveryOrderCreateRequest
     * @return
     */
    AppResult<List<ScmDeliveryOrderCreateResponse>> deliveryOrderCreate(ScmDeliveryOrderCreateRequest deliveryOrderCreateRequest);

    /**
     * 退货入库单创建
     * @param returnOrderCreateRequest
     * @return
     */
    AppResult<ScmReturnOrderCreateResponse> returnOrderCreate(ScmReturnOrderCreateRequest returnOrderCreateRequest);

    /**
     * 发货单取消
     * @param orderCancelRequest
     * @return
     */
    AppResult<ScmOrderCancelResponse> orderCancel(ScmOrderCancelRequest orderCancelRequest);

    /**
     * 入库单详情查询
     * @param entryOrderDetailRequest
     * @return
     */
    AppResult<List<ScmEntryOrderDetailResponse>> entryOrderDetail(ScmEntryOrderDetailRequest entryOrderDetailRequest);

    /**
     * 出库单详情查询
     * @param deliveryOrderDetailRequest
     * @return
     */
    AppResult<ScmDeliveryOrderDetailResponse> deliveryOrderDetail(ScmDeliveryOrderDetailRequest deliveryOrderDetailRequest);


    /**
     * 物流包裹详情查询
     * @param orderPacksRequest
     * @return
     */
    AppResult<ScmOrderPacksResponse> orderPack(ScmOrderPacksRequest orderPacksRequest);
    
    /**
     * 调拨出库通知
     * @param 
     * @return
     */
    AppResult<ScmAllocateOrderOutResponse> allocateOrderOutNotice (ScmAllocateOrderOutRequest allocateOrderOutRequest);

    /**
     * 调拨入库通知
     * @param 
     * @return
     */
	AppResult<ScmAllocateOrderInResponse> allocateOrderInNotice(ScmAllocateOrderInRequest request);

    /**
     * 创建京东仓间调拨单
     * @param 
     * @return
     */
	AppResult<ScmJosAllocateOrderResponse> josAllocateOrderCreate(ScmJosAllocateOrderRequest req);

    /**
     * 创建退货出库通知单
     * @param 
     * @return
     */
	AppResult<ScmEntryReturnOrderCreateResponse> entryReturnOrderCreate(ScmEntryReturnOrderCreateRequest request);
	
    /**
     * 退货出库单详情
     * @param 
     * @return
     */
	AppResult<List<ScmEntryReturnDetailResponse>> entryReturnDetail(ScmEntryReturnDetailRequest request);

    /**
     * 出库单(sku级)取消
     * @param 
     * @return
     */
	AppResult<ScmAfterSaleOrderCancelResponse> afterSaleCancel(ScmAfterSaleOrderCancelRequest req);

    /**
     * 取消退货入库单
     * @param req
     * @return
     */
    AppResult<ScmCancelAfterSaleOrderResponse> returnInOrderCancel(ScmCancelAfterSaleOrderRequest req);


    /**
     * 提交售后单物流信息
     * @param req
     * @return
     */
    AppResult<ScmSubmitAfterSaleOrderLogisticsResponse> submitAfterSaleLogistics(ScmSubmitAfterSaleOrderLogisticsRequest req);

}
