package org.trc.form.order;

import java.util.List;

public class SupplierOrderCancelNotify {

    //订单类型：0-京东订单,1-粮油订单
    private String orderType;
    //供应商取消订单信息
    private List<SupplierOrderCancelInfo> order;

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public List<SupplierOrderCancelInfo> getOrder() {
        return order;
    }

    public void setOrder(List<SupplierOrderCancelInfo> order) {
        this.order = order;
    }
}
