package org.trc.mapper.impower;

import org.apache.ibatis.annotations.Param;
import org.trc.domain.impower.RoleJurisdictionRelation;
import org.trc.util.BaseMapper;

import java.util.List;

/**
 * Created by sone on 2017/5/11.
 */
public interface RoleJurisdictionRelationMapper extends BaseMapper<RoleJurisdictionRelation>{
    /**
     * 根据角色的id查询对应的权限
     * @param roleId
     * @return
     */
    List<Long> selectJurisdictionIdList(Long roleId);

    /**
     * 根据角色id删除该角色对应的权限
     * @param roleId
     * @return
     */
    int deleteByRoleId(Long roleId);

    /**
     * 根据多个角色id查询角色信息
     * @param roleIds
     * @return
     * @throws Exception
     */
    List<RoleJurisdictionRelation> selectListByRoleId(@Param("roleIds")Long ...roleIds)throws Exception;
}
