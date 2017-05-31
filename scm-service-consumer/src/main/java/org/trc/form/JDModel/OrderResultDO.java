package org.trc.form.JDModel;

import com.alibaba.fastjson.JSONArray;

import java.util.List;

/**
 * Created by hzwyz on 2017/5/31 0031.
 */
public class OrderResultDO {
    private String jdOrderId;

    private String freight;

    private List sku;

    private String orderPrice;

    private String orderNakedPrice;

    private String orderTaxPrice;

    public String getJdOrderId() {
        return jdOrderId;
    }

    public void setJdOrderId(String jdOrderId) {
        this.jdOrderId = jdOrderId;
    }

    public String getFreight() {
        return freight;
    }

    public void setFreight(String freight) {
        this.freight = freight;
    }

    public List getSku() {
        return sku;
    }

    public void setSku(List sku) {
        this.sku = sku;
    }

    public String getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(String orderPrice) {
        this.orderPrice = orderPrice;
    }

    public String getOrderNakedPrice() {
        return orderNakedPrice;
    }

    public void setOrderNakedPrice(String orderNakedPrice) {
        this.orderNakedPrice = orderNakedPrice;
    }

    public String getOrderTaxPrice() {
        return orderTaxPrice;
    }

    public void setOrderTaxPrice(String orderTaxPrice) {
        this.orderTaxPrice = orderTaxPrice;
    }
}
