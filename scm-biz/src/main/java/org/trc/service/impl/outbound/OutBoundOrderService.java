package org.trc.service.impl.outbound;

import org.springframework.stereotype.Service;
import org.trc.domain.order.OutboundOrder;
import org.trc.service.impl.BaseService;
import org.trc.service.outbound.IOutBoundOrderService;

@Service("outBoundOrderService")
public class OutBoundOrderService extends BaseService<OutboundOrder, Long> implements IOutBoundOrderService {
}
