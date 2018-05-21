package org.trc.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.trc.biz.impower.IAclWmsUserAccreditInfoBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.impower.AclWmsUserAccreditInfo;
import org.trc.form.impower.WmsUserAccreditInfoForm;
import org.trc.util.Pagenation;
import org.trc.util.ResultUtil;

import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@Component
@Path(SupplyConstants.AclWmsUser.ROOT)
public class WmsResourceResource {
    @Autowired
    private IAclWmsUserAccreditInfoBiz aclWmsUserAccreditInfoBiz;

    @GET
    @Path(SupplyConstants.AclWmsUser.ACL_WMS_USER_PAGE)
    @Produces(MediaType.APPLICATION_JSON)
    public Response wmsUserAccreditInfoPage(@BeanParam WmsUserAccreditInfoForm form, @BeanParam Pagenation<AclWmsUserAccreditInfo> page){
        return ResultUtil.createSuccessPageResult(aclWmsUserAccreditInfoBiz.wmsUserAccreditInfoPage(form,page));
    }

    @POST
    @Path(SupplyConstants.AclWmsUser.ACL_WMS_USER_SAVE)
    @Produces(MediaType.APPLICATION_JSON)
    public Response saveAclWmsUserAccreditInfo(@BeanParam AclWmsUserAccreditInfo aclWmsUserAccreditInfo,@Context ContainerRequestContext requestContext){
        aclWmsUserAccreditInfoBiz.saveAclWmsUserAccreditInfo(aclWmsUserAccreditInfo,(AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO));
        return ResultUtil.createSuccessResult("新增WMS用户授权成功!","");
    }

    @GET
    @Path(SupplyConstants.AclWmsUser.ACL_WMS_USER_WAREHOUSE)
    @Produces(MediaType.APPLICATION_JSON)
    public Response queryWarehouseInfo(@QueryParam("id") Long id) {
        return ResultUtil.createSuccessResult("查询自营仓库成功!", aclWmsUserAccreditInfoBiz.queryWarehouseInfo(id));
    }

    @GET
    @Path(SupplyConstants.AclWmsUser.ACL_WMS_USER_RESOURCE)
    @Produces(MediaType.APPLICATION_JSON)
    public Response queryResource(@QueryParam("id") Long id) {
        return ResultUtil.createSuccessResult("查询WMS资源成功!", aclWmsUserAccreditInfoBiz.queryResource(id));
    }

    @GET
    @Path(SupplyConstants.AclWmsUser.ACL_WMS_USER_QUERY+"/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response queryAclWmsUserAccreditInfo(@PathParam("id") Long id) {
        return ResultUtil.createSuccessResult("查询WMS用户成功!", aclWmsUserAccreditInfoBiz.queryAclWmsUserAccreditInfo(id));
    }

    @PUT
    @Path(SupplyConstants.AclWmsUser.ACL_WMS_USER_UPDATE+"/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateAclWmsUserAccreditInfo(@BeanParam AclWmsUserAccreditInfo aclWmsUserAccreditInfo,@Context ContainerRequestContext requestContext) {
        AclUserAccreditInfo aclUserAccreditInfo=  (AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO);
        aclWmsUserAccreditInfoBiz.updateAclWmsUserAccreditInfo(aclWmsUserAccreditInfo,aclUserAccreditInfo);
        return ResultUtil.createSuccessResult("修改WMS用户成功!","");
    }

    @PUT
    @Path(SupplyConstants.AclWmsUser.ACL_WMS_USER_UPDATE_STATE+"/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateAclWmsUserAccreditInfoState(@BeanParam AclWmsUserAccreditInfo aclWmsUserAccreditInfo,@Context ContainerRequestContext requestContext) {
        AclUserAccreditInfo aclUserAccreditInfo=  (AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO);
        aclWmsUserAccreditInfoBiz.updateAclWmsUserAccreditInfoState(aclWmsUserAccreditInfo,aclUserAccreditInfo);
        return ResultUtil.createSuccessResult("修改WMS用户状态成功!","");
    }


    @POST
    @Path(SupplyConstants.AclWmsUser.ACL_WMS_USER_PHONE)
    @Produces(MediaType.APPLICATION_JSON)
    public Response checkWmsPhone(@FormParam("phone")String checkWmsPhone) {
        try {
            aclWmsUserAccreditInfoBiz.checkWmsPhone(checkWmsPhone);
        }catch (Exception e){
            return ResultUtil.createSuccessResult("手机号码校验成功!",e.getMessage());
        }
        return ResultUtil.createSuccessResult("手机号码校验成功!",1);
    }

}
