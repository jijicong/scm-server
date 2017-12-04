package org.trc.service;

import com.qimen.api.request.DeliveryorderCreateRequest;
import com.qimen.api.request.EntryorderCreateRequest;
import com.qimen.api.request.ItemsSynchronizeRequest;
import com.qimen.api.request.OrderCancelRequest;
import com.qimen.api.response.DeliveryorderCreateResponse;
import com.qimen.api.response.EntryorderCreateResponse;

import com.qimen.api.response.InventoryQueryResponse;
import com.qimen.api.response.OrderCancelResponse;
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
