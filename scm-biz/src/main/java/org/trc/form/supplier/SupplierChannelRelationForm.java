package org.trc.form.supplier;

import org.trc.util.QueryModel;

import javax.ws.rs.QueryParam;

/**
 * Created by hzwdx on 2017/5/22.
 */
public class SupplierChannelRelationForm extends QueryModel{

    @QueryParam("id")
    private Long id;
    @QueryParam("supplierId")
    private Long supplierId;
    @QueryParam("channelId")
    private Long channelId;
    @QueryParam("supplierCode")
    private String supplierCode;
    @QueryParam("channelCode")
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
        this.supplierCode = supplierCode;
    }

    public String getChannelCode() {
        return channelCode;
    }

    public void setChannelCode(String channelCode) {
        this.channelCode = channelCode;
    }
}
