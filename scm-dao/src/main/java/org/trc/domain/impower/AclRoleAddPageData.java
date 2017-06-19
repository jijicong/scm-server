package org.trc.domain.impower;

import javax.ws.rs.FormParam;

/**
 * Created by sone on 2017/5/16.
 */
public class AclRoleAddPageData extends AclRole {

    @FormParam("roleJurisdiction")
    private String  roleJurisdiction;

    public String getRoleJurisdiction() {
        return roleJurisdiction;
    }

    public void setRoleJurisdiction(String roleJurisdiction) {
        this.roleJurisdiction = roleJurisdiction;
    }

}
