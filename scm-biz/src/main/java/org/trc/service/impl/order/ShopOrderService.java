package org.trc.service.impl.order;

import org.springframework.stereotype.Service;
import org.trc.domain.order.ShopOrder;
import org.trc.service.impl.BaseService;
import org.trc.service.order.IShopOrderService;

/**
 * Created by ding on 2017/6/22.
 */
@Service("shopOrderService")
public class ShopOrderService extends BaseService<ShopOrder,Long> implements IShopOrderService {
}
