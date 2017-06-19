package org.trc.service.impower;

import org.trc.domain.impower.AclRole;
import org.trc.service.IBaseService;

import java.util.List;

/**
 * Created by sone on 2017/5/11.
 */
public interface IAclRoleService extends IBaseService<AclRole,Long>{
    /**
     * 根据角色id，查询使用者(授权用户)的数量
     * @param roleId 角色id
     * @return 授权用户的数量
     * @throws Exception
     */
    int findNumFromRoleAndAccreditInfoByRoleId(Long roleId) throws Exception;

    List<AclRole> findRoleList(List<Long> roleIds) throws Exception;
}

