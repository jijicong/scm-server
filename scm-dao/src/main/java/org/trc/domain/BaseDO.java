package org.trc.domain;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hibernate.validator.constraints.Length;
import org.trc.custom.CustomDateSerializer;
import org.trc.domain.util.CommonDO;

import javax.persistence.Transient;
import javax.ws.rs.FormParam;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by hzwdx on 2017/4/19.
 */
public class BaseDO extends CommonDO implements Serializable{

    @FormParam("isValid")
    @Length(max = 2, message = "是否有编码字母和数字不能超过2个")
    private String isValid; //是否有效:0-否,1-是
    @Transient
    private String deleteAuth;
    @Transient
    private String addAuth;
    @Transient
    private String updateAuth;
    @Transient
    private String selectAuth;
    public String getIsValid() {
        return isValid;
    }

    public void setIsValid(String isValid) {
        this.isValid = isValid;
    }

    public String getDeleteAuth() {
        return deleteAuth;
    }

    public void setDeleteAuth(String deleteAuth) {
        this.deleteAuth = deleteAuth;
    }

    public String getAddAuth() {
        return addAuth;
    }

    public void setAddAuth(String addAuth) {
        this.addAuth = addAuth;
    }

    public String getUpdateAuth() {
        return updateAuth;
    }

    public void setUpdateAuth(String updateAuth) {
        this.updateAuth = updateAuth;
    }

    public String getSelectAuth() {
        return selectAuth;
    }

    public void setSelectAuth(String selectAuth) {
        this.selectAuth = selectAuth;
    }
}
