package org.trc.domain.purchase;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.trc.domain.BaseDO;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.ws.rs.FormParam;
import javax.ws.rs.PathParam;

/**
 * 采购组管理
 * Created by sone on 2017/5/19.
 */
public class PurchaseGroup extends BaseDO{
    @PathParam("id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @FormParam("code")
    @NotEmpty
    @Length(max = 32, message = "采购组编码字母和数字不能超过32个,汉字不能超过16个")
    private String code;
    @FormParam("name")
    @NotEmpty
    @Length(max = 64, message = "采购组名称字母和数字不能超过64个,汉字不能超过32个")
    private String name;
    @FormParam("leaderUserId")
    @NotEmpty
    @Length(max = 64, message = "采购组组长id字母和数字不能超过64个,汉字不能超过32个")
    private String leaderUserId;
    @FormParam("leaderName")
    @NotEmpty
    @Length(max = 64, message = "采购组组长名称字母和数字不能超过64个,汉字不能超过32个")
    private String leaderName;
    @FormParam("memberUserId")
    @Length(max = 1024, message = "采购组组员的ids字母和数字不能超过1024个,汉字不能超过512个")
    private String memberUserId;
    @FormParam("memberName")
    @Length(max =1024, message = "采购组组员的ids字母和数字不能超过1024个,汉字不能超过512个")
    private String memberName;
    @FormParam("remark")
    @Length(max =1024, message = "采购组组的备注字母和数字不能超过1024个,汉字不能超过512个")
    private  String remark;

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLeaderUserId() {
        return leaderUserId;
    }

    public void setLeaderUserId(String leaderUserId) {
        this.leaderUserId = leaderUserId;
    }

    public String getLeaderName() {
        return leaderName;
    }

    public void setLeaderName(String leaderName) {
        this.leaderName = leaderName;
    }

    public String getMemberUserId() {
        return memberUserId;
    }

    public void setMemberUserId(String memberUserId) {
        this.memberUserId = memberUserId;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    @Override
    public String toString() {
        return "PurchaseGroup{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", leaderUserId='" + leaderUserId + '\'' +
                ", leaderName='" + leaderName + '\'' +
                ", memberUserId='" + memberUserId + '\'' +
                ", memberName='" + memberName + '\'' +
                '}';
    }
}
