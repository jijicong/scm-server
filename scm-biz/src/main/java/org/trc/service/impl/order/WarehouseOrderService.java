package org.trc.service.impl.order;

import org.springframework.stereotype.Service;
import org.trc.domain.order.WarehouseOrder;
import org.trc.service.impl.BaseService;
import org.trc.service.order.IWarehouseOrderService;

/**
 * Created by ding on 2017/6/22.
 */
@Service("warehouseOrderService")
public class WarehouseOrderService extends BaseService<WarehouseOrder,Long> implements IWarehouseOrderService {
}
