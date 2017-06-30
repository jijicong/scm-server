package org.trc.service.impl.impower;

import org.springframework.stereotype.Service;
import org.trc.domain.impower.AclRoleResourceRelation;
import org.trc.mapper.impower.AclRoleResourceRelationMapper;
import org.trc.service.impl.BaseService;
import org.trc.service.impower.IAclRoleResourceRelationService;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by sone on 2017/5/16.
 */
@Service("roleJurisdictionRelationService")
public class AclRoleResourceRelationService extends BaseService<AclRoleResourceRelation,Long> implements IAclRoleResourceRelationService {

    @Resource
    private AclRoleResourceRelationMapper aclRoleResourceRelationMapper;
    @Override
    public List<Long> selectJurisdictionIdList(Long roleId) {
        return aclRoleResourceRelationMapper.selectJurisdictionIdList(roleId);
    }

    @Override
    public int deleteByRoleId(Long roleId){
        return aclRoleResourceRelationMapper.deleteByRoleId(roleId);
    }

    @Override
    public List<AclRoleResourceRelation> selectListByRoleIds(Long... roleIds){
        return aclRoleResourceRelationMapper.selectListByRoleId(roleIds);
    }
}
