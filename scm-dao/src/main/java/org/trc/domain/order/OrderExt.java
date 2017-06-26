package org.trc.domain.order;

import java.util.List;

/**
 * Created by hzwdx on 2017/6/26.
 */
public class OrderExt extends OrderBase{

    /**
     * 订单商品明细列表
     */
    private List<OrderItem> orderItemList;

    public List<OrderItem> getOrderItemList() {
        return orderItemList;
    }

    public void setOrderItemList(List<OrderItem> orderItemList) {
        this.orderItemList = orderItemList;
    }
}
