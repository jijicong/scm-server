package org.trc.domain.supplier;

import org.trc.domain.BaseDO;

import java.util.Date;

public class SupplierBrand extends BaseDO{
    private Long id;

    private Long supplierId;

    private String supplierCode;

    private Long brandId;

    private String brandCode;

    private Long categoryId;

    private String categoryCode;

    private String proxyAptitudeId;

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

    public Long getBrandId() {
        return brandId;
    }

    public void setBrandId(Long brandId) {
        this.brandId = brandId;
    }

    public String getBrandCode() {
        return brandCode;
    }

    public void setBrandCode(String brandCode) {
        this.brandCode = brandCode == null ? null : brandCode.trim();
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode == null ? null : categoryCode.trim();
    }

    public String getProxyAptitudeId() {
        return proxyAptitudeId;
    }

    public void setProxyAptitudeId(String proxyAptitudeId) {
        this.proxyAptitudeId = proxyAptitudeId == null ? null : proxyAptitudeId.trim();
    }
}