package org.trc.form.purchase;

import org.trc.util.QueryModel;

import javax.ws.rs.QueryParam;

/**
 * 采购入库通知单
 * Created by hzgjl on 2018/5/28.
 */
public class PurchaseInOrderForm extends QueryModel {
    /**
     * 入库通知单编号
     */
    @QueryParam("warehouseNoticeCode")
    private String warehouseNoticeCode;

    /**
     * 采购单编号
     */
    @QueryParam("purchaseOrderCode")
    private String purchaseOrderCode;

    /**
     *供应商名称
     */
    @QueryParam("supplierName")
    private String supplierName;

    /**
     * 采购类型
     */
    @QueryParam("purchaseType")
    private String purchaseType;

    /**
     *入库通知单状态
     */
    @QueryParam("status")
    private String status;

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

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getPurchaseType() {
        return purchaseType;
    }

    public void setPurchaseType(String purchaseType) {
        this.purchaseType = purchaseType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
