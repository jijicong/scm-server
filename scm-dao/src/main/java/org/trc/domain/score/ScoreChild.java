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
@Table(name = "score_change")
@NameStyle(Style.normal)
public class ScoreChild implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                        //主键id

    private String userId;                  //唯一用户标示

    private Long scoreId;                   //积分账户标示

    private Long score;                     //积分

    private Long freezingScore;            //冻结积分

    private Long version;                   //版本号，时间戳微秒

    private Date expirationTime;           //过期时间

    private Integer isDeleted;                 //删除标示

    private Date createTime;                 //创建时间

    private Date updateTime;                //修改时间

    public ScoreChild() {

    }

    public ScoreChild(String userId, Long scoreId, Long score, Date expirationTime, Date createTime) {
        this.userId = userId;
        this.scoreId = scoreId;
        this.score = score;
        this.freezingScore = 0l;
        this.version = 1l;
        this.expirationTime = expirationTime;
        this.isDeleted = 0;
        this.createTime = createTime;
        this.updateTime = createTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Long getScoreId() {
        return scoreId;
    }

    public void setScoreId(Long scoreId) {
        this.scoreId = scoreId;
    }

    public Long getScore() {
        return score;
    }

    public void setScore(Long score) {
        this.score = score;
    }

    public Long getFreezingScore() {
        return freezingScore;
    }

    public void setFreezingScore(Long freezingScore) {
        this.freezingScore = freezingScore;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Date getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(Date expirationTime) {
        this.expirationTime = expirationTime;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "ScoreChild{" +
                "id=" + id +
                ", userId='" + userId + '\'' +
                ", scoreId=" + scoreId +
                ", score=" + score +
                ", freezingScore=" + freezingScore +
                ", version=" + version +
                ", expirationTime=" + expirationTime +
                ", isDeleted=" + isDeleted +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
