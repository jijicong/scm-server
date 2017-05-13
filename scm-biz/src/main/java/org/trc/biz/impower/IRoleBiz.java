package org.trc.biz.impower;

import org.trc.domain.impower.Role;
import org.trc.form.impower.RoleForm;
import org.trc.util.Pagenation;

/**
 * Created by sone on 2017/5/11.
 */
public interface IRoleBiz {
    /**
     * 角色分页查询
     * @param form 查询的条件
     * @param page 页面信息
     * @return
     */
    Pagenation<Role> rolePage(RoleForm form,Pagenation<Role> page);
    /**
     * 根据角色名查询
     * @param name 角色名称
     * @return
     */
    Role findRoleByName(String name) throws Exception;
    /**
     * 角色保存
     * @param role 角色信息
     * @return
     */
    int saveRole(Role role,String roleJurisdiction) throws Exception;

}
