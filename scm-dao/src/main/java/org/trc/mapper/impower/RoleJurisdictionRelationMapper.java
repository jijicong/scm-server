package org.trc.mapper.impower;

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

}
