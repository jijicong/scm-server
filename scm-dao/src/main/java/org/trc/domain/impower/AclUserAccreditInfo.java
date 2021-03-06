package org.trc.domain.impower;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.trc.domain.BaseDO;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.ws.rs.FormParam;
import javax.ws.rs.PathParam;

/**
 * 用户授权列表
 *
 * @author sone
 * @date 2017/5/11
 * 对应数据库表user_accredit_info
 */
public class AclUserAccreditInfo extends BaseDO{//acl_user_accredit_info
    @PathParam("id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @FormParam("userId")
    private String userId;

    //必须已经存在用户中心的id
    @FormParam("phone")
    @NotEmpty
    @Length(max = 64, message = "用户授权电话字母和数字不能超过64个,汉字不能超过32个")
    private String phone;

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

    @FormParam("channelCode")
    @Length(max = 32)
    private String channelCode;
    @Transient
    private Long channelId;
    @Transient
    private String channelName;
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
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
        this.name = name == null ? null : name.trim();
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

    public String getChannelCode() {
        return channelCode;
    }

    public void setChannelCode(String channelCode) {
        this.channelCode = channelCode;
    }

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}

