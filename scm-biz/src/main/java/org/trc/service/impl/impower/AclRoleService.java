package org.trc.service.impl.impower;

import org.springframework.stereotype.Service;
import org.trc.domain.impower.AclRole;
import org.trc.mapper.impower.AclRoleMapper;
import org.trc.service.impl.BaseService;
import org.trc.service.impower.IAclRoleService;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by sone on 2017/5/11.
 */
@Service("roleService")
public class AclRoleService extends BaseService<AclRole,Long> implements IAclRoleService {

    @Resource
    private AclRoleMapper aclRoleMapper;

    @Override
    public int findNumFromRoleAndAccreditInfoByRoleId(Long roleId){
        return aclRoleMapper.findNumFromRoleAndAccreditInfoByRoleId(roleId);
    }

    @Override
    public List<AclRole> findRoleList(List<Long> roleIds){
        return aclRoleMapper.findRoleList(roleIds);
    }

}
