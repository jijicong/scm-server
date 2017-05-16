package org.trc.service.impl.impower;

import org.springframework.stereotype.Service;
import org.trc.domain.impower.Role;
import org.trc.mapper.impower.RoleMapper;
import org.trc.mapper.impower.UserAccreditRoleRelationMapper;
import org.trc.service.impl.BaseService;
import org.trc.service.impower.IRoleService;

import javax.annotation.Resource;

/**
 * Created by sone on 2017/5/11.
 */
@Service("roleService")
public class RoleService extends BaseService<Role ,Long> implements IRoleService{

    @Resource
    private RoleMapper roleMapper;

    @Override
    public int findNumFromRoleAndAccreditInfoByRoleId(Long roleId) {
        return roleMapper.findNumFromRoleAndAccreditInfoByRoleId(roleId);
    }

}
