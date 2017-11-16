package org.trc.form.liangyou;

import org.trc.util.QueryModel;

import javax.ws.rs.QueryParam;

/**
 * Created by wangyz on 2017/11/13.
 */
public class LyStatementForm extends QueryModel {
    //商品SKU编号
    @QueryParam("skuCode")
    private String skuCode;
    //粮油商品SKU编号
    @QueryParam("supplierSkuCode")
    private String supplierSkuCode;
    //粮油商品名称
    @QueryParam("itemName")
    private String itemName;
    //平台订单号
    @QueryParam("platformOrderCode")
    private String platformOrderCode;
    //店铺订单号
    @QueryParam("shopOrderCode")
    private String shopOrderCode;
    //粮油订单号
    @QueryParam("supplierOrderCode")
    private String supplierOrderCode;

    public String getSupplierSkuCode() {
        return supplierSkuCode;
    }

    public void setSupplierSkuCode(String supplierSkuCode) {
        this.supplierSkuCode = supplierSkuCode;
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

    public String getPlatformOrderCode() {
        return platformOrderCode;
    }

    public void setPlatformOrderCode(String platformOrderCode) {
        this.platformOrderCode = platformOrderCode;
    }

    public String getShopOrderCode() {
        return shopOrderCode;
    }

    public void setShopOrderCode(String shopOrderCode) {
        this.shopOrderCode = shopOrderCode;
    }

    public String getSupplierOrderCode() {
        return supplierOrderCode;
    }

    public void setSupplierOrderCode(String supplierOrderCode) {
        this.supplierOrderCode = supplierOrderCode;
    }
}
