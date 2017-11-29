package org.trc.form.warehouse;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.validator.constraints.Length;
import org.trc.util.QueryModel;

import javax.ws.rs.QueryParam;

/**
 *
 * @author sone
 * @date 2017/5/2
 */
public class WarehouseNoticeForm extends QueryModel {
    /**
     * 入库通知单编码
     */
    @QueryParam("warehouseNoticeCode")
    @Length(max = 32)
    private String warehouseNoticeCode;

    @QueryParam("purchaseOrderCode")
    @Length(max = 32)
    private String purchaseOrderCode;

    @QueryParam("purchaseType")
    @Length(max = 32)
    private String purchaseType;

    @QueryParam("warehouseNoticeStatus")
    @Length(max = 2)
    private String warehouseNoticeStatus;

    /**
     * 入库通知单状态
     */
    @QueryParam("status")
    @Length(max = 2)
    private String status;

    @QueryParam("supplierName")
    @Length(max = 64)
    private String supplierName;

    public String getWarehouseNoticeCode() {
        return warehouseNoticeCode;
    }

    public void setWarehouseNoticeCode(String warehouseNoticeCode) {
        this.warehouseNoticeCode = warehouseNoticeCode;
    }

    public String getPurchaseOrderCode() {
        return purchaseOrderCode;
    }

    public void setPurchaseOrderCode(String purchaseOrderCode) {
        this.purchaseOrderCode = purchaseOrderCode;
    }

    public String getPurchaseType() {
        return purchaseType;
    }

    public void setPurchaseType(String purchaseType) {
        this.purchaseType = purchaseType;
    }

    public String getWarehouseNoticeStatus() {
        return warehouseNoticeStatus;
    }

    public void setWarehouseNoticeStatus(String warehouseNoticeStatus) {
        this.warehouseNoticeStatus = warehouseNoticeStatus;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
