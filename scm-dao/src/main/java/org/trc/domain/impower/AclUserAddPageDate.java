package org.trc.domain.impower;


import org.trc.domain.System.ChannelExt;

import javax.ws.rs.FormParam;
import java.util.List;

/**
 *
 * @author hzszy
 * @date 2017/5/17
 * 用于接收授权新增页面传进来的数据
 */
public class AclUserAddPageDate extends AclUserAccreditInfo {
    /**
     * 角色名的名字加逗号的（name1,name2）
     */
    @FormParam("roleNames")
    private String roleNames;

    @FormParam("channelMsg")
    private String channelMsg;

    private List<ChannelExt> channelExtList;

    public String getRoleNames() {
        return roleNames;
    }

    public void setRoleNames(String roleNames) {
        this.roleNames = roleNames;
    }

    public List<ChannelExt> getChannelExtList() {
        return channelExtList;
    }

    public void setChannelExtList(List<ChannelExt> channelExtList) {
        this.channelExtList = channelExtList;
    }

    public String getChannelMsg() {
        return channelMsg;
    }

    public void setChannelMsg(String channelMsg) {
        this.channelMsg = channelMsg;
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
