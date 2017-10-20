package org.trc.dbUnit.order.form;

import java.util.List;

/**
 * Created by hzwdx on 2017/7/3.
 */
public class TrcShopOrderForm {

    private TrcShopOrder shopOrder;

    private List<TrcOrderItem> orderItems;

    public TrcShopOrder getShopOrder() {
        return shopOrder;
    }

    public void setShopOrder(TrcShopOrder shopOrder) {
        this.shopOrder = shopOrder;
    }

    public List<TrcOrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<TrcOrderItem> orderItems) {
        this.orderItems = orderItems;
    }
}
