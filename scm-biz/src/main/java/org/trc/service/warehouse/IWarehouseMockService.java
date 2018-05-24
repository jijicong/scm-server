package org.trc.service.warehouse;

import org.trc.form.warehouse.*;
import org.trc.util.AppResult;

import java.util.List;

/**
 * 仓库mock服务
 */
public interface IWarehouseMockService {

    /**
     *入库单明细
     * @param entryOrderDetailRequest
     * @return
     */
    AppResult<List<ScmEntryOrderDetailResponse>> entryOrderDetail(ScmEntryOrderDetailRequest entryOrderDetailRequest);

    /**
     * 出库单明细
     * @param deliveryOrderDetailRequest
     * @return
     */
    AppResult<ScmDeliveryOrderDetailResponse> deliveryOrderDetail(ScmDeliveryOrderDetailRequest deliveryOrderDetailRequest);

    /**
     * 查询发货单包裹信息
     * @param orderPacksRequest
     * @return
     */
    AppResult<ScmOrderPacksResponse> orderPack(ScmOrderPacksRequest orderPacksRequest);

}
