package org.trc.domain.impower;


import javax.ws.rs.FormParam;

/**
 * Created by hzszy on 2017/5/17.
 * 用于接收授权新增页面传进来的数据
 */
public class AclUserAddPageDate extends AclUserAccreditInfo {

    @FormParam("roleNames")
    private String roleNames;//角色名的名字加逗号的（name1,name2）

    public String getRoleNames() {
        return roleNames;
    }

    public void setRoleNames(String roleNames) {
        this.roleNames = roleNames;
    }


    public AclUserAddPageDate() {
    }

    public AclUserAddPageDate(AclUserAccreditInfo aclUserAccreditInfo) {
        this.setId(aclUserAccreditInfo.getId());
        this.setChannelCode(aclUserAccreditInfo.getChannelCode());
        this.setPhone(aclUserAccreditInfo.getPhone());
        this.setRemark(aclUserAccreditInfo.getRemark());
        this.setName(aclUserAccreditInfo.getName());
        this.setRoleNames(this.getRoleNames());
        this.setUserType(aclUserAccreditInfo.getUserType());
        this.setIsDeleted(aclUserAccreditInfo.getIsDeleted());
        this.setCreateOperator(aclUserAccreditInfo.getCreateOperator());
        this.setIsValid(aclUserAccreditInfo.getIsValid());
        this.setCreateTime(aclUserAccreditInfo.getCreateTime());
        this.setUserId(aclUserAccreditInfo.getUserId());
        this.setUpdateTime(aclUserAccreditInfo.getUpdateTime());
    }
}
