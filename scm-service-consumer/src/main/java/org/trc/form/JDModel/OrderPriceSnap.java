package org.trc.form.JDModel;

import java.math.BigDecimal;

/**
 * Created by hzwdx on 2017/7/1.
 */
public class OrderPriceSnap {

    //商品编号
    private Long skuId;
    //商品价格
    private BigDecimal price;

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
