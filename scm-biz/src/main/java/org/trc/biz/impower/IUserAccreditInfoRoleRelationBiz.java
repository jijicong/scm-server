package org.trc.biz.impower;

/**
 * Created by sone on 2017/5/15.
 */
public interface IUserAccreditInfoRoleRelationBiz {

    /**
     * 根据角色id，查询使用者(授权用户)的数量
     * @param roleId 角色id
     * @return 授权用户的数量
     * @throws Exception
     */
    int findRoleAndAccreditInfoByRoleId(Long roleId) throws Exception;

}
