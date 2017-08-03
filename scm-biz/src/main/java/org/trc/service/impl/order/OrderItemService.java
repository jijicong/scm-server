package org.trc.service.impl.order;

import org.springframework.stereotype.Service;
import org.trc.domain.order.OrderItem;
import org.trc.service.impl.BaseService;
import org.trc.service.order.IOrderItemService;

/**
 * Created by ding on 2017/6/22.
 */
@Service("orderItemService")
public class OrderItemService extends BaseService<OrderItem,Long> implements IOrderItemService {
}
