package org.trc.service.impl.order;

import org.springframework.stereotype.Service;
import org.trc.domain.order.OrderIdempotent;
import org.trc.service.impl.BaseService;
import org.trc.service.order.IOrderIdempotentService;

@Service("orderIdempotentService")
public class OrderIdempotentService  extends BaseService<OrderIdempotent,Long> implements IOrderIdempotentService {
}
