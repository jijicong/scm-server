package org.trc.form.goods;

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
    @Length(max = 128, message = "商品名称长度不能超过128个")
    private String itemName;
    @QueryParam("warehouse")
    @Length(max = 32, message = "仓库长度不能超过32个")
    private String warehouse;
    @QueryParam("brand")
    @Length(max = 32, message = "商品编号长度不能超过32个")
    private String brand;
    @QueryParam("barCode")
    @Length(max = 64, message = "条形码长度不能超过64个")
    private String barCode;
    @QueryParam("supplierSkuCode")
    @Length(max = 32, message = "供应商商品sku编号长度不能超过32个")
    private String supplierSkuCode;

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
}
