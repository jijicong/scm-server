package org.trc.domain.warehouseNotice;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

/**
 * Created by sone on 2017/7/11.
 */
public class WarehouseNoticeDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //'入库通知单编号',
    private String warehouseNoticeCode;
    //'商品名称',
    private String skuName;
    //'sku编码',
    private String skuCode;
    //'品牌',
    private String brandCode;
    //'分类',
    private String categoryCode;
    //'采购价,单位/分',
    private Long  purchasePrice;
    //'采购数量',
    private Long purchasingQuantity;
    //'实际入库数量',
    private Long actualStorageQuantity;
    //'创建时间,格式yyyy-mm-dd hh:mi:ss',
    private Date createTime;
    //'入库时间,格式yyyy-mm-dd hh:mi:ss',
    private Date storageTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWarehouseNoticeCode() {
        return warehouseNoticeCode;
    }

    public void setWarehouseNoticeCode(String warehouseNoticeCode) {
        this.warehouseNoticeCode = warehouseNoticeCode;
    }

    public String getSkuName() {
        return skuName;
    }

    public void setSkuName(String skuName) {
        this.skuName = skuName;
    }

    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }

    public String getBrandCode() {
        return brandCode;
    }

    public void setBrandCode(String brandCode) {
        this.brandCode = brandCode;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public Long getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(Long purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public Long getPurchasingQuantity() {
        return purchasingQuantity;
    }

    public void setPurchasingQuantity(Long purchasingQuantity) {
        this.purchasingQuantity = purchasingQuantity;
    }

    public Long getActualStorageQuantity() {
        return actualStorageQuantity;
    }

    public void setActualStorageQuantity(Long actualStorageQuantity) {
        this.actualStorageQuantity = actualStorageQuantity;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getStorageTime() {
        return storageTime;
    }

    public void setStorageTime(Date storageTime) {
        this.storageTime = storageTime;
    }
}
