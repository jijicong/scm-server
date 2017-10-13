package org.trc.form.order;

public class SupplierOrderCancelForm {

    // 仓库订单编码
    private String warehouseOrderCode;
    //是否取消：0-否,1-是
    private String isCancel;
    //取消原因
    private String cancelReason;

    public String getWarehouseOrderCode() {
        return warehouseOrderCode;
    }

    public void setWarehouseOrderCode(String warehouseOrderCode) {
        this.warehouseOrderCode = warehouseOrderCode;
    }

    public String getIsCancel() {
        return isCancel;
    }

    public void setIsCancel(String isCancel) {
        this.isCancel = isCancel;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }
}
