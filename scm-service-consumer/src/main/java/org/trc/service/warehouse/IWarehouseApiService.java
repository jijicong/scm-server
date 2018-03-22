package org.trc.service.warehouse;

import org.trc.form.warehouse.*;
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
    AppResult<Object> itemSync(ScmItemSyncRequest scmItemSyncRequest);

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
    AppResult<String> orderCancel(ScmOrderCancelRequest orderCancelRequest);

    /**
     * 入库单详情查询
     * @param entryOrderDetailRequest
     * @return
     */
    AppResult<ScmEntryOrderDetailResponse> entryOrderDetail(ScmEntryOrderDetailRequest entryOrderDetailRequest);

    /**
     * 出库单详情查询
     * @param deliveryOrderDetailRequest
     * @return
     */
    AppResult<ScmDeliveryOrderDetailResponse> deliveryOrderDetail(ScmDeliveryOrderDetailRequest deliveryOrderDetailRequest);

}
