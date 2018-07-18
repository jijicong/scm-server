package org.trc.form.purchase;

import org.hibernate.validator.constraints.Length;
import org.trc.util.QueryModel;

import javax.ws.rs.QueryParam;

/**
 * Created by sone on 2017/5/27.
 */
public class ItemForm extends QueryModel{
    /**
     * sku名称
     */
    @QueryParam("skuName")
    @Length(max = 128)
    private String skuName;
    /**
     * 商品SKU-CODE, 多个用,分隔
     */
    @QueryParam("skuCode")
    @Length(max = 32)
    private String skuCode;
    /**
     * 品牌名称
     */
    @QueryParam("brandName")
    @Length(max = 256)
    private String brandName;
    /**
     * 货号
     */
    @QueryParam("itemNo")
    @Length(max = 32)
    private String itemNo;
    /**
     * 条形码, 多个用,分隔
     */
    @QueryParam("barCode")
    @Length(max = 64)
    private String barCode;

    @QueryParam("supplierCode")
    private String supplierCode;

    @QueryParam("warehouseInfoId")
    private String warehouseInfoId;




    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getItemNo() {
        return itemNo;
    }

    public void setItemNo(String itemNo) {
        this.itemNo = itemNo;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public String getSkuName() {
        return skuName;
    }

    public void setSkuName(String skuName) {
        this.skuName = skuName;
    }

    public String getSupplierCode() {
        return supplierCode;
    }

    public void setSupplierCode(String supplierCode) {
        this.supplierCode = supplierCode;
    }

    public String getWarehouseInfoId() {
        return warehouseInfoId;
    }

    public void setWarehouseInfoId(String warehouseInfoId) {
        this.warehouseInfoId = warehouseInfoId;
    }

}
