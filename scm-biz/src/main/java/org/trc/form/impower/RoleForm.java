package org.trc.form.impower;

import org.hibernate.validator.constraints.Length;
import org.trc.util.QueryModel;

import javax.ws.rs.QueryParam;

/**
 * Created by sone on 2017/5/11.
 */
public class RoleForm extends QueryModel{
    /**
     * 角色名称
     */
    @QueryParam("name")
    @Length(max = 64)
    private String name;
    @QueryParam("roleType")
    @Length(max = 32)
    private String roleType;

    public String getRoleType() {
        return roleType;
    }

    public void setRoleType(String roleType) {
        this.roleType = roleType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
