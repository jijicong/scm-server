package org.trc.service.impl.impower;

import org.springframework.stereotype.Service;
import org.trc.domain.impower.RoleJurisdictionRelation;
import org.trc.mapper.impower.RoleJurisdictionRelationMapper;
import org.trc.service.impl.BaseService;
import org.trc.service.impower.IRoleJurisdictionRelationService;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by sone on 2017/5/16.
 */
@Service("roleJurisdictionRelationRelationService")
public class RoleJurisdictionRelationRelationService extends BaseService<RoleJurisdictionRelation,Long> implements IRoleJurisdictionRelationService {

    @Resource
    private RoleJurisdictionRelationMapper roleJurisdictionRelationMapper;
    @Override
    public List<Long> selectJurisdictionIdList(Long roleId) throws Exception {

        return null;
    }
}
