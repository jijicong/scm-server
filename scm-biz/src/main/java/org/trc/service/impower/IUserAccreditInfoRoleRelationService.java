package org.trc.service.impower;

import org.apache.ibatis.annotations.Param;
import org.trc.domain.impower.UserAccreditRoleRelation;
import org.trc.service.IBaseService;

import java.util.List;

/**
 * Created by sone on 2017/5/16.
 */
public interface IUserAccreditInfoRoleRelationService extends IBaseService<UserAccreditRoleRelation, Long> {

    /**
     * 根据userAccreditId删除关联的角色
     */
    void deleteByUserAccreditId(Long userAccreditId) throws Exception;

    /**
     * 根据用户授权信息表id查询用户角色id
     *
     * @param userAccreditId
     * @return
     * @throws Exception
     */
    List<UserAccreditRoleRelation> selectListByUserAcId(Long userAccreditId) throws Exception;
}
