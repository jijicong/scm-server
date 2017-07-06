package org.trc.form.liangyou;

import java.util.List;

/**
 * Created by hzwdx on 2017/7/6.
 */
public class LiangYouOrder {

    //收货人
    private String consignee;
    //商家订单号
    private String orderSn;
    //外部订单号
    private String outOrderSn;
    //身份证姓名
    private String realName;
    //身份证号码
    private String imId;
    //固定值 F
    private String disType;
    //电话
    private String phoneMob;
    //详细收货地址
    private String address;
    //省
    private String province;
    //市
    private String city;
    //区
    private String county;
    //导入订单中的配送方式Id，不用设置
    private String shippingId;
    //商品列表数组
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

    public String getDisType() {
        return disType;
    }

    public void setDisType(String disType) {
        this.disType = disType;
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

    public String getShippingId() {
        return shippingId;
    }

    public void setShippingId(String shippingId) {
        this.shippingId = shippingId;
    }

    public List<OutOrderGoods> getOutOrderGoods() {
        return outOrderGoods;
    }

    public void setOutOrderGoods(List<OutOrderGoods> outOrderGoods) {
        this.outOrderGoods = outOrderGoods;
    }
}
