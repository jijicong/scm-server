package org.trc.biz.impower;

import org.trc.domain.impower.Role;
import org.trc.form.impower.RoleForm;
import org.trc.util.Pagenation;

/**
 * Created by sone on 2017/5/11.
 */
public interface IRoleBiz {
    /**
     * 根据角色的id查询，角色信息和权限信息
     * @param id  角色id
     * @return
     * @throws Exception
     */
    Role findRoleById(Long roleId) throws Exception;
    /**
     * 更新角色的状态
     * @param role  角色对象
     * @throws Exception
     */
    void updateRoleState(Role role) throws Exception;
    /**
     * 根据角色id，查询使用者(授权用户)的数量
     * @param roleId 角色id
     * @return 授权用户的数量
     * @throws Exception
     */
    int findNumFromRoleAndAccreditInfoByRoleId(Long roleId) throws Exception;
    /**
     * 角色分页查询
     * @param form 查询的条件
     * @param page 页面信息
     * @return
     */
    Pagenation<Role> rolePage(RoleForm form,Pagenation<Role> page) throws Exception;
    /**
     * 根据角色名查询
     * @param name 角色名称
     * @return
     */
    Role findRoleByName(String name) throws Exception;
    /**
     * 修改角色以及修改角色与权限的对饮关系
     * @param role
     * @param roleJurisdiction
     */
    void updateRole(Role role,String roleJurisdiction) throws Exception;
    /**
     * 角色保存和保存角色与权限的对应关系
     * @param role 角色信息
     * @return
     */
    void saveRole(Role role,String roleJurisdiction) throws Exception;

}
