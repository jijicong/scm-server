package org.trc.domain.supplier;

import org.hibernate.validator.constraints.NotEmpty;
import org.trc.domain.BaseDO;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.ws.rs.FormParam;

/**
 * Created by hzqph on 2017/5/12.
 */
public class SupplierApply extends BaseDO {

    @FormParam("id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotEmpty
    @FormParam("applyCode")
    private String applyCode;//申请编号
    @NotEmpty
    @FormParam("supplierId")
    private Long supplierId;//供应商Id
    @NotEmpty
    @FormParam("channelId")
    private Long channelId;//渠道Id
    @NotEmpty
    @FormParam("supplierCode")
    private String supplierCode;//供应商编号
    @NotEmpty
    @FormParam("channelCode")
    private String channelCode;//渠道编号
    @NotEmpty
    @FormParam("description")
    private String description;//说明
    @NotEmpty
    @FormParam("status")
    private String status;//审核状态
    @FormParam("auditOpinion")
    private String auditOpinion;//审核意见
    @Transient
    private String contact;//联系人
    @Transient
    private String supplierName;//供应商名称
    @Transient
    private String supplierKindCode;//供应商性质编号
    private String brandNames;
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getApplyCode() {
        return applyCode;
    }

    public void setApplyCode(String applyCode) {
        this.applyCode = applyCode;
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public String getSupplierCode() {
        return supplierCode;
    }

    public void setSupplierCode(String supplierCode) {
        this.supplierCode = supplierCode;
    }

    public String getChannelCode() {
        return channelCode;
    }

    public void setChannelCode(String channelCode) {
        this.channelCode = channelCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAuditOpinion() {
        return auditOpinion;
    }

    public void setAuditOpinion(String auditOpinion) {
        this.auditOpinion = auditOpinion;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
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

    public String getBrandNames() {
        return brandNames;
    }

    public void setBrandNames(String brandNames) {
        this.brandNames = brandNames;
    }
}
