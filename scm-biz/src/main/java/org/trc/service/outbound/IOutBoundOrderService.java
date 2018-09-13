package org.trc.service.outbound;

import java.util.List;
import java.util.Map;

import org.trc.domain.order.OutboundDetail;
import org.trc.domain.order.OutboundOrder;
import org.trc.form.warehouse.ScmDeliveryOrderDetailResponse;
import org.trc.service.IBaseService;
import org.trc.util.AppResult;

public interface IOutBoundOrderService extends IBaseService<OutboundOrder,Long> {

    void updateDeliveryOrderDetail(AppResult<ScmDeliveryOrderDetailResponse> responseAppResult,
                                          String outboundOrderCode, String orderId) throws Exception;

	Map<String, String> deliveryCancel(OutboundOrder targetOrder, String skuCode);
	
	String getOutboundOrderStatusByDetail(List<OutboundDetail> outboundDetailList);
	
	void updateItemOrderSupplierOrderStatus(String outboundOrderCode, String warehouseOrderCode);


}
