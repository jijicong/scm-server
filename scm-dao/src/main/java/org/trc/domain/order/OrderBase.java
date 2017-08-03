package org.trc.domain.order;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.trc.custom.CustomDateSerializer;

import javax.persistence.Transient;
import java.util.Date;

/**
 * Created by hzwdx on 2017/6/26.
 */
public class OrderBase {

    // 用户id
    @Transient
    private String userId;

    // 会员名称
    @Transient
    private String userName;

    // 订单类型：0-普通订单 1-零元购 2-分期购 3-拼团
    @Transient
    private String type;

    // 支付时间
    @Transient
    @JsonSerialize(using = CustomDateSerializer.class)
    private Date payTime;

    // 收货人所在省
    @Transient
    private String receiverProvince;

    // 收货人所在城市
    @Transient
    private String receiverCity;

    // 收货人所在地区
    @Transient
    private String receiverDistrict;

    // 收货人详细地址
    @Transient
    private String receiverAddress;

    // 收货人姓名
    @Transient
    private String receiverName;

    // 收货人电话号码
    @Transient
    private String receiverPhone;

    // 收货人手机号码
    @Transient
    private String receiverMobile;

    // 买家留言
    @Transient
    private String buyerMessage;

    // 卖家备注
    @Transient
    private String shopMemo;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getPayTime() {
        return payTime;
    }

    public void setPayTime(Date payTime) {
        this.payTime = payTime;
    }

    public String getReceiverProvince() {
        return receiverProvince;
    }

    public void setReceiverProvince(String receiverProvince) {
        this.receiverProvince = receiverProvince;
    }

    public String getReceiverCity() {
        return receiverCity;
    }

    public void setReceiverCity(String receiverCity) {
        this.receiverCity = receiverCity;
    }

    public String getReceiverDistrict() {
        return receiverDistrict;
    }

    public void setReceiverDistrict(String receiverDistrict) {
        this.receiverDistrict = receiverDistrict;
    }

    public String getReceiverAddress() {
        return receiverAddress;
    }

    public void setReceiverAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }

    public String getReceiverMobile() {
        return receiverMobile;
    }

    public void setReceiverMobile(String receiverMobile) {
        this.receiverMobile = receiverMobile;
    }

    public String getBuyerMessage() {
        return buyerMessage;
    }

    public void setBuyerMessage(String buyerMessage) {
        this.buyerMessage = buyerMessage;
    }

    public String getShopMemo() {
        return shopMemo;
    }

    public void setShopMemo(String shopMemo) {
        this.shopMemo = shopMemo;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
