package org.trc.domain.supplier;

import org.trc.domain.BaseDO;

import java.util.Date;

public class SupplierAfterSaleInfo extends BaseDO{
    private Long id;

    private String supplierId;

    private String supplierCode;

    private String goodsReturnAddress;

    private String goodsReturnContactPerson;

    private String goodsReturnPhone;

    private String goodsReturnStrategy;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId == null ? null : supplierId.trim();
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