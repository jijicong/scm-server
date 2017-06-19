package org.trc.service.impl.impower;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.trc.domain.impower.AclUserAccreditRoleRelation;
import org.trc.mapper.impower.AclUserAccreditRoleRelationMapper;
import org.trc.service.impl.BaseService;
import org.trc.service.impower.IAclUserAccreditRoleRelationService;

import java.util.List;
import java.util.Map;

/**
 * Created by sone on 2017/5/16.
 */
@Service("userAccreditInfoRoleRelationService")
public class AclUserAccreditRoleRelationService extends BaseService<AclUserAccreditRoleRelation, Long> implements IAclUserAccreditRoleRelationService {

    @Autowired
    private AclUserAccreditRoleRelationMapper aclUserAccreditRoleRelationMapper;

    @Override
    public int deleteByUserAccreditId(Long userAccreditId){
       return   aclUserAccreditRoleRelationMapper.deleteByUserAccreditId(userAccreditId);
    }

    @Override
    public List<AclUserAccreditRoleRelation> selectListByUserAcId(Long userAccreditId){
        return aclUserAccreditRoleRelationMapper.selectListByUserAcId(userAccreditId);
    }

    @Override
    public void updateStatusByRoleId(Map<String,Object> map) {
        aclUserAccreditRoleRelationMapper.updateStatusByRoleId(map);
    }

}
