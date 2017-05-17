package org.trc.domain.impower;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.trc.domain.BaseDO;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.ws.rs.FormParam;
import javax.ws.rs.PathParam;

/**
 * Created by sone on 2017/5/16.
 */
public class RoleExpand extends BaseDO{
    @PathParam("id")
    private Long id;
    @FormParam("name")
    @NotEmpty
    @Length(max = 64, message = "角色名称字母和数字不能超过64个,汉字不能超过32个")
    private String name;
    @FormParam("roleType")
    @NotEmpty
    @Length(max = 32, message = "角色类型字母和数字不能超过32个,汉字不能超过16个")
    private String roleType;
    @FormParam("remark")
    @Length(max = 1024, message = "角色信息备注字母和数字不能超过1024个,汉字不能超过512个")
    private String remark;
    @FormParam("roleJurisdiction")
    private String  roleJurisdiction;

    public String getRoleJurisdiction() {
        return roleJurisdiction;
    }

    public void setRoleJurisdiction(String roleJurisdiction) {
        this.roleJurisdiction = roleJurisdiction;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRoleType() {
        return roleType;
    }

    public void setRoleType(String roleType) {
        this.roleType = roleType;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

}
