package org.trc.form.purchase;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.validator.constraints.Length;
import org.trc.util.QueryModel;

import javax.ws.rs.QueryParam;

/**
 * Created by sone on 2017/6/2.
 */
public class PurchaseOrderForm extends QueryModel{
    /**
     * 采购单编号
     */
    @QueryParam("purchaseOrderCode")
    @Length(max = 32)
    private String purchaseOrderCode;
    /**
     *供应商名称
     */
    @QueryParam("supplierName")
    @Length(max = 64)
    private String supplierName;
    /**
     * 采购人姓名
     */
    @QueryParam("purchaseName")
    @Length(max = 32)
    private String purchaseName;
    /**
     *采购单状态
     */
    @QueryParam("purchaseStatus")
    private String purchaseStatus;
    /**
     * 采购类型
     */
    @QueryParam("purchaseType")
    private String purchaseType;

    /**
     * 采购组
     */
    @QueryParam("purchaseGroup")
    private String  purchaseGroup;

    /**
     * v2.5 入库状态:0-等待入库,1-全部入库,2-部分入库,3-入库异常,其他情况为null
     */
    @QueryParam("warehouseNoticeStatus")
    private String warehouseNoticeStatus;

    public String getWarehouseNoticeStatus() {
        return warehouseNoticeStatus;
    }

    public void setWarehouseNoticeStatus(String warehouseNoticeStatus) {
        this.warehouseNoticeStatus = warehouseNoticeStatus;
    }

    public String getPurchaseOrderCode() {
        return purchaseOrderCode;
    }

    public void setPurchaseOrderCode(String purchaseOrderCode) {
        this.purchaseOrderCode = purchaseOrderCode;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getPurchaseName() {
        return purchaseName;
    }

    public void setPurchaseName(String purchaseName) {
        this.purchaseName = purchaseName;
    }

    public String getPurchaseStatus() {
        return purchaseStatus;
    }

    public void setPurchaseStatus(String purchaseStatus) {
        this.purchaseStatus = purchaseStatus;
    }

    public String getPurchaseType() {
        return purchaseType;
    }

    public void setPurchaseType(String purchaseType) {
        this.purchaseType = purchaseType;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
