package org.trc.mapper.impower;

import org.trc.domain.impower.AclRole;
import org.trc.util.BaseMapper;

import java.util.List;

/**
 * Created by sone on 2017/5/11.
 */
public interface AclRoleMapper extends BaseMapper<AclRole>{

    int findNumFromRoleAndAccreditInfoByRoleId(Long roleId);

    List<AclRole> findRoleList(List<Long> roleIds);
}
