package org.trc.form.order;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import javax.ws.rs.FormParam;

public class SupplierOrderCancelForm {

    @FormParam("warehouseOrderCode")
    @NotEmpty
    @Length(max = 32, message = "仓库订单编码长度不能超过32个")
    private String warehouseOrderCode;
    //是否取消：0-否,1-是
    @FormParam("isCancel")
    @NotEmpty
    @Length(max = 2, message = "是否取消长度不能超过2个")
    private String isCancel;
    //取消原因
    @FormParam("cancelReason")
    @Length(max = 256, message = "取消原因长度不能超过256个")
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
