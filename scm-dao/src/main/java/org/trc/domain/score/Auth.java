package org.trc.domain.score;

import tk.mybatis.mapper.annotation.NameStyle;
import tk.mybatis.mapper.code.Style;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by george on 2017/3/31.
 */
@Table(name = "score_auth")
@NameStyle(Style.normal)
public class Auth implements Serializable{

    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String channelCode;//平台code

    private String exchangeCurrency;      //外币

    private Long shopId;//店铺id

    private String phone;//用户账号

    private String userId;//用户id

    private String contactsUser;//用户姓名

    private Date createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getChannelCode() {
        return channelCode;
    }

    public void setChannelCode(String channelCode) {
        this.channelCode = channelCode;
    }

    public String getExchangeCurrency() {
        return exchangeCurrency;
    }

    public void setExchangeCurrency(String exchangeCurrency) {
        this.exchangeCurrency = exchangeCurrency;
    }

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getContactsUser() {
        return contactsUser;
    }

    public void setContactsUser(String contactsUser) {
        this.contactsUser = contactsUser;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "Auth{" +
                "id=" + id +
                ", channelCode='" + channelCode + '\'' +
                ", exchangeCurrency='" + exchangeCurrency + '\'' +
                ", shopId=" + shopId +
                ", phone='" + phone + '\'' +
                ", userId='" + userId + '\'' +
                ", contactsUser='" + contactsUser + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}
