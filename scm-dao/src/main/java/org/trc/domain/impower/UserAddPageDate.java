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


    public UserAddPageDate() {
    }

    public UserAddPageDate(UserAccreditInfo userAccreditInfo) {
        this.setId(userAccreditInfo.getId());
        this.setChannelCode(userAccreditInfo.getChannelCode());
        this.setPhone(userAccreditInfo.getPhone());
        this.setRemark(userAccreditInfo.getRemark());
        this.setName(userAccreditInfo.getName());
        this.setRoleNames(this.getRoleNames());
        this.setUserType(userAccreditInfo.getUserType());
        this.setIsDeleted(userAccreditInfo.getIsDeleted());
        this.setCreateOperator(userAccreditInfo.getCreateOperator());
        this.setIsValid(userAccreditInfo.getIsValid());
        this.setCreateTime(userAccreditInfo.getCreateTime());
        this.setUserId(userAccreditInfo.getUserId());
        this.setUpdateTime(userAccreditInfo.getUpdateTime());
    }
}
