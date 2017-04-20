package org.trc.domain.score;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by george on 2017/3/31.
 */
public class ScoreSettlement implements Serializable{

    private Long id;

    private Long scoreId;

    private Long dailyBalance;

    private String accountDay;

    private Date createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getScoreId() {
        return scoreId;
    }

    public void setScoreId(Long scoreId) {
        this.scoreId = scoreId;
    }

    public Long getDailyBalance() {
        return dailyBalance;
    }

    public void setDailyBalance(Long dailyBalance) {
        this.dailyBalance = dailyBalance;
    }

    public String getAccountDay() {
        return accountDay;
    }

    public void setAccountDay(String accountDay) {
        this.accountDay = accountDay;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "ScoreSettlement{" +
                "id=" + id +
                ", scoreId=" + scoreId +
                ", dailyBalance=" + dailyBalance +
                ", accountDay='" + accountDay + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}
