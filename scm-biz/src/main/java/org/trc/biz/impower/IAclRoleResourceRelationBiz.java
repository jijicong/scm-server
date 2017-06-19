package org.trc.biz.impower;

/**
 * Created by sone on 2017/5/16.
 */
public interface IAclRoleResourceRelationBiz {
    /**
     * 角色和权限关联表的更新
     * @param roleJurisdiction
     * @param roleId
     * @throws Exception
     */
    void updateRoleJurisdictionRelations(String roleJurisdiction,Long roleId) throws Exception;

    /**
     * 角色和权限关联表的保存
     * @param roleJurisdiction
     * @param roleId
     * @return
     * @throws Exception
     */
    void  saveRoleJurisdictionRelations(String roleJurisdiction,Long roleId) throws Exception;

}
