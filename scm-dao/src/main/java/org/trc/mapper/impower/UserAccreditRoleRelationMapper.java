package org.trc.mapper.impower;

import org.apache.ibatis.annotations.Param;
import org.trc.domain.impower.UserAccreditRoleRelation;
import org.trc.util.BaseMapper;

import java.util.List;
import java.util.Map;

/**
 * Created by sone on 2017/5/11.
 */
public interface UserAccreditRoleRelationMapper extends BaseMapper<UserAccreditRoleRelation>{

    /**
     *根据userAccreditId删除关联的角色
     */
    void deleteByUserAccreditId(Long userAccreditId);

    /**
     * 根据用户授权信息表id查询用户角色id
     * @param userAccreditId
     * @return
     * @throws Exception
     */
    List<UserAccreditRoleRelation> selectListByUserAcId(@Param("userAccreditId")Long userAccreditId);

    /**
     *根据角色的id修改关联状态
     */
    void updateStatusByRoleId(Map<String,Object> map);

}
