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
 * 角色表
 * Created by sone on 2017/5/11.
 */
public class Role extends BaseDO {
    @FormParam("id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
