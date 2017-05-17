package org.trc.form.supplier;

import org.hibernate.validator.constraints.Length;
import org.trc.util.QueryModel;

import javax.ws.rs.QueryParam;

/**
 * Created by hzwdx on 2017/5/10.
 */
public class SupplierCategoryForm extends QueryModel{

    @QueryParam("categoryId")
    private Long categoryId;
    @QueryParam("supplierCode")
    @Length(max = 32, message = "供应商编码长度不能超过32个")
    private String supplierCode;

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getSupplierCode() {
        return supplierCode;
    }

    public void setSupplierCode(String supplierCode) {
        this.supplierCode = supplierCode;
    }
}
