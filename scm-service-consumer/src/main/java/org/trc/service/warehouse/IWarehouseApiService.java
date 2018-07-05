package org.trc.service.warehouse;

import java.util.List;

import org.trc.form.warehouse.ScmDeliveryOrderCreateRequest;
import org.trc.form.warehouse.ScmDeliveryOrderCreateResponse;
import org.trc.form.warehouse.ScmDeliveryOrderDetailRequest;
import org.trc.form.warehouse.ScmDeliveryOrderDetailResponse;
import org.trc.form.warehouse.ScmEntryOrderCreateRequest;
import org.trc.form.warehouse.ScmEntryOrderDetailRequest;
import org.trc.form.warehouse.ScmEntryOrderDetailResponse;
import org.trc.form.warehouse.ScmInventoryQueryRequest;
import org.trc.form.warehouse.ScmInventoryQueryResponse;
import org.trc.form.warehouse.ScmItemSyncRequest;
import org.trc.form.warehouse.ScmItemSyncResponse;
import org.trc.form.warehouse.ScmOrderCancelRequest;
import org.trc.form.warehouse.ScmOrderCancelResponse;
import org.trc.form.warehouse.ScmOrderPacksRequest;
import org.trc.form.warehouse.ScmOrderPacksResponse;
import org.trc.form.warehouse.ScmReturnOrderCreateRequest;
import org.trc.form.warehouse.ScmReturnOrderCreateResponse;
import org.trc.form.warehouse.allocateOrder.ScmAllocateOrderInRequest;
import org.trc.form.warehouse.allocateOrder.ScmAllocateOrderInResponse;
import org.trc.form.warehouse.allocateOrder.ScmAllocateOrderOutRequest;
import org.trc.form.warehouse.allocateOrder.ScmAllocateOrderOutResponse;
import org.trc.form.warehouse.allocateOrder.ScmJosAllocateOrderRequest;
import org.trc.form.warehouse.allocateOrder.ScmJosAllocateOrderResponse;
import org.trc.util.AppResult;

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
}
