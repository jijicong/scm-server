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
    AppResult<ItemsSynchronizeResponse> itemsSync(ItemsSynchronizeRequest req);

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
    
    /**
     * 退货入库单创建
     * @param req
     * @return
     */
    AppResult<ReturnorderCreateResponse> returnOrderCreate(ReturnorderCreateRequest req);
    
    /**
     * 出库单创建
     * @param req
     * @return
     */
    AppResult<StockoutCreateResponse> stockoutCreate(StockoutCreateRequest req);
    
    /**
     * 单据挂起（恢复）
     * 出库单创建
     * @param req
     * @return
     */
    AppResult<OrderPendingResponse> orderPending(OrderPendingRequest req);
}
