package org.trc.form.order;

/**
 * 京东订单拆单通知参数
 */
public class JdOrderSplitParam {
    //仓库订单号
    private String warehouseOrderCode;
    //京东订单号
    private String jdOrderCode;

    public String getWarehouseOrderCode() {
        return warehouseOrderCode;
    }

    public void setWarehouseOrderCode(String warehouseOrderCode) {
        this.warehouseOrderCode = warehouseOrderCode;
    }

    public String getJdOrderCode() {
        return jdOrderCode;
    }

    public void setJdOrderCode(String jdOrderCode) {
        this.jdOrderCode = jdOrderCode;
    }
}
