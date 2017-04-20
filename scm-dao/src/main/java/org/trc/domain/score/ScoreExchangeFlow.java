package org.trc.domain.score;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by george on 2017/3/31.
 */
public class ScoreExchangeFlow implements Serializable{

    private Long id;                         //主键id

    private Long converterId;               //规则id

    private Integer amount;                 //兑换额度

    private Integer score;                   //积分

    private String direction;                //兑换方向

    private Long personEverydayInLimit;        //每人每天可兑入限额（可兑换的外币数量）

    private Long personEverydayOutLimit;        //每人每天可兑出限额（可兑换的外币数量）

    private Long channelEverydayInLimit;       //渠道每天可兑入限额（可兑换的外币数量）

    private Long channelEverydayOutLimit;       //渠道每天可兑出限额（可兑换的外币数量）

    private String operatedBy;                //操作人

    private Date operatedTime;                //操作时间

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getConverterId() {
        return converterId;
    }

    public void setConverterId(Long converterId) {
        this.converterId = converterId;
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

    public String getOperatedBy() {
        return operatedBy;
    }

    public void setOperatedBy(String operatedBy) {
        this.operatedBy = operatedBy;
    }

    public Date getOperatedTime() {
        return operatedTime;
    }

    public void setOperatedTime(Date operatedTime) {
        this.operatedTime = operatedTime;
    }

    @Override
    public String toString() {
        return "ScoreExchangeFlow{" +
                "id=" + id +
                ", converterId=" + converterId +
                ", amount=" + amount +
                ", score=" + score +
                ", direction='" + direction + '\'' +
                ", personEverydayInLimit=" + personEverydayInLimit +
                ", personEverydayOutLimit=" + personEverydayOutLimit +
                ", channelEverydayInLimit=" + channelEverydayInLimit +
                ", channelEverydayOutLimit=" + channelEverydayOutLimit +
                ", operatedBy='" + operatedBy + '\'' +
                ", operatedTime=" + operatedTime +
                '}';
    }
}
