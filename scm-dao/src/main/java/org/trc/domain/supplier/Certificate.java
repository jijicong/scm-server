package org.trc.domain.supplier;

import java.util.Date;

public class Certificate {
    private Long id;

    private Long supplierId;

    private String supplierCode;

    private String businessLicence;

    private String businessLicencePic;

    private String organRegistraCodeCertificate;

    private String organRegistraCodeCertificatePic;

    private String taxRegistrationCertificate;

    private String taxRegistrationCertificatePic;

    private String multiCertificateCombineNo;

    private String multiCertificateCombinePic;

    private String legalPersonIdCard;

    private String legalPersonIdCardPic1;

    private String legalPersonIdCardPic2;

    private String isDeleted;

    private Date createTime;

    private Date updateTime;

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

    public String getBusinessLicence() {
        return businessLicence;
    }

    public void setBusinessLicence(String businessLicence) {
        this.businessLicence = businessLicence == null ? null : businessLicence.trim();
    }

    public String getBusinessLicencePic() {
        return businessLicencePic;
    }

    public void setBusinessLicencePic(String businessLicencePic) {
        this.businessLicencePic = businessLicencePic == null ? null : businessLicencePic.trim();
    }

    public String getOrganRegistraCodeCertificate() {
        return organRegistraCodeCertificate;
    }

    public void setOrganRegistraCodeCertificate(String organRegistraCodeCertificate) {
        this.organRegistraCodeCertificate = organRegistraCodeCertificate == null ? null : organRegistraCodeCertificate.trim();
    }

    public String getOrganRegistraCodeCertificatePic() {
        return organRegistraCodeCertificatePic;
    }

    public void setOrganRegistraCodeCertificatePic(String organRegistraCodeCertificatePic) {
        this.organRegistraCodeCertificatePic = organRegistraCodeCertificatePic == null ? null : organRegistraCodeCertificatePic.trim();
    }

    public String getTaxRegistrationCertificate() {
        return taxRegistrationCertificate;
    }

    public void setTaxRegistrationCertificate(String taxRegistrationCertificate) {
        this.taxRegistrationCertificate = taxRegistrationCertificate == null ? null : taxRegistrationCertificate.trim();
    }

    public String getTaxRegistrationCertificatePic() {
        return taxRegistrationCertificatePic;
    }

    public void setTaxRegistrationCertificatePic(String taxRegistrationCertificatePic) {
        this.taxRegistrationCertificatePic = taxRegistrationCertificatePic == null ? null : taxRegistrationCertificatePic.trim();
    }

    public String getMultiCertificateCombineNo() {
        return multiCertificateCombineNo;
    }

    public void setMultiCertificateCombineNo(String multiCertificateCombineNo) {
        this.multiCertificateCombineNo = multiCertificateCombineNo == null ? null : multiCertificateCombineNo.trim();
    }

    public String getMultiCertificateCombinePic() {
        return multiCertificateCombinePic;
    }

    public void setMultiCertificateCombinePic(String multiCertificateCombinePic) {
        this.multiCertificateCombinePic = multiCertificateCombinePic == null ? null : multiCertificateCombinePic.trim();
    }

    public String getLegalPersonIdCard() {
        return legalPersonIdCard;
    }

    public void setLegalPersonIdCard(String legalPersonIdCard) {
        this.legalPersonIdCard = legalPersonIdCard == null ? null : legalPersonIdCard.trim();
    }

    public String getLegalPersonIdCardPic1() {
        return legalPersonIdCardPic1;
    }

    public void setLegalPersonIdCardPic1(String legalPersonIdCardPic1) {
        this.legalPersonIdCardPic1 = legalPersonIdCardPic1 == null ? null : legalPersonIdCardPic1.trim();
    }

    public String getLegalPersonIdCardPic2() {
        return legalPersonIdCardPic2;
    }

    public void setLegalPersonIdCardPic2(String legalPersonIdCardPic2) {
        this.legalPersonIdCardPic2 = legalPersonIdCardPic2 == null ? null : legalPersonIdCardPic2.trim();
    }

    public String getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(String isDeleted) {
        this.isDeleted = isDeleted == null ? null : isDeleted.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}