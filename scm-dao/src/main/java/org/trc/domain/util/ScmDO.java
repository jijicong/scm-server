package org.trc.domain.util;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hibernate.validator.constraints.Length;
import org.trc.custom.CustomDateSerializer;

import javax.ws.rs.FormParam;
import java.util.Date;

/**
 * Created by hzwdx on 2017/5/9.
 */
public class ScmDO {

    @JsonSerialize(using = CustomDateSerializer.class)
    private Date createTime; //创建时间

    @JsonSerialize(using = CustomDateSerializer.class)
    private Date updateTime; //更新时间

    private String isDeleted; //是否删除:0-否,1-是

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
