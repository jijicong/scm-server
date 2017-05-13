package org.trc.domain.impower;

import org.trc.domain.BaseDO;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.ws.rs.FormParam;

/**
 * 用户角色关系表
 * Created by sone on 2017/5/11.
 */
public class UserAccreditRoleRelation extends BaseDO{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userAccreditId;

    private Long userId; //用户中心的用户id

    private Long roleId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserAccreditId() {
        return userAccreditId;
    }

    public void setUserAccreditId(Long userAccreditId) {
        this.userAccreditId = userAccreditId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

}
