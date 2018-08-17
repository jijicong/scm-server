package org.trc.service.outbound;

import org.trc.domain.order.OutboundOrder;
import org.trc.form.warehouse.ScmDeliveryOrderDetailResponse;
import org.trc.service.IBaseService;
import org.trc.util.AppResult;

public interface IOutBoundOrderService extends IBaseService<OutboundOrder,Long> {

    void updateDeliveryOrderDetail(AppResult<ScmDeliveryOrderDetailResponse> responseAppResult,
                                          String outboundOrderCode, String orderId) throws Exception;


}
