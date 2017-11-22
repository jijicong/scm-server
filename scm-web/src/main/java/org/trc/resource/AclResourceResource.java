package org.trc.resource;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.trc.biz.impower.IAclResourceBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.form.impower.JurisdictionTreeNode;
import org.trc.util.ResultUtil;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * 资源控制
 * Created by sone on 2017/5/11.
 */
@Component
@Path(SupplyConstants.Jurisdiction.ROOT)
public class AclResourceResource {
    @Autowired
    private IAclResourceBiz jurisdictionBiz;

    /**
     * 提供两种角色下对应的角色权限
     * 1.提供全局角色对应的权限资源
     * 2.提供渠道角色对应的权限资源
     */
    @GET
    @Path(SupplyConstants.Jurisdiction.JURISDICTION_WHOLE)
    @Produces(MediaType.APPLICATION_JSON)
    public Response findWholeJurisdiction(){

        return ResultUtil.createSuccessResult("查询全局角色成功", jurisdictionBiz.findWholeJurisdiction());

    }

    @GET
    @Path(SupplyConstants.Jurisdiction.JURISDICTION_WHOLE_MODULE)
    @Produces(MediaType.APPLICATION_JSON)
    public Response findWholeJurisdictionModule(@Context ContainerRequestContext requestContext){
        AclUserAccreditInfo userAccreditInfo = (AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO);
        return ResultUtil.createSuccessResult("查询全局角色成功", jurisdictionBiz.findWholeJurisdictionModule(userAccreditInfo));
    }

    @GET
    @Path(SupplyConstants.Jurisdiction.JURISDICTION_CHANNEL)
    @Produces(MediaType.APPLICATION_JSON)
    public Response findChannelJurisdiction(){
        return ResultUtil.createSuccessResult("查询渠道角色成功", jurisdictionBiz.findChannelJurisdiction());
    }

    @GET
    @Path(SupplyConstants.Jurisdiction.JURISDICTION_CHANNEL_MODULE)
    @Produces(MediaType.APPLICATION_JSON)
    public Response findChannelJurisdictionModule(){
        return ResultUtil.createSuccessResult("查询渠道角色成功", jurisdictionBiz.findChannelJurisdictionModule());
    }

    /**
     * 提供两种角色下对应的角色权限，用于回写被选中的权限
     * 1.提供带有角色id的，角色与权限的关联信息查询<全局角色>
     * 2.提供带有角色id的，角色与权限的关联信息查询<渠道角色>
     */
    @GET
    @Path(SupplyConstants.Jurisdiction.JURISDICTION_WHOLE + "/{roleId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findWholeJurisdictionAndCheckedByRoleId(@PathParam("roleId") Long roleId){

        return ResultUtil.createSuccessResult("查询全局角色成功", jurisdictionBiz.findWholeJurisdictionAndCheckedByRoleId(roleId));

    }

    @GET
    @Path(SupplyConstants.Jurisdiction.JURISDICTION_CHANNEL + "/{roleId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response  findChannelJurisdictionAndCheckedByRoleId(@PathParam("roleId") Long roleId){

        return ResultUtil.createSuccessResult("查询全局角色成功", jurisdictionBiz.findChannelJurisdictionAndCheckedByRoleId(roleId));

    }

    @GET
    @Path(SupplyConstants.Jurisdiction.JURISDICTION_WHOLE_MODULE + "/{roleId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findWholeJurisdictionAndCheckedModuleByRoleId(@PathParam("roleId") Long roleId,@Context ContainerRequestContext requestContext){
        AclUserAccreditInfo userAccreditInfo = (AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO);
        return ResultUtil.createSuccessResult("查询全局角色成功", jurisdictionBiz.findWholeJurisdictionAndCheckedModuleByRoleId(roleId,userAccreditInfo));

    }

    @GET
    @Path(SupplyConstants.Jurisdiction.JURISDICTION_CHANNEL_MODULE + "/{roleId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findChannelJurisdictionAndCheckedModuleByRoleId(@PathParam("roleId") Long roleId,@Context ContainerRequestContext requestContext){
        return ResultUtil.createSuccessResult("查询全局角色成功", jurisdictionBiz.findChannelJurisdictionAndCheckedModuleByRoleId(roleId));

    }

    @GET
    @Path(SupplyConstants.Jurisdiction.JURISDICTION_TREE)
    @Produces(MediaType.APPLICATION_JSON)
    public Response jurisdictionTree(@QueryParam("parentId") Long parentId, @QueryParam("isRecursive") boolean isRecursive){
        return ResultUtil.createSuccessResult("查询权限资源成功", jurisdictionBiz.getNodes(parentId, isRecursive));
    }

    @POST
    @Path(SupplyConstants.Jurisdiction.JURISDICTION_SAVE)
    @Produces(MediaType.APPLICATION_JSON)
    public Response saveJurisdiction(@BeanParam JurisdictionTreeNode jurisdictionTreeNode,@Context ContainerRequestContext requestContext) {
        jurisdictionBiz.saveJurisdiction(jurisdictionTreeNode,(AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO));
        return ResultUtil.createSuccessResult("新增权限资源成功", "");
    }

    @PUT
    @Path(SupplyConstants.Jurisdiction.JURISDICTION_EDIT+"/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateJurisdiction(@BeanParam JurisdictionTreeNode jurisdictionTreeNode){
        jurisdictionBiz.updateJurisdiction(jurisdictionTreeNode);
        return ResultUtil.createSuccessResult("更新权限资源成功", "");
    }


    @GET
    @Path(SupplyConstants.Jurisdiction.JURISDICTION_HTML)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateJurisdiction(@Context ContainerRequestContext requestContext, HttpServletRequest request){
        System.out.println(request.getSession());
        System.out.println(request.getSession().getAttribute("channelCode"));
        String userId= (String) requestContext.getProperty(SupplyConstants.Authorization.USER_ID);
        return ResultUtil.createSuccessResult("查询用户html页面权限成功", jurisdictionBiz.getHtmlJurisdiction(userId));
    }


}
