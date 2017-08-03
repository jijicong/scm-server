package org.trc.domain.supplier;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.trc.domain.util.ScmDO;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.ws.rs.FormParam;

public class SupplierAfterSaleInfo extends ScmDO{
    @FormParam("id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @FormParam("supplierId")
    private Long supplierId;
    @FormParam("supplierCode")
    //@NotEmpty
    @Length(max = 32, message = "供应链编号长度不能超过32个")
    private String supplierCode;
    @FormParam("goodsReturnAddress")
    @NotEmpty
    @Length(max = 256, message = "退货地址长度不能超过256个")
    private String goodsReturnAddress;
    @FormParam("goodsReturnContactPerson")
    @NotEmpty
    @Length(max = 32, message = "退货联系人长度不能超过32个")
    private String goodsReturnContactPerson;
    @FormParam("goodsReturnPhone")
    @NotEmpty
    @Length(max = 32, message = "退货联系电话长度不能超过32个")
    private String goodsReturnPhone;
    @FormParam("goodsReturnStrategy")
    @Length(max = 3072, message = "退货策略长度不能超过3072个")
    private String goodsReturnStrategy;

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

    public String getGoodsReturnAddress() {
        return goodsReturnAddress;
    }

    public void setGoodsReturnAddress(String goodsReturnAddress) {
        this.goodsReturnAddress = goodsReturnAddress == null ? null : goodsReturnAddress.trim();
    }

    public String getGoodsReturnContactPerson() {
        return goodsReturnContactPerson;
    }

    public void setGoodsReturnContactPerson(String goodsReturnContactPerson) {
        this.goodsReturnContactPerson = goodsReturnContactPerson == null ? null : goodsReturnContactPerson.trim();
    }

    public String getGoodsReturnPhone() {
        return goodsReturnPhone;
    }

    public void setGoodsReturnPhone(String goodsReturnPhone) {
        this.goodsReturnPhone = goodsReturnPhone == null ? null : goodsReturnPhone.trim();
    }

    public String getGoodsReturnStrategy() {
        return goodsReturnStrategy;
    }

    public void setGoodsReturnStrategy(String goodsReturnStrategy) {
        this.goodsReturnStrategy = goodsReturnStrategy == null ? null : goodsReturnStrategy.trim();
    }
}