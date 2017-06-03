package org.trc.service.impower;

import org.apache.ibatis.annotations.Param;
import org.trc.domain.impower.RoleJurisdictionRelation;
import org.trc.service.IBaseService;

import java.util.List;

/**
 * Created by sone on 2017/5/16.
 */
public interface IRoleJurisdictionRelationService extends IBaseService<RoleJurisdictionRelation,Long>{
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
    List<RoleJurisdictionRelation> selectListByRoleIds(Long ...roleIds)throws Exception;
}
