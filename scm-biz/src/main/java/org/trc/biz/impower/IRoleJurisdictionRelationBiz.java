package org.trc.biz.impower;

/**
 * Created by sone on 2017/5/12.
 */
public interface IRoleJurisdictionRelationBiz {
    /**
     * 批量插入角色与其关系表
     * @return
     */
    int saveRoleJurisdictionRelationS(String ids,Long roleId) throws Exception;

}
