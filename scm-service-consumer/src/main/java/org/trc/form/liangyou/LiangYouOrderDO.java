package org.trc.form.liangyou;

import java.math.BigInteger;
import java.util.List;

/**
 * Created by hzwyz on 2017/6/13 0013.
 */
public class LiangYouOrderDO {
    private String consignee;

    private String orderSn;

    private String outOrderSn;

    private String realName;

    private String imId;

    private String phoneMob;

    private String address;

    private String province;

    private String city;

    private String county;

    private int shippingId;

    private List<OutOrderGoods> outOrderGoods;

    public String getConsignee() {
        return consignee;
    }

    public void setConsignee(String consignee) {
        this.consignee = consignee;
    }

    public String getOrderSn() {
        return orderSn;
    }

    public void setOrderSn(String orderSn) {
        this.orderSn = orderSn;
    }

    public String getOutOrderSn() {
        return outOrderSn;
    }

    public void setOutOrderSn(String outOrderSn) {
        this.outOrderSn = outOrderSn;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getImId() {
        return imId;
    }

    public void setImId(String imId) {
        this.imId = imId;
    }

    public String getPhoneMob() {
        return phoneMob;
    }

    public void setPhoneMob(String phoneMob) {
        this.phoneMob = phoneMob;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public int getShippingId() {
        return shippingId;
    }

    public void setShippingId(int shippingId) {
        this.shippingId = shippingId;
    }

    public List<OutOrderGoods> getOutOrderGoods() {
        return outOrderGoods;
    }

    public void setOutOrderGoods(List<OutOrderGoods> outOrderGoods) {
        this.outOrderGoods = outOrderGoods;
    }
}
