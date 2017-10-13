package org.trc.form.order;

public class SupplierOrderCancelInfo {

    //仓库订单号
    private String warehouseOrderCode;
    //供应商父订单号,orderType=0时非空
    private String supplierParentOrderCode;
    //供应商订单号
    private String supplyOrderCode;
    //取消原因
    private String cancelReason;

    public String getWarehouseOrderCode() {
        return warehouseOrderCode;
    }

    public void setWarehouseOrderCode(String warehouseOrderCode) {
        this.warehouseOrderCode = warehouseOrderCode;
    }

    public String getSupplierParentOrderCode() {
        return supplierParentOrderCode;
    }

    public void setSupplierParentOrderCode(String supplierParentOrderCode) {
        this.supplierParentOrderCode = supplierParentOrderCode;
    }

    public String getSupplyOrderCode() {
        return supplyOrderCode;
    }

    public void setSupplyOrderCode(String supplyOrderCode) {
        this.supplyOrderCode = supplyOrderCode;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }
}
