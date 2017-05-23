package org.trc.form.supplier;

import org.trc.util.QueryModel;

import javax.ws.rs.QueryParam;

/**
 * Created by hzqph on 2017/5/12.
 */
public class SupplierApplyAuditForm extends QueryModel {
        @QueryParam("supplierName")
        private String supplierName;//供应商名称
        @QueryParam("contact")
        private String contact;//联系人
        @QueryParam("supplierCode")
        private String supplierCode;//供应商编号
        @QueryParam("status")
        private String status;//审核状态
        @QueryParam("applySquare")
        private String applySquare;//申请方

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getSupplierCode() {
        return supplierCode;
    }

    public void setSupplierCode(String supplierCode) {
        this.supplierCode = supplierCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getApplySquare() {
        return applySquare;
    }

    public void setApplySquare(String applySquare) {
        this.applySquare = applySquare;
    }
}
