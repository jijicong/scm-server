package org.trc.domain.supplier;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.trc.domain.util.ScmDO;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.ws.rs.FormParam;

public class SupplierCategory extends ScmDO {
    @FormParam("id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @FormParam("supplierId")
    private Long supplierId;
    @FormParam("supplierCode")
    @NotEmpty
    @Length(max = 32, message = "供应链编号长度不能超过32个")
    private String supplierCode;
    @FormParam("categoryId")
    @NotEmpty
    private Long categoryId;
    @Transient
    @FormParam("supplierCetegory")
    @NotEmpty
    private String supplierCetegory;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    public String getSupplierCode() {
        return supplierCode;
    }

    public void setSupplierCode(String supplierCode) {
        this.supplierCode = supplierCode == null ? null : supplierCode.trim();
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getSupplierCetegory() {
        return supplierCetegory;
    }

    public void setSupplierCetegory(String supplierCetegory) {
        this.supplierCetegory = supplierCetegory;
    }
}