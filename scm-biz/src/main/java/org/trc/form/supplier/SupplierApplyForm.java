package org.trc.form.supplier;

import org.hibernate.validator.constraints.Length;
import org.trc.util.QueryModel;

import javax.ws.rs.QueryParam;

/**
 * Created by hzqph on 2017/5/23.
 */
public class SupplierApplyForm extends QueryModel {

    @QueryParam("supplierName")
    @Length(max = 50, message = "属性名称不能超过50个字符")
    private String supplierName;//供应商名称
    @QueryParam("contact")
    @Length(max = 64, message = "属性名称不能超过64个字符")
    private String contact;//联系人
    @QueryParam("supplierCode")
    private String supplierCode;//供应商编号
    @QueryParam("applyStatus")
    private String status;//申请状态
    @QueryParam("supplierKindCode")
    private String supplierKindCode;//供应商

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

    public String getSupplierKindCode() {
        return supplierKindCode;
    }

    public void setSupplierKindCode(String supplierKindCode) {
        this.supplierKindCode = supplierKindCode;
    }
}
