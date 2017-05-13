package org.trc.resource;

import org.springframework.stereotype.Component;
import org.trc.biz.impower.IRoleBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.impower.Role;
import org.trc.domain.impower.RoleAdd;
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

    //仓库分页查询
    @GET
    @Path(SupplyConstants.Role.ROLE_PAGE)
    @Produces(MediaType.APPLICATION_JSON)
    public Pagenation<Role> rolePage(@BeanParam RoleForm form, @BeanParam Pagenation<Role> page) throws Exception{
        return roleBiz.rolePage(form,page);
    }
    @POST
    @Path(SupplyConstants.Role.ROLE)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult saveRole(@BeanParam RoleAdd roleAdd) throws Exception{
        Role role=new Role();
        role.setName(roleAdd.getName());
        role.setRemark(roleAdd.getRemark());
        role.setRoleType(roleAdd.getRoleType());
        role.setIsValid(roleAdd.getIsValid());
        return  ResultUtil.createSucssAppResult("保存成功",roleBiz.saveRole(role,roleAdd.getRoleJurisdiction()));
    }

    //根据角色名查询角色
    @GET
    @Path(SupplyConstants.Role.ROLE)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult findRoleByName(@QueryParam("name") String name ) throws Exception{
        //  前台接受为null则数据没问题 ，有数据则名称不能使用，"1" 为标志存在数据
        return  ResultUtil.createSucssAppResult("查询角色成功", roleBiz.findRoleByName(name)==null ? null :"1");
    }

}
