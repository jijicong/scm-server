package org.trc.resource;

import org.springframework.stereotype.Component;
import org.trc.biz.impower.IAclRoleBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.impower.AclRole;
import org.trc.domain.impower.AclRoleAddPageData;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.enums.ValidEnum;
import org.trc.form.impower.RoleForm;
import org.trc.util.AppResult;
import org.trc.util.Pagenation;
import org.trc.util.ResultUtil;

import javax.annotation.Resource;
import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by sone on 2017/5/11.
 */
@Component
@Path(SupplyConstants.Role.ROOT)
public class AclRoleResource {

    @Resource
    private IAclRoleBiz roleBiz;

    //角色分页查询
    @GET
    @Path(SupplyConstants.Role.ROLE_PAGE)
    @Produces(MediaType.APPLICATION_JSON)
    public Response rolePage(@BeanParam RoleForm form, @BeanParam Pagenation<AclRole> page, @Context ContainerRequestContext requestContext){
        return ResultUtil.createSuccessPageResult(roleBiz.rolePage(form,page,(AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO)));
    }
    //修改角色信息以及与之对应的角色权限关联表信息的修改
    @PUT
    @Path(SupplyConstants.Role.ROLE+"/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateRole(@BeanParam AclRoleAddPageData roleAddPageData, @Context ContainerRequestContext requestContext){

        AclRole aclRole = roleAddPageData;
        roleBiz.updateRole(aclRole, roleAddPageData.getRoleJurisdiction(),(AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO));
        return  ResultUtil.createSuccessResult("修改角色信息成功","");

    }
    //保存角色信息以及与之对应的角色权限关联表信息的保存
    @POST
    @Path(SupplyConstants.Role.ROLE)
    @Produces(MediaType.APPLICATION_JSON)
    public Response saveRole(@BeanParam AclRoleAddPageData roleAddPageData, @Context ContainerRequestContext requestContext){

        AclRole aclRole = roleAddPageData;
        roleBiz.saveRole(aclRole, roleAddPageData.getRoleJurisdiction(),(AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO));
        return  ResultUtil.createSuccessResult("保存角色资源成功","");

    }
    //根据角色名查询角色
    @GET
    @Path(SupplyConstants.Role.ROLE)
    @Produces(MediaType.APPLICATION_JSON)
    public Response findRoleByName(@QueryParam("name") String name ){
        //  前台接受为null则数据没问题 ，有数据则名称不能使用，"1" 为标志存在数据
        return  ResultUtil.createSuccessResult("查询角色成功", roleBiz.findRoleByName(name)==null ? null :"1");
    }
    //根据角色的id 查询使用该角色的用户数量，以及启用状态
    @GET
    @Path(SupplyConstants.Role.ROLE_ACCREDITINFO)
    @Produces(MediaType.APPLICATION_JSON)
    public Response findNumFromRoleAndAccreditInfoByRoleId(@QueryParam("roleId") Long roleId){

        return  ResultUtil.createSuccessResult("查询角色数量成功",roleBiz.findNumFromRoleAndAccreditInfoByRoleId(roleId));

    }
    //修改角色的状态
    @PUT
    @Path(SupplyConstants.Role.UPDATE_STATE+"/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response  updateRoleState(@BeanParam AclRole aclRole, @Context ContainerRequestContext requestContext){
        roleBiz.updateRoleState(aclRole,(AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO));
        return ResultUtil.createSuccessResult(ValidEnum.VALID.getCode().equals(aclRole.getIsValid()) ? "停用成功!":"启用成功!","");
    }

    @GET
    @Path(SupplyConstants.Role.ROLE+"/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findRoleById(@PathParam("id") Long id){
        return ResultUtil.createSuccessResult("查询角色成功",roleBiz.findRoleById(id));
    }

}
