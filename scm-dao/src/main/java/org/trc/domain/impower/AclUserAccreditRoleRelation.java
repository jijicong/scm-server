package org.trc.domain.impower;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hibernate.validator.constraints.Length;
import org.trc.custom.CustomDateSerializer;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.ws.rs.FormParam;
import java.io.Serializable;
import java.util.Date;

/**
 * 用户角色关系表
 * Created by sone on 2017/5/11.
 * 对应数据库表user_accredit_role_relation
 */
public class AclUserAccreditRoleRelation implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userAccreditId;

    private String userId; //用户中心的用户id

    private Long roleId;
    @FormParam("isValid")
    @Length(max = 2, message = "是否有编码字母和数字不能超过2个")
    private String isValid; //是否有效:0-否,1-是

    @FormParam("createOperator")
    @Length(max = 32, message = "字典类型编码字母和数字不能超过32个,汉字不能超过16个")
    private String createOperator; //创建人

    @JsonSerialize(using = CustomDateSerializer.class)
    private Date createTime; //创建时间

    @JsonSerialize(using = CustomDateSerializer.class)
    private Date updateTime; //更新时间

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserAccreditId() {
        return userAccreditId;
    }

    public void setUserAccreditId(Long userAccreditId) {
        this.userAccreditId = userAccreditId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

}
