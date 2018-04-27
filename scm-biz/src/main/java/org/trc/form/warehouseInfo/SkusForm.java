package org.trc.form.warehouseInfo;

import org.trc.util.QueryModel;

import javax.ws.rs.QueryParam;

/**
 * Created by wangyz on 2017/11/17.
 */
public class SkusForm extends QueryModel {
    //商品sku编号
    @QueryParam("skuCode")
    private String skuCode;

    //商品名称
    @QueryParam("skuName")
    private String skuName;

    //商品spu编号
    @QueryParam("spuCode")
    private String spuCode;

    //商品品牌
    @QueryParam("brandName")
    private String brandName;

    @QueryParam("barCode")
    private String barCode;

    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }

    public String getSkuName() {
        return skuName;
    }

    public void setSkuName(String skuName) {
        this.skuName = skuName;
    }

    public String getSpuCode() {
        return spuCode;
    }

    public void setSpuCode(String spuCode) {
        this.spuCode = spuCode;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }
}
