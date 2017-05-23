package org.trc.domain.impower;

import org.trc.domain.BaseDO;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * 角色权限关系表
 * Created by sone on 2017/5/11.
 */
public class RoleJurisdictionRelation extends BaseDO{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long roleId;

    private Long jurisdictionCode;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public Long getJurisdictionCode() {
        return jurisdictionCode;
    }

    public void setJurisdictionCode(Long jurisdictionCode) {
        this.jurisdictionCode = jurisdictionCode;
    }
}
