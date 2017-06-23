package org.trc.domain.goods;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hibernate.validator.constraints.Length;
import org.trc.custom.CustomDateSerializer;
import org.trc.custom.MoneySerializer;

import javax.persistence.*;
import javax.ws.rs.FormParam;
import javax.ws.rs.PathParam;
import java.util.Date;

/**
 * @author: Ding
 * @mail: hzdzf@tairanchina.com
 * @create: 2017-06-20 11:47
 */
@Table(name = "external_item_sku")
public class ExternalItemSku {

    // 主键
    @PathParam("id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 供应商id
    @FormParam("supplierId")
    private Long supplierId;

    // 供应链编号
    @FormParam("supplierCode")
    @Length(max = 32, message = "供应商编号长度不能超过32个")
    private String supplierCode;

    // 供应链名称
    @FormParam("supplierCode")
    @Length(max = 64, message = "供应商名称长度不能超过64个")
    private String supplierName;

    // 商品SKU编号
    @FormParam("skuCode")
    @Length(max = 32, message = "商品SKU编号长度不能超过32个")
    private String skuCode;

    // 供应商商品sku编号
    @FormParam("supplierSkuCode")
    @Length(max = 32, message = "供应商商品sku编号长度不能超过32个")
    private String supplierSkuCode;

    // 商品名称
    @FormParam("itemName")
    @Length(max = 128, message = "商品名称长度不能超过128个")
    private String itemName;

    // 条形码
    @FormParam("barCode")
    @Length(max = 64, message = "条形码长度不能超过64个")
    private String barCode;

    // 供货价,单位/分
    @JsonSerialize(using = MoneySerializer.class)
    private Long supplyPrice;

    // 供应商售价,单位/分
    @JsonSerialize(using = MoneySerializer.class)
    private Long supplierPrice;

    // 市场参考价,单位/分
    @JsonSerialize(using = MoneySerializer.class)
    private Long marketReferencePrice;

    // 仓库
    @FormParam("warehouse")
    @Length(max = 32, message = "仓库长度不能超过32个")
    private String warehouse;

    // 商品副标题
    @FormParam("subtitle")
    @Length(max = 64, message = "商品副标题长度不能超过64个")
    private String subtitle;

    // 品牌
    @FormParam("brand")
    @Length(max = 32, message = "品牌长度不能超过32个")
    private String brand;

    // 分类
    @FormParam("category")
    @Length(max = 32, message = "分类长度不能超过32个")
    private String category;

    // 分类名称
    @FormParam("categoryName")
    @Length(max = 64, message = "分类长度不能超过64个")
    private String categoryName;

    // 重量,单位/克
    @FormParam("weight")
    private Long weight;

    // 产地
    @FormParam("producingArea")
    @Length(max = 32, message = "产地长度不能超过32个")
    private String producingArea;

    // 发货地
    @FormParam("placeOfDelivery")
    @Length(max = 64, message = "发货地长度不能超过64个")
    private String placeOfDelivery;

    // 商品类型
    @FormParam("itemType")
    @Length(max = 16, message = "商品类型长度不能超过16个")
    private String itemType;

    // 1-普通商品 2-跨境直邮 3-跨境保税
    @FormParam("tariff")
    private Double tariff;

    // 商品主图
    @FormParam("mainPictrue")
    @Length(max = 256, message = "商品主图长度不能超过256个")
    private String mainPictrue;

    // 详情图
    @FormParam("detailPictrues")
    @Length(max = 32, message = "商品图片详情长度不能超过32个")
    private String detailPictrues;

    // 详情
    @FormParam("detail")
    @Length(max = 1024, message = "详情长度不能超过1024个")
    private String detail;

    // 属性
    @FormParam("properties")
    @Length(max = 512, message = "属性长度不能超过512个")
    private String properties;

    // 库存
    @FormParam("stock")
    private Long stock;

    // 是否有效:0-无效,1-有效
    @FormParam("stock")
    @Length(max = 2, message = "是否有效长度不能超过2个")
    private String isValid;

    // 是否删除:0-否,1-是
    @Length(max = 2, message = "是否删除长度不能超过2个")
    private String isDeleted;

    // 创建时间,格式yyyy-mm-dd hh:mi:ss
    @JsonSerialize(using = CustomDateSerializer.class)
    private Date createTime;

    // 最后更新时间,格式yyyy-mm-dd hh:mi:ss
    @JsonSerialize(using = CustomDateSerializer.class)
    private Date updateTime;

    //京东图片url
    @Transient
    private String jdPictureUrl;

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
        this.supplierCode = supplierCode;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }

    public String getSupplierSkuCode() {
        return supplierSkuCode;
    }

    public void setSupplierSkuCode(String supplierSkuCode) {
        this.supplierSkuCode = supplierSkuCode;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public Long getSupplyPrice() {
        return supplyPrice;
    }

    public void setSupplyPrice(Long supplyPrice) {
        this.supplyPrice = supplyPrice;
    }

    public Long getSupplierPrice() {
        return supplierPrice;
    }

    public void setSupplierPrice(Long supplierPrice) {
        this.supplierPrice = supplierPrice;
    }

    public Long getMarketReferencePrice() {
        return marketReferencePrice;
    }

    public void setMarketReferencePrice(Long marketReferencePrice) {
        this.marketReferencePrice = marketReferencePrice;
    }

    public String getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(String warehouse) {
        this.warehouse = warehouse;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Long getWeight() {
        return weight;
    }

    public void setWeight(Long weight) {
        this.weight = weight;
    }

    public String getProducingArea() {
        return producingArea;
    }

    public void setProducingArea(String producingArea) {
        this.producingArea = producingArea;
    }

    public String getPlaceOfDelivery() {
        return placeOfDelivery;
    }

    public void setPlaceOfDelivery(String placeOfDelivery) {
        this.placeOfDelivery = placeOfDelivery;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public Double getTariff() {
        return tariff;
    }

    public void setTariff(Double tariff) {
        this.tariff = tariff;
    }

    public String getMainPictrue() {
        return mainPictrue;
    }

    public void setMainPictrue(String mainPictrue) {
        this.mainPictrue = mainPictrue;
    }

    public String getDetailPictrues() {
        return detailPictrues;
    }

    public void setDetailPictrues(String detailPictrues) {
        this.detailPictrues = detailPictrues;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getProperties() {
        return properties;
    }

    public void setProperties(String properties) {
        this.properties = properties;
    }

    public Long getStock() {
        return stock;
    }

    public void setStock(Long stock) {
        this.stock = stock;
    }

    public String getIsValid() {
        return isValid;
    }

    public void setIsValid(String isValid) {
        this.isValid = isValid;
    }

    public String getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(String isDeleted) {
        this.isDeleted = isDeleted;
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

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getJdPictureUrl() {
        return jdPictureUrl;
    }

    public void setJdPictureUrl(String jdPictureUrl) {
        this.jdPictureUrl = jdPictureUrl;
    }
}