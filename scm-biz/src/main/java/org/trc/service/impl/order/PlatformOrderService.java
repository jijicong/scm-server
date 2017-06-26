package org.trc.service.impl.order;

import org.springframework.stereotype.Service;
import org.trc.domain.order.PlatformOrder;
import org.trc.service.impl.BaseService;
import org.trc.service.order.IPlatformOrderService;

/**
 * Created by ding on 2017/6/22.
 */
@Service("platformOrderService")
public class PlatformOrderService extends BaseService<PlatformOrder,Long> implements IPlatformOrderService {
}
