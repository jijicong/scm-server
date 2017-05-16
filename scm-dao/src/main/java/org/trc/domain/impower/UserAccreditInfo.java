package org.trc.domain.impower;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.trc.domain.BaseDO;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.ws.rs.FormParam;

/**
 * 用户授权列表
 * Created by sone on 2017/5/11.
 */
public class UserAccreditInfo extends BaseDO{
    @FormParam("id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @FormParam("userId")
    @NotEmpty
    private Long userId;
    @FormParam("phone")
    @NotEmpty
    @Length(max = 64, message = "用户授权电话字母和数字不能超过64个,汉字不能超过32个")
    private String phone;//必须已经存在用户中心的id
    @FormParam("name")
    @NotEmpty
    @Length(max = 64, message = "用户授权名称字母和数字不能超过64个,汉字不能超过32个")
    private String name;
    @FormParam("userType")
    @NotEmpty
    @Length(max = 16, message = "用户授权名称字母和数字不能超过16个,汉字不能超过8个")
    private String userType;
    @FormParam("remark")
    @Length(max = 1024, message = "用户授权名称字母和数字不能超过1024个,汉字不能超过512个")
    private String remark;

    @Transient
    private String roleNames;//角色名的名字加逗号的（name1,name2）

    public String getRoleNames() {
        return roleNames;
    }

    public void setRoleNames(String roleNames) {
        this.roleNames = roleNames;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}