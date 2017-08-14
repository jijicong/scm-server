package org.trc.biz.outbuond;

import org.trc.domain.order.OutboundOrder;
import org.trc.form.outbound.OutBoundOrderForm;
import org.trc.util.Pagenation;

public interface IOutBoundOrderBiz {
    Pagenation<OutboundOrder> outboundOrderPage(OutBoundOrderForm queryModel, Pagenation<OutboundOrder> page) throws Exception;
}
