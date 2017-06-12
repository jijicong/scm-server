package org.trc.domain.supplier;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.trc.domain.BaseDO;
import org.trc.domain.util.ScmDO;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.ws.rs.FormParam;
import java.util.Date;

public class SupplierBrand extends ScmDO {

    @FormParam("id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @FormParam("supplierId")
    private Long supplierId;
    @FormParam("supplierCode")
    @Length(max = 32, message = "供应商编号长度不能超过32个")
    private String supplierCode;
    @FormParam("brandId")
    //@NotEmpty
    private Long brandId;
    @FormParam("brandCode")
    //@NotEmpty
    @Length(max = 32, message = "品牌编号长度不能超过32个")
    private String brandCode;
    @FormParam("categoryId")
    //@NotEmpty
    private Long categoryId;
    @FormParam("categoryCode")
    //@NotEmpty
    //@Length(max = 32, message = "分类ID编号长度不能超过32个")
    private String categoryCode;
    @FormParam("proxyAptitudeId")
    //@NotEmpty
    @Length(max = 32, message = "代理资质编号度不能超过32个")
    private String proxyAptitudeId;
    @FormParam("proxyAptitudeStartDate")
    //@NotEmpty
    @Length(max = 32, message = "资质有效期开始日期长度不能超过32个")
    private String proxyAptitudeStartDate;
    @FormParam("proxyAptitudeEndDate")
    //@NotEmpty
    @Length(max = 32, message = "资质有效期截止日期长度不能超过32个")
    private String proxyAptitudeEndDate;
    @FormParam("aptitudePic")
    //@NotEmpty
    @Length(max = 256, message = "资质证明图片路径长度不能超过256个")
    private String aptitudePic;
    @Transient
    private String brandName;
    @Transient
    @FormParam("supplierBrand")
    private String supplierBrand;
    @FormParam("isValid")
    @Length(max = 2, message = "是否有编码字母和数字不能超过2个")
    private String isValid; //是否有效:0-否,1-是

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

    public String getProxyAptitudeStartDate() {
        return proxyAptitudeStartDate;
    }

    public void setProxyAptitudeStartDate(String proxyAptitudeStartDate) {
        this.proxyAptitudeStartDate = proxyAptitudeStartDate;
    }

    public String getProxyAptitudeEndDate() {
        return proxyAptitudeEndDate;
    }

    public void setProxyAptitudeEndDate(String proxyAptitudeEndDate) {
        this.proxyAptitudeEndDate = proxyAptitudeEndDate;
    }

    public String getAptitudePic() {
        return aptitudePic;
    }

    public void setAptitudePic(String aptitudePic) {
        this.aptitudePic = aptitudePic;
    }

    public String getSupplierBrand() {
        return supplierBrand;
    }

    public void setSupplierBrand(String supplierBrand) {
        this.supplierBrand = supplierBrand;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getIsValid() {
        return isValid;
    }

    public void setIsValid(String isValid) {
        this.isValid = isValid;
    }
}