package org.trc.form.purchase;

import org.hibernate.validator.constraints.Length;
import org.trc.util.QueryModel;

import javax.ws.rs.QueryParam;

/**
 * 采购单审核查询表单
 * Created by sone on 2017/6/20.
 */
public class PurchaseOrderAuditForm extends QueryModel{
    /**
     * 采购单编号
     */
    @QueryParam("purchaseOrderCode")
    @Length(max = 32)
    private String purchaseOrderCode;
    /**
     *供应商名称
     */
    @QueryParam("supplierName")
    @Length(max = 64)
    private String supplierName;
    /**
     *审核状态
     */
    @QueryParam("purchaseOrderAuditStatus")
    private String purchaseOrderAuditStatus;
    /**
     * 采购类型
     */
    @QueryParam("purchaseType")
    private String purchaseType;

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

    public String getPurchaseOrderAuditStatus() {
        return purchaseOrderAuditStatus;
    }

    public void setPurchaseOrderAuditStatus(String purchaseOrderAuditStatus) {
        this.purchaseOrderAuditStatus = purchaseOrderAuditStatus;
    }

    public String getPurchaseType() {
        return purchaseType;
    }

    public void setPurchaseType(String purchaseType) {
        this.purchaseType = purchaseType;
    }

}
