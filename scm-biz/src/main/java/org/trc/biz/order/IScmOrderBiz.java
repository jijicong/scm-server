package org.trc.biz.order;

import org.trc.domain.order.ShopOrder;
import org.trc.form.order.ShopOrderForm;
import org.trc.util.Pagenation;

import java.util.List;

/**
 * Created by hzwdx on 2017/6/26.
 */
public interface IScmOrderBiz {

    /**
     * 订单分页查询
     * @param form
     * @return
     * @throws Exception
     */
    Pagenation<ShopOrder> shopOrderPage(ShopOrderForm form, Pagenation<ShopOrder> page);

    /**
     *查询商铺订单列表
     * @param form
     * @return
     */
    List<ShopOrder> queryShopOrders(ShopOrderForm form);

}
