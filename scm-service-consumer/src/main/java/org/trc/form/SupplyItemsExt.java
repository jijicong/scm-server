package org.trc.form;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by hzszy on 2017/6/19.
 */
public class SupplyItemsExt implements Serializable{
    @ApiModelProperty(value = "主键ID")
    private Long id; //主键ID
    @ApiModelProperty(value = "供应商编码")
    private String supplierCode; //供应商编码
    @ApiModelProperty(value = "供应商名称")
    private String supplyName; //供应商名称
    @ApiModelProperty(value = "供应商商品Sku")
    private String supplySku;//供应商商品Sku
    @ApiModelProperty(value = "商品名称")
    private String upc; //商品名称
    @ApiModelProperty(value = "供应商售价")
    private BigDecimal supplierPrice; //供应商售价
    @ApiModelProperty(value = "供货价")
    private BigDecimal supplyPrice; //供货价
    @ApiModelProperty(value = "市场价")
    private BigDecimal marketPrice;//市场价
    @ApiModelProperty(value = "分类")
    private String category;//分类
    @ApiModelProperty(value = "分类名称")
    private String categoryCode;//分类名称
    @ApiModelProperty(value = "品牌")
    private String brand;//品牌
    @ApiModelProperty(value = "商品类型")
    private String skuType;//商品类型
    @ApiModelProperty(value = "重量")
    private BigDecimal weight;//重量
    @ApiModelProperty(value = "产地")
    private String productArea;//产地
    @ApiModelProperty(value = "销售单位")
    private String saleUnit;//销售单位
    @ApiModelProperty(value = "上下架状态：0-下架,1-上架")
    private String state;//上下架状态
    @ApiModelProperty(value = "商品详情文本")
    private String introduction;//商品详情文本
    @ApiModelProperty(value = "商品主图地址")
    private String imagePath;//商品主图地址
    @ApiModelProperty(value = "商品详情图")
    private String detailImagePath;//商品详情图
    @ApiModelProperty(value = "商品名称")
    private String skuName;//商品名称
    @ApiModelProperty(value = "是否已经使用")
    private String isUsed;//是否已经使用
    @ApiModelProperty(value = "是否有效:0-否,1-是")
    private String isValid; //是否有效:0-否,1-是
    @ApiModelProperty(value = "创建人")
    private String createOperator; //创建人
    @ApiModelProperty(value = "创建时间")
    private Date createTime; //创建时间
    @ApiModelProperty(value = "更新时间")
    private Date updateTime; //更新时间
    @ApiModelProperty(value = "是否删除:0-否,1-是")
    private String isDeleted; //是否删除:0-否,1-是
    private String highLightName;
    @ApiModelProperty(value = "库存")
    private Long stock;//库存
    @ApiModelProperty(value = "最小购买量")
    private Integer minBuyCount;
    /**
     * 被停用供应商编码，多个用逗号分隔
     */
    private String stopedSupplierCode;

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

    public String getHighLightName() {
        return highLightName;
    }

    public void setHighLightName(String highLightName) {
        this.highLightName = highLightName;
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

    public String getIsValid() {
        return isValid;
    }

    public void setIsValid(String isValid) {
        this.isValid = isValid;
    }

    public String getCreateOperator() {
        return createOperator;
    }

    public void setCreateOperator(String createOperator) {
        this.createOperator = createOperator;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(String isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public String getDetailImagePath() {
        return detailImagePath;
    }

    public void setDetailImagePath(String detailImagePath) {
        this.detailImagePath = detailImagePath;
    }

    public Long getStock() {
        return stock;
    }

    public void setStock(Long stock) {
        this.stock = stock;
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

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public String getStopedSupplierCode() {
        return stopedSupplierCode;
    }

    public void setStopedSupplierCode(String stopedSupplierCode) {
        this.stopedSupplierCode = stopedSupplierCode;
    }

    public Integer getMinBuyCount() {
        return minBuyCount;
    }

    public void setMinBuyCount(Integer minBuyCount) {
        this.minBuyCount = minBuyCount;
    }
}
