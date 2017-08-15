package org.trc.form;

import java.util.List;

/**
 * 供应商订单下单返回结果
 * Created by hzwdx on 2017/8/15.
 */
public class OrderSubmitResult {

    /**
     * 仓库订单编码
     */
    private String warehouseOrderCode;

    /**
     * 订单类型:0-京东,1-粮油
     */
    private String orderType;

    /**
     * 订单信息
     */
    private List<SupplierOrderReturn> order;

    public String getWarehouseOrderCode() {
        return warehouseOrderCode;
    }

    public void setWarehouseOrderCode(String warehouseOrderCode) {
        this.warehouseOrderCode = warehouseOrderCode;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public List<SupplierOrderReturn> getOrder() {
        return order;
    }

    public void setOrder(List<SupplierOrderReturn> order) {
        this.order = order;
    }
}
