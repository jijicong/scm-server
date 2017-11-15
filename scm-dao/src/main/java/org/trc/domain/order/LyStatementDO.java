package org.trc.domain.order;


import javax.ws.rs.QueryParam;

/**
 * Created by wangyz on 2017/11/13.
 */
public class LyStatementDO {
    //商品SKU编号
    private String spuCode;
    //粮油商品SKU编号
    private String skuCode;
    //粮油商品名称
    private String itemName;
    //平台订单号
    private String platformOrderCode;
    //店铺订单号
    private String shopOrderCode;
    //粮油订单号
    private String supplierOrderCode;

    public String getSpuCode() {
        return spuCode;
    }

    public void setSpuCode(String spuCode) {
        this.spuCode = spuCode;
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
