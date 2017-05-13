package org.trc.service.impl.impower;

import org.springframework.stereotype.Service;
import org.trc.domain.impower.Role;
import org.trc.service.impl.BaseService;
import org.trc.service.impower.IRoleService;

/**
 * Created by sone on 2017/5/11.
 */
@Service("roleService")
public class RoleService extends BaseService<Role ,Long> implements IRoleService{
}
