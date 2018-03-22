package org.trc.service.impl.warehouse;

import org.springframework.stereotype.Service;
import org.trc.form.warehouse.*;
import org.trc.service.warehouse.IWarehouseApiService;
import org.trc.util.AppResult;

import java.util.List;

@Service("warehouseApiService")
public class WarehouseApiServiceImpl implements IWarehouseApiService {
    @Override
    public AppResult<Object> itemSync(ScmItemSyncRequest scmItemSyncRequest) {
        return null;
    }

    @Override
    public AppResult<List<ScmInventoryQueryResponse>> inventoryQuery(ScmInventoryQueryRequest inventoryQueryRequest) {
        return null;
    }

    @Override
    public AppResult<String> entryOrderCreate(ScmEntryOrderCreateRequest entryOrderCreateRequest) {
        return null;
    }

    @Override
    public AppResult<List<ScmDeliveryOrderCreateResponse>> deliveryOrderCreate(ScmDeliveryOrderCreateRequest deliveryOrderCreateRequest) {
        return null;
    }

    @Override
    public AppResult<ScmReturnOrderCreateResponse> returnOrderCreate(ScmReturnOrderCreateRequest returnOrderCreateRequest) {
        return null;
    }

    @Override
    public AppResult<String> orderCancel(ScmOrderCancelRequest orderCancelRequest) {
        return null;
    }

    @Override
    public AppResult<ScmEntryOrderDetailResponse> entryOrderDetail(ScmEntryOrderDetailRequest entryOrderDetailRequest) {
        return null;
    }

    @Override
    public AppResult<ScmDeliveryOrderDetailResponse> deliveryOrderDetail(ScmDeliveryOrderDetailRequest deliveryOrderDetailRequest) {
        return null;
    }
}
