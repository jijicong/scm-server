package org.trc.service.impl.impower;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.trc.domain.impower.UserAccreditRoleRelation;
import org.trc.mapper.impower.UserAccreditInfoMapper;
import org.trc.mapper.impower.UserAccreditRoleRelationMapper;
import org.trc.service.impl.BaseService;
import org.trc.service.impower.IUserAccreditInfoRoleRelationService;

import java.util.List;

/**
 * Created by sone on 2017/5/16.
 */
@Service("userAccreditInfoRoleRelationService")
public class UserAccreditInfoRoleRelationService extends BaseService<UserAccreditRoleRelation, Long> implements IUserAccreditInfoRoleRelationService {

    @Autowired
    private UserAccreditRoleRelationMapper userAccreditRoleRelationMapper;

    @Override
    public void deleteByUserAccreditId(Long userAccreditId) throws Exception {
        userAccreditRoleRelationMapper.deleteByUserAccreditId(userAccreditId);

    }

    @Override
    public List<UserAccreditRoleRelation> selectListByUserAcId(Long userAccreditId) throws Exception {
        return userAccreditRoleRelationMapper.selectListByUserAcId(userAccreditId);
    }
}
