package org.trc.domain.supplier;

import org.hibernate.validator.constraints.Length;
import org.trc.domain.util.ScmDO;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.ws.rs.FormParam;

public class SupplierChannelRelation extends ScmDO{

    @FormParam("id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @FormParam("supplierId")
    private Long supplierId;
    @FormParam("channelId")
    private Long channelId;
    @FormParam("supplierCode")
    @Length(max = 32, message = "供应商编号长度不能超过32个")
    private String supplierCode;
    @FormParam("channelCode")
    @Length(max = 32, message = "渠道编号长度不能超过32个")
    private String channelCode;

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
        this.supplierCode = supplierCode == null ? null : supplierCode.trim();
    }

    public String getChannelCode() {
        return channelCode;
    }

    public void setChannelCode(String channelCode) {
        this.channelCode = channelCode == null ? null : channelCode.trim();
    }
}