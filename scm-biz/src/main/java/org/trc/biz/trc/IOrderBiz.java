package org.trc.biz.trc;

import org.trc.domain.order.OrderItem;
import org.trc.domain.order.PlatformOrder;
import org.trc.domain.order.ShopOrder;

import java.util.List;

/**
 * Created by ding on 2017/6/23.
 */
public interface IOrderBiz {

    void splitOrder(List<OrderItem> orderItems, List<ShopOrder> shopOrders, PlatformOrder platformOrder) throws Exception;
}
