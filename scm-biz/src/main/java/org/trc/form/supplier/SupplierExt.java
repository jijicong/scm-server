package org.trc.form.supplier;

import org.trc.domain.supplier.*;

import java.util.List;

/**
 * Created by hzwdx on 2017/5/19.
 */
public class SupplierExt {

    /**
     * 供应商基本信息
     */
    private Supplier supplier;
    /**
     * 供应商证件信息
     */
    private Certificate certificate;
    /**
     * 供应商财务信息
     */
    private SupplierFinancialInfo supplierFinancialInfo;
    /**
     * 供应商售后信息
     */
    private SupplierAfterSaleInfo supplierAfterSaleInfo;
    /**
     *供应商渠道信息
     */
    private List<SupplierChannelRelation> supplierChannelRelations;

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public Certificate getCertificate() {
        return certificate;
    }

    public void setCertificate(Certificate certificate) {
        this.certificate = certificate;
    }

    public SupplierFinancialInfo getSupplierFinancialInfo() {
        return supplierFinancialInfo;
    }

    public void setSupplierFinancialInfo(SupplierFinancialInfo supplierFinancialInfo) {
        this.supplierFinancialInfo = supplierFinancialInfo;
    }

    public SupplierAfterSaleInfo getSupplierAfterSaleInfo() {
        return supplierAfterSaleInfo;
    }

    public void setSupplierAfterSaleInfo(SupplierAfterSaleInfo supplierAfterSaleInfo) {
        this.supplierAfterSaleInfo = supplierAfterSaleInfo;
    }

    public List<SupplierChannelRelation> getSupplierChannelRelations() {
        return supplierChannelRelations;
    }

    public void setSupplierChannelRelations(List<SupplierChannelRelation> supplierChannelRelations) {
        this.supplierChannelRelations = supplierChannelRelations;
    }
}
