package org.trc.mapper.impower;

import org.trc.domain.impower.Role;
import org.trc.util.BaseMapper;

import java.util.List;

/**
 * Created by sone on 2017/5/11.
 */
public interface RoleMapper extends BaseMapper<Role>{

    int findNumFromRoleAndAccreditInfoByRoleId(Long roleId) throws Exception;

    List<Role> findRoleList(List<Long> roleIds) throws Exception;
}
