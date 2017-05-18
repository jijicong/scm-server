package org.trc.service.impower;

import org.trc.domain.impower.UserAccreditRoleRelation;
import org.trc.service.IBaseService;

/**
 * Created by sone on 2017/5/16.
 */
public interface IUserAccreditInfoRoleRelationService extends IBaseService<UserAccreditRoleRelation,Long> {

    /**
     *根据userAccreditId删除关联的角色
     */
    void deleteByUserAccreditId(Long userAccreditId) throws Exception;
}
