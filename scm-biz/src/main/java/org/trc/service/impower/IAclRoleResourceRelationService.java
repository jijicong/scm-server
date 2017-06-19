package org.trc.service.impower;

import org.trc.domain.impower.AclRoleResourceRelation;
import org.trc.service.IBaseService;

import java.util.List;

/**
 * Created by sone on 2017/5/16.
 */
public interface IAclRoleResourceRelationService extends IBaseService<AclRoleResourceRelation,Long>{
    /**
     * 根据角色的id查询 对应的权限id
     * @param roleId
     * @return
     * @throws Exception
     */
    List<Long> selectJurisdictionIdList(Long roleId) throws Exception;

    /**
     * 根据角色id删除该角色对应的权限
     * @param roleId
     * @return
     * @throws Exception
     */
    int deleteByRoleId(Long roleId) throws Exception;

    /**
     * 根据多个角色id查询角色信息
     * @param roleIds
     * @return
     * @throws Exception
     */
    List<AclRoleResourceRelation> selectListByRoleIds(Long ...roleIds)throws Exception;
}
