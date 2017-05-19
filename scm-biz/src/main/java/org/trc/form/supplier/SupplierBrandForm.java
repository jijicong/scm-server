package org.trc.form.supplier;

import org.hibernate.validator.constraints.Length;
import org.trc.util.QueryModel;

import javax.ws.rs.QueryParam;

/**
 * Created by hzwdx on 2017/5/10.
 */
public class SupplierBrandForm extends QueryModel {

    @QueryParam("supplierCode")
    @Length(max = 32, message = "供应商编码长度不能超过32个")
    private String supplierCode;
    @QueryParam("categoryId")
    private String categoryId;//多个分类ID用逗号分隔

    public String getSupplierCode() {
        return supplierCode;
    }

    public void setSupplierCode(String supplierCode) {
        this.supplierCode = supplierCode;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }
}
