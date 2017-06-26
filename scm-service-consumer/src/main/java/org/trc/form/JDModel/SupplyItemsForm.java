package org.trc.form.JDModel;

import org.hibernate.validator.constraints.Length;
import org.trc.util.QueryModel;

import javax.ws.rs.QueryParam;

/**
 * Created by hzwdx on 2017/6/20.
 */
public class SupplyItemsForm extends QueryModel{

    @QueryParam("supplierCode")
    private String supplierCode; //供应商编号
    @QueryParam("supplySku")
    @Length(max = 32, message = "商品SKU编号长度不能超过32个")
    private String supplySku;//供应商商品Sku
    @QueryParam("skuName")
    @Length(max = 32, message = "商品名称长度不能超过32个")
    private String skuName;//商品名称

    public String getSupplierCode() {
        return supplierCode;
    }

    public void setSupplierCode(String supplierCode) {
        this.supplierCode = supplierCode;
    }

    public String getSupplySku() {
        return supplySku;
    }

    public void setSupplySku(String supplySku) {
        this.supplySku = supplySku;
    }

    public String getSkuName() {
        return skuName;
    }

    public void setSkuName(String skuName) {
        this.skuName = skuName;
    }
}
