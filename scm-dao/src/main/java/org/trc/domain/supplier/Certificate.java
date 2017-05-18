package org.trc.domain.supplier;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.trc.domain.util.CommonDO;
import org.trc.domain.util.ScmDO;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.ws.rs.FormParam;
import java.util.Date;

public class Certificate extends ScmDO {
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
    @FormParam("businessLicence")
    @Length(max = 64, message = "营业执照长度不能超过64个")
    private String businessLicence;
    @FormParam("businessLicencePic")
    @Length(max = 256, message = "营业执照图片路径长度不能超过256个")
    private String businessLicencePic;
    @FormParam("organRegistraCodeCertificate")
    @Length(max = 64, message = "组织机构代码证长度不能超过64个")
    private String organRegistraCodeCertificate;
    @FormParam("organRegistraCodeCertificatePic")
    @Length(max = 256, message = "组织机构代码证图片路径长度不能超过256个")
    private String organRegistraCodeCertificatePic;
    @FormParam("taxRegistrationCertificate")
    @Length(max = 64, message = "税务登记证长度不能超过64个")
    private String taxRegistrationCertificate;
    @FormParam("taxRegistrationCertificatePic")
    @Length(max = 256, message = "税务登记证图片路径长度不能超过256个")
    private String taxRegistrationCertificatePic;
    @FormParam("multiCertificateCombineNo")
    @Length(max = 64, message = "证合一证号长度不能超过64个")
    private String multiCertificateCombineNo;
    @FormParam("multiCertificateCombinePic")
    @Length(max = 256, message = "证合一证图片路径长度不能超过256个")
    private String multiCertificateCombinePic;
    @FormParam("legalPersonIdCard")
    @NotEmpty
    @Length(max = 32, message = "法人身份证长度不能超过32个")
    private String legalPersonIdCard;
    @FormParam("legalPersonIdCardPic1")
    @NotEmpty
    @Length(max = 256, message = "法人身份证正面图片路径长度不能超过256个")
    private String legalPersonIdCardPic1;
    @FormParam("legalPersonIdCardPic2")
    @NotEmpty
    @Length(max = 256, message = "法人身份证背面图片路径长度不能超过256个")
    private String legalPersonIdCardPic2;
    @FormParam("businessLicenceStartDate")
    @Length(max = 20, message = "营业执照有效期开始日期长度不能超过20个")
    private String businessLicenceStartDate;
    @FormParam("businessLicenceEndDate")
    @Length(max = 20, message = "营业执照有效期截止日期长度不能超过20个")
    private String businessLicenceEndDate;
    @FormParam("organRegistraStartDate")
    @Length(max = 20, message = "组织结构代码证有效期开始日期长度不能超过20个")
    private String organRegistraStartDate;
    @FormParam("organRegistraEndDate")
    @Length(max = 20, message = "组织结构代码证有效期截止日期长度不能超过20个")
    private String organRegistraEndDate;
    @FormParam("taxRegistrationStartDate")
    @Length(max = 20, message = "税务登记证有效期开始日期长度不能超过20个")
    private String taxRegistrationStartDate;
    @FormParam("taxRegistrationEndDate")
    @Length(max = 20, message = "税务登记证有效期截止日期长度不能超过20个")
    private String taxRegistrationEndDate;
    @FormParam("multiCertificateStartDate")
    @Length(max = 20, message = "多证合一有效期开始日期长度不能超过20个")
    private String multiCertificateStartDate;
    @FormParam("multiCertificateEndDate")
    @Length(max = 20, message = "多证合一有效期截止日期长度不能超过20个")
    private String multiCertificateEndDate;
    @FormParam("idCardStartDate")
    @Length(max = 20, message = "法人身份证有效期开始日期长度不能超过20个")
    private String idCardStartDate;
    @FormParam("idCardEndDate")
    @Length(max = 20, message = "法人身份证有效期截止日期长度不能超过20个")
    private String idCardEndDate;

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

    public String getBusinessLicenceStartDate() {
        return businessLicenceStartDate;
    }

    public void setBusinessLicenceStartDate(String businessLicenceStartDate) {
        this.businessLicenceStartDate = businessLicenceStartDate;
    }

    public String getBusinessLicenceEndDate() {
        return businessLicenceEndDate;
    }

    public void setBusinessLicenceEndDate(String businessLicenceEndDate) {
        this.businessLicenceEndDate = businessLicenceEndDate;
    }

    public String getOrganRegistraStartDate() {
        return organRegistraStartDate;
    }

    public void setOrganRegistraStartDate(String organRegistraStartDate) {
        this.organRegistraStartDate = organRegistraStartDate;
    }

    public String getOrganRegistraEndDate() {
        return organRegistraEndDate;
    }

    public void setOrganRegistraEndDate(String organRegistraEndDate) {
        this.organRegistraEndDate = organRegistraEndDate;
    }

    public String getTaxRegistrationStartDate() {
        return taxRegistrationStartDate;
    }

    public void setTaxRegistrationStartDate(String taxRegistrationStartDate) {
        this.taxRegistrationStartDate = taxRegistrationStartDate;
    }

    public String getTaxRegistrationEndDate() {
        return taxRegistrationEndDate;
    }

    public void setTaxRegistrationEndDate(String taxRegistrationEndDate) {
        this.taxRegistrationEndDate = taxRegistrationEndDate;
    }

    public String getMultiCertificateStartDate() {
        return multiCertificateStartDate;
    }

    public void setMultiCertificateStartDate(String multiCertificateStartDate) {
        this.multiCertificateStartDate = multiCertificateStartDate;
    }

    public String getMultiCertificateEndDate() {
        return multiCertificateEndDate;
    }

    public void setMultiCertificateEndDate(String multiCertificateEndDate) {
        this.multiCertificateEndDate = multiCertificateEndDate;
    }

    public String getIdCardStartDate() {
        return idCardStartDate;
    }

    public void setIdCardStartDate(String idCardStartDate) {
        this.idCardStartDate = idCardStartDate;
    }

    public String getIdCardEndDate() {
        return idCardEndDate;
    }

    public void setIdCardEndDate(String idCardEndDate) {
        this.idCardEndDate = idCardEndDate;
    }
}