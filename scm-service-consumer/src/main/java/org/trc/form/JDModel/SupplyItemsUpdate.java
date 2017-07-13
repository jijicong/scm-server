package org.trc.form.JDModel;

import java.math.BigDecimal;

/**京东商品价格上下架状态更新
 * Created by hzszy on 2017/6/22.
 */
public class SupplyItemsUpdate {
    //商品编号
    private String skuId;
    //客户购买价格
    private BigDecimal price;
    //京东价格
    private BigDecimal jdPrice;

    public String getSkuId() {
        return skuId;
    }

    public void setSkuId(String skuId) {
        this.skuId = skuId;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getJdPrice() {
        return jdPrice;
    }

    public void setJdPrice(BigDecimal jdPrice) {
        this.jdPrice = jdPrice;
    }
}

