package org.trc.domain.impower;


import javax.ws.rs.FormParam;

/**
 * Created by hzszy on 2017/5/17.
 * 用于接收授权新增页面传进来的数据
 */
public class UserAddPageDate extends UserAccreditInfo {

    @FormParam("roleNames")
    private String roleNames;//角色名的名字加逗号的（name1,name2）

    public String getRoleNames() {
        return roleNames;
    }

    public void setRoleNames(String roleNames) {
        this.roleNames = roleNames;
    }
}
