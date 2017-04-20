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
@Table(name = "score_converter")
@NameStyle(Style.normal)
public class ScoreConverter implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                         //主键id

    private Long shopId;                    //店铺Id

    private String channelCode;             //频道编码

    private String exchangeCurrency;        //兑换币种

    private Integer amount;                 //兑换额度

    private Integer score;                   //积分

    private String direction;                //兑换方向

    private Long personEverydayInLimit;        //每人每天可兑入限额（可兑换的外币数量）

    private Long personEverydayOutLimit;        //每人每天可兑出限额（可兑换的外币数量）

    private Long channelEverydayInLimit;       //渠道每天可兑入限额（可兑换的外币数量）

    private Long channelEverydayOutLimit;       //渠道每天可兑出限额（可兑换的外币数量）

    private Integer isDeleted;                   //状态 1:存在；0：删除

    private String createBy;                 //创建人

    private Date createTime;                 //创建时间

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
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

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public Long getPersonEverydayInLimit() {
        return personEverydayInLimit;
    }

    public void setPersonEverydayInLimit(Long personEverydayInLimit) {
        this.personEverydayInLimit = personEverydayInLimit;
    }

    public Long getPersonEverydayOutLimit() {
        return personEverydayOutLimit;
    }

    public void setPersonEverydayOutLimit(Long personEverydayOutLimit) {
        this.personEverydayOutLimit = personEverydayOutLimit;
    }

    public Long getChannelEverydayInLimit() {
        return channelEverydayInLimit;
    }

    public void setChannelEverydayInLimit(Long channelEverydayInLimit) {
        this.channelEverydayInLimit = channelEverydayInLimit;
    }

    public Long getChannelEverydayOutLimit() {
        return channelEverydayOutLimit;
    }

    public void setChannelEverydayOutLimit(Long channelEverydayOutLimit) {
        this.channelEverydayOutLimit = channelEverydayOutLimit;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "ScoreConverter{" +
                "id=" + id +
                ", shopId=" + shopId +
                ", channelCode='" + channelCode + '\'' +
                ", exchangeCurrency='" + exchangeCurrency + '\'' +
                ", amount=" + amount +
                ", score=" + score +
                ", direction='" + direction + '\'' +
                ", personEverydayInLimit=" + personEverydayInLimit +
                ", personEverydayOutLimit=" + personEverydayOutLimit +
                ", channelEverydayInLimit=" + channelEverydayInLimit +
                ", channelEverydayOutLimit=" + channelEverydayOutLimit +
                ", isDeleted=" + isDeleted +
                ", createBy='" + createBy + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}
