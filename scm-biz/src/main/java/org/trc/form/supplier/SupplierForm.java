package org.trc.form.supplier;

import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hibernate.validator.constraints.Length;
import org.trc.util.QueryModel;

import javax.ws.rs.QueryParam;

/**
 * Created by hzwdx on 2017/5/5.
 */
public class SupplierForm extends QueryModel{

    @ApiParam(value = "供应商编码")
    @QueryParam("supplierCode")
    @Length(max = 32, message = "供应商编码长度不能超过32个")
    private String supplierCode;
    @ApiParam(value = "供应商名称")
    @QueryParam("supplierName")
    @Length(max = 50, message = "供应商名称长度不能超过50个")
    private String supplierName;
    @ApiParam(value = "供应商性质编码")
    @QueryParam("supplierKindCode")
    @Length(max = 32, message = "供应商性质编码长度不能超过32个")
    private String supplierKindCode;
    @ApiParam(value = "联系人")
    @QueryParam("contact")
    @Length(max = 64, message = "联系人长度不能超过64个")
    private String contact;
    @ApiParam(value = "审核状态")
    @QueryParam("status")
    @Length(max = 2, message = "审核状态长度不能超过2个")
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSupplierCode() {
        return supplierCode;
    }

    public void setSupplierCode(String supplierCode) {
        this.supplierCode = supplierCode;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getSupplierKindCode() {
        return supplierKindCode;
    }

    public void setSupplierKindCode(String supplierKindCode) {
        this.supplierKindCode = supplierKindCode;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    @Override
    public String toString(){
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}
