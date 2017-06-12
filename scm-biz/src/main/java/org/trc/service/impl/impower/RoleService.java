package org.trc.service.impl.impower;

import org.springframework.stereotype.Service;
import org.trc.domain.impower.Role;
import org.trc.mapper.impower.RoleMapper;
import org.trc.mapper.impower.UserAccreditRoleRelationMapper;
import org.trc.service.impl.BaseService;
import org.trc.service.impower.IRoleService;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by sone on 2017/5/11.
 */
@Service("roleService")
public class RoleService extends BaseService<Role ,Long> implements IRoleService{

    @Resource
    private RoleMapper roleMapper;

    @Override
    public int findNumFromRoleAndAccreditInfoByRoleId(Long roleId) throws Exception{
        return roleMapper.findNumFromRoleAndAccreditInfoByRoleId(roleId);
    }

    @Override
    public List<Role> findRoleList(List<Long> roleIds) throws Exception {
        return roleMapper.findRoleList(roleIds);
    }

}
