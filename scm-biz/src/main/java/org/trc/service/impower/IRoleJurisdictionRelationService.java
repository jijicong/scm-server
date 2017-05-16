package org.trc.service.impower;

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

}
