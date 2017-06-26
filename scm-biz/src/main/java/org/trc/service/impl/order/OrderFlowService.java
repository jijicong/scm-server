package org.trc.service.impl.order;

import org.springframework.stereotype.Service;
import org.trc.domain.order.OrderFlow;
import org.trc.service.impl.BaseService;
import org.trc.service.order.IOrderFlowService;

/**
 * Created by ding on 2017/6/22.
 */
@Service("orderFlowService")
public class OrderFlowService extends BaseService<OrderFlow,String> implements IOrderFlowService {
}
