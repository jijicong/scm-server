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
public class RoleAddPageData extends Role{

    @FormParam("roleJurisdiction")
    private String  roleJurisdiction;

    public String getRoleJurisdiction() {
        return roleJurisdiction;
    }

    public void setRoleJurisdiction(String roleJurisdiction) {
        this.roleJurisdiction = roleJurisdiction;
    }

}
