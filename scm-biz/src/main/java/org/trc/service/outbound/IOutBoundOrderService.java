package org.trc.service.outbound;

import org.trc.domain.order.OutboundOrder;
import org.trc.form.warehouse.ScmDeliveryOrderDetailResponse;
import org.trc.service.IBaseService;
import org.trc.util.AppResult;

import java.util.Map;

public interface IOutBoundOrderService extends IBaseService<OutboundOrder,Long> {

    void updateDeliveryOrderDetail(AppResult<ScmDeliveryOrderDetailResponse> responseAppResult,
                                          String outboundOrderCode, String orderId) throws Exception;

	Map<String, String> deliveryCancel(OutboundOrder targetOrder, String skuCode);


}
