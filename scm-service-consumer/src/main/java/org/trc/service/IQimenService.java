package org.trc.service;

import com.qimen.api.request.*;
import com.qimen.api.response.*;

import org.trc.form.JDModel.ReturnTypeDO;
import org.trc.util.AppResult;

import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by hzcyn on 2017/11/22.
 */
public interface IQimenService {

    /**
     * 商品同步接口 (批量)
     * @return
     */
    ReturnTypeDO itemsSync(String warehouseCode, String ownerCode, List<ItemsSynchronizeRequest.Item> items);

	AppResult<EntryorderCreateResponse> entryOrderCreate(EntryorderCreateRequest req);

    AppResult<DeliveryorderCreateResponse> deliveryOrderCreate(DeliveryorderCreateRequest req);

    /**
     * 创建发货单(批量)
     * @param req
     * @return
     */
    AppResult<DeliveryorderBatchcreateResponse> deliveryorderBatchcreate(DeliveryorderBatchcreateRequest req);

    /**
     * 商品库存查询接口(批量)
     * @param inventoryQueryRequest
     * @return
     */
    AppResult<InventoryQueryResponse> inventoryQuery(InventoryQueryRequest inventoryQueryRequest);



    /**
     * 单据取消
     */
    AppResult<OrderCancelResponse> orderCancel(OrderCancelRequest req);
}
