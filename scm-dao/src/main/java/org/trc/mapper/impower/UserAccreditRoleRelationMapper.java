package org.trc.mapper.impower;

import org.trc.domain.impower.UserAccreditRoleRelation;
import org.trc.util.BaseMapper;

/**
 * Created by sone on 2017/5/11.
 */
public interface UserAccreditRoleRelationMapper extends BaseMapper<UserAccreditRoleRelation>{

    /**
     *根据userAccreditId删除关联的角色
     */
    void deleteByUserAccreditId(Long userAccreditId) throws Exception;

}
