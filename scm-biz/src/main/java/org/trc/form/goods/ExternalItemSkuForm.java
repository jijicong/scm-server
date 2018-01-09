package org.trc.form.goods;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hibernate.validator.constraints.Length;
import org.trc.util.QueryModel;

import javax.ws.rs.QueryParam;

/**
 * Created by hzwdx on 2017/6/20.
 */
public class ExternalItemSkuForm extends QueryModel{
    @QueryParam("supplierId")
    private Long supplierId;

    @QueryParam("supplierCode")
    @Length(max = 32, message = "供应商编号长度不能超过32个")
    private String supplierCode;

    @QueryParam("skuCode")
    @Length(max = 32, message = "商品SKU编号长度不能超过32个")
    private String skuCode;
    @QueryParam("itemName")
    @Length(max = 255, message = "商品名称长度不能超过255个")
    private String itemName;
    @QueryParam("warehouse")
    @Length(max = 32, message = "仓库长度不能超过32个")
    private String warehouse;
    @QueryParam("brand")
    @Length(max = 32, message = "商品长度不能超过32个")
    private String brand;
    @QueryParam("category")
    @Length(max = 32, message = "分类长度不能超过32个")
    private String category;
    @QueryParam("barCode")
    @Length(max = 64, message = "条形码长度不能超过64个")
    private String barCode;
    @QueryParam("supplierSkuCode")
    @Length(max = 32, message = "供应商商品sku编号长度不能超过32个")
    private String supplierSkuCode;
    @QueryParam("querySource")
    private String querySource;//0-商品查询 1-商品管理
    @QueryParam("state")
    private String state;//商品状态0-商品下架 1-商品上架

    //sku关联状态: 0-未关联,1-已关联
    @QueryParam("skuRelationStatus")
    private String skuRelationStatus;

    public String getSupplierCode() {
        return supplierCode;
    }

    public void setSupplierCode(String supplierCode) {
        this.supplierCode = supplierCode;
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(String warehouse) {
        this.warehouse = warehouse;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public String getSupplierSkuCode() {
        return supplierSkuCode;
    }

    public void setSupplierSkuCode(String supplierSkuCode) {
        this.supplierSkuCode = supplierSkuCode;
    }

    public String getQuerySource() {
        return querySource;
    }

    public void setQuerySource(String querySource) {
        this.querySource = querySource;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getSkuRelationStatus() {
        return skuRelationStatus;
    }

    public void setSkuRelationStatus(String skuRelationStatus) {
        this.skuRelationStatus = skuRelationStatus;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString(){
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}
