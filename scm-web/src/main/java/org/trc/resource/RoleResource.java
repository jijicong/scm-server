package org.trc.resource;

import org.springframework.stereotype.Component;
import org.trc.biz.impower.IRoleBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.impower.Role;
import org.trc.domain.impower.RoleAddPageData;
import org.trc.form.impower.RoleForm;
import org.trc.util.AppResult;
import org.trc.util.Pagenation;
import org.trc.util.ResultUtil;

import javax.annotation.Resource;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by sone on 2017/5/11.
 */
@Component
@Path(SupplyConstants.Role.ROOT)
public class RoleResource {

    @Resource
    private IRoleBiz roleBiz;

    //角色分页查询
    @GET
    @Path(SupplyConstants.Role.ROLE_PAGE)
    @Produces(MediaType.APPLICATION_JSON)
    public Pagenation<Role> rolePage(@BeanParam RoleForm form, @BeanParam Pagenation<Role> page) throws Exception{
        return roleBiz.rolePage(form,page);
    }
    //修改角色信息以及与之对应的角色权限关联表信息的修改
    @PUT
    @Path(SupplyConstants.Role.ROLE+"/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult updateRole(@BeanParam RoleAddPageData roleAddPageData) throws Exception{

        Role role= roleAddPageData;
        roleBiz.updateRole(role, roleAddPageData.getRoleJurisdiction());
        return  ResultUtil.createSucssAppResult("修改角色信息成功","");

    }
    //保存角色信息以及与之对应的角色权限关联表信息的保存
    @POST
    @Path(SupplyConstants.Role.ROLE)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult saveRole(@BeanParam RoleAddPageData roleAddPageData) throws Exception{

        Role role= roleAddPageData;
        roleBiz.saveRole(role, roleAddPageData.getRoleJurisdiction());
        return  ResultUtil.createSucssAppResult("保存成功","");

    }
    //根据角色名查询角色
    @GET
    @Path(SupplyConstants.Role.ROLE)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult findRoleByName(@QueryParam("name") String name ) throws Exception{
        //  前台接受为null则数据没问题 ，有数据则名称不能使用，"1" 为标志存在数据
        return  ResultUtil.createSucssAppResult("查询角色成功", roleBiz.findRoleByName(name)==null ? null :"1");
    }
    //根据角色的id 查询使用该角色的用户数量，以及启用状态
    @GET
    @Path(SupplyConstants.Role.ROLE_ACCREDITINFO)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult findNumFromRoleAndAccreditInfoByRoleId(@QueryParam("roleId") Long roleId) throws Exception{

        return  ResultUtil.createSucssAppResult("查询角色数量成功",roleBiz.findNumFromRoleAndAccreditInfoByRoleId(roleId));

    }
    //修改角色的状态
    @POST
    @Path(SupplyConstants.Role.UPDATE_STATE+"/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult  updateRoleState(@BeanParam Role role) throws Exception{
        roleBiz.updateRoleState(role);
        return ResultUtil.createSucssAppResult("修改角色状态成功","");
    }

    @GET
    @Path(SupplyConstants.Role.ROLE+"/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult<Role> findRoleById(@PathParam("id") Long id) throws Exception{
        return ResultUtil.createSucssAppResult("查询角色成功",roleBiz.findRoleById(id));
    }

}
