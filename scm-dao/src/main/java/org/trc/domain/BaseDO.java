package org.trc.domain;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.trc.custom.CustomDateSerializer;

import javax.ws.rs.FormParam;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by hzwdx on 2017/4/19.
 */
public class BaseDO implements Serializable{
    @FormParam("isValid")
    private String isValid; //是否有效:0-否,1-是
    @FormParam("createOperator")
    private String createOperator; //创建人

    @JsonSerialize(using = CustomDateSerializer.class)
    private Date createTime; //创建时间

    @JsonSerialize(using = CustomDateSerializer.class)
    private Date updateTime; //更新时间

    private String isDeleted; //是否删除:0-否,1-是

    public String getIsValid() {
        return isValid;
    }

    public void setIsValid(String isValid) {
        this.isValid = isValid;
    }

    public String getCreateOperator() {
        return createOperator;
    }

    public void setCreateOperator(String createOperator) {
        this.createOperator = createOperator;
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

    public String getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(String isDeleted) {
        this.isDeleted = isDeleted;
    }
}
