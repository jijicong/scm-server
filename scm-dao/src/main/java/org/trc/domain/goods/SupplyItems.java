package org.trc.domain.goods;

import org.trc.domain.BaseDO;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by hzszy on 2017/6/19.
 */
public class SupplyItems extends BaseDO {
    private Long id; //主键ID
    private String supplierCode; //供应商编码
    private String supplyName; //供应商名称
    private String supplySku;//供应商商品Sku
    private String upc; //商品名称
    private BigDecimal supplierPrice; //供应商售价
    private BigDecimal supplyPrice; //供货价
    private BigDecimal marketPrice;//市场价
    private String category;//分类
    private String categoryCode;//分类名称
    private String brand;//品牌
    private String skuType;//商品类型
    private Double weight;//重量
    private String productArea;//产地
    private String saleUnit;//销售单位
    private String state;//上下架状态
    private String introduction;//商品详情文本
    private String imagePath;//商品主图地址
    private String skuName;//商品名称
    private String detailImagePath;//商品明细图片地址
    private String isUsed;//是否已经使用
    private String warehouse;//仓库名称
    private Long stock;//库存
    private String updateFlag;
    private Date notifyTime;
    private Integer minBuyCount;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSupplyName() {
        return supplyName;
    }

    public String getSkuName() {
        return skuName;
    }

    public void setSkuName(String skuName) {
        this.skuName = skuName;
    }

    public void setSupplyName(String supplyName) {
        this.supplyName = supplyName;
    }

    public String getSupplySku() {
        return supplySku;
    }

    public void setSupplySku(String supplySku) {
        this.supplySku = supplySku;
    }

    public String getUpc() {
        return upc;
    }

    public void setUpc(String upc) {
        this.upc = upc;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getSkuType() {
        return skuType;
    }

    public void setSkuType(String skuType) {
        this.skuType = skuType;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public String getProductArea() {
        return productArea;
    }

    public void setProductArea(String productArea) {
        this.productArea = productArea;
    }

    public String getSaleUnit() {
        return saleUnit;
    }

    public void setSaleUnit(String saleUnit) {
        this.saleUnit = saleUnit;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getSupplierCode() {
        return supplierCode;
    }

    public void setSupplierCode(String supplierCode) {
        this.supplierCode = supplierCode;
    }

    public String getIsUsed() {
        return isUsed;
    }

    public void setIsUsed(String isUsed) {
        this.isUsed = isUsed;
    }

    public String getDetailImagePath() {
        return detailImagePath;
    }

    public void setDetailImagePath(String detailImagePath) {
        this.detailImagePath = detailImagePath;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public String getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(String warehouse) {
        this.warehouse = warehouse;
    }

    public BigDecimal getSupplierPrice() {
        return supplierPrice;
    }

    public void setSupplierPrice(BigDecimal supplierPrice) {
        this.supplierPrice = supplierPrice;
    }

    public BigDecimal getSupplyPrice() {
        return supplyPrice;
    }

    public void setSupplyPrice(BigDecimal supplyPrice) {
        this.supplyPrice = supplyPrice;
    }

    public BigDecimal getMarketPrice() {
        return marketPrice;
    }

    public void setMarketPrice(BigDecimal marketPrice) {
        this.marketPrice = marketPrice;
    }

    public Long getStock() {
        return stock;
    }

    public void setStock(Long stock) {
        this.stock = stock;
    }

    public String getUpdateFlag() {
        return updateFlag;
    }

    public void setUpdateFlag(String updateFlag) {
        this.updateFlag = updateFlag;
    }

    public Date getNotifyTime() {
        return notifyTime;
    }

    public void setNotifyTime(Date notifyTime) {
        this.notifyTime = notifyTime;
    }

    public Integer getMinBuyCount() {
        return minBuyCount;
    }

    public void setMinBuyCount(Integer minBuyCount) {
        this.minBuyCount = minBuyCount;
    }
}
