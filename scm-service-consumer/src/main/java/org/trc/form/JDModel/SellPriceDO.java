package org.trc.form.JDModel;

/**
 * Created by hzwyz on 2017/5/26 0026.
 */
public class SellPriceDO {
    //商品编号
    private String skuId;

    //客户购买价格
    private String price;

    //京东价格
    private String jdPrice;

    public String getSkuId() {
        return skuId;
    }

    public void setSkuId(String skuId) {
        this.skuId = skuId;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getJdPrice() {
        return jdPrice;
    }

    public void setJdPrice(String jdPrice) {
        this.jdPrice = jdPrice;
    }
}
