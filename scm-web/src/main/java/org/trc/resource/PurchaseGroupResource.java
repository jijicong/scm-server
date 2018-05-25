package org.trc.resource;

import com.sun.org.apache.regexp.internal.RE;
import org.apache.ibatis.annotations.Delete;
import org.springframework.stereotype.Component;
import org.trc.biz.purchase.IPurchaseGroupBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.purchase.PurchaseGroup;
import org.trc.domain.purchase.PurchaseGroupUser;
import org.trc.enums.ValidEnum;
import org.trc.form.purchase.PurchaseGroupForm;
import org.trc.util.AppResult;
import org.trc.util.Pagenation;
import org.trc.util.ResultUtil;

import javax.annotation.Resource;
import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by sone on 2017/5/19.
 */
@Component
@Path(SupplyConstants.PurchaseGroup.ROOT)
public class PurchaseGroupResource {

    @Resource
    private IPurchaseGroupBiz purchaseGroupBiz;

    @GET
    @Path(SupplyConstants.PurchaseGroup.PURCHASE_GROUP_PAGE)
    @Produces(MediaType.APPLICATION_JSON)
    public Response purchaseGroupPage(@BeanParam PurchaseGroupForm form, @BeanParam Pagenation<PurchaseGroup> page, @Context ContainerRequestContext requestContext){
        return ResultUtil.createSuccessPageResult(purchaseGroupBiz.purchaseGroupPage(form , page,(AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO)));
    }

    @PUT
    @Path(SupplyConstants.PurchaseGroup.UPDATE_STATE+"/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updatePurchaseStatus(@BeanParam PurchaseGroup purchaseGroup, @Context ContainerRequestContext requestContext){
        purchaseGroupBiz.updatePurchaseStatus(purchaseGroup,(AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO));
        return ResultUtil.createSuccessResult(ValidEnum.VALID.getCode().equals(purchaseGroup.getIsValid()) ? "停用成功!":"启用成功!","");
    }

    @GET
    @Path(SupplyConstants.PurchaseGroup.PURCHASE_GROUP_CODE_USER)
    @Produces(MediaType.APPLICATION_JSON)
    public Response findPurchaseGroupPersons(@QueryParam("purchaseGroupCode") String purchaseGroupCode){
        return ResultUtil.createSuccessResult("查询采购组人员成功",purchaseGroupBiz.findPurchaseGroupPersons(purchaseGroupCode));
    }

    @GET
    @Path(SupplyConstants.PurchaseGroup.PURCHASE_GROUP_CODE+"/{code}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findPurcahseByCode(@PathParam("code") String code){
        return ResultUtil.createSuccessResult("根据编码查询采购组成功",purchaseGroupBiz.findPurchaseGroupByCode(code));
    }

    @GET
    @Path(SupplyConstants.PurchaseGroup.PURCHASE_GROUP)
    @Produces(MediaType.APPLICATION_JSON)
    public Response findPurchaseByName(@QueryParam("name") String name){
        return ResultUtil.createSuccessResult("根据name查询采购组信息成功", purchaseGroupBiz.findPurchaseByName(name)==null ? null :"1");
    }

    @GET
    @Path(SupplyConstants.PurchaseGroup.PURCHASE_GROUP_LIST)
    @Produces(MediaType.APPLICATION_JSON)
    public Response findPurchaseGroups(@Context ContainerRequestContext requestContext){
        //查询当前渠道下的采购组成功
        return ResultUtil.createSuccessResult("查询采购组列表",purchaseGroupBiz.findPurchaseGroupList((AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO)));
    }


    @PUT
    @Path(SupplyConstants.PurchaseGroup.PURCHASE_GROUP+"/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updatePurchaseGroup(@BeanParam PurchaseGroup purchaseGroup ,@Context ContainerRequestContext requestContext){
        purchaseGroupBiz.updatePurchaseGroup(purchaseGroup,(AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO));
        return ResultUtil.createSuccessResult("修改采购组成功","");
    }

    @POST
    @Path(SupplyConstants.PurchaseGroup.PURCHASE_GROUP)
    @Produces(MediaType.APPLICATION_JSON)
    public Response savePurchaseGroup(@BeanParam  PurchaseGroup purchaseGroup,@Context ContainerRequestContext requestContext){
        purchaseGroupBiz.savePurchaseGroup(purchaseGroup,(AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO));
        return  ResultUtil.createSuccessResult("保存采购组成功","");
    }

    @GET
    @Path(SupplyConstants.PurchaseGroup.PURCHASE_GROUP_USER+"/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findPurchaseGroupMemberStateById(@PathParam("id") Long id){
        return ResultUtil.createSuccessResult("根据采购组id查询无效状态的成员成功",purchaseGroupBiz.findPurchaseGroupMemberStateById(id));
    }

    @GET
    @Path(SupplyConstants.PurchaseGroup.PURCHASE_GROUP+"/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findPurchaseGroupById(@PathParam("id") Long id){

        return ResultUtil.createSuccessResult("根据id查询采购组成功",purchaseGroupBiz.findPurchaseById(id));

    }

    @GET
    @Path(SupplyConstants.PurchaseGroup.PURCHASE_GROUP_USER_NEW)
    public Response findPurchaseCroupUser(@Context ContainerRequestContext requestContext){
        return ResultUtil.createSuccessResult("查询采购组员成功",purchaseGroupBiz.findPurchaseGroupUser((AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO)));
    }

    @PUT
    @Path(SupplyConstants.PurchaseGroup.PURCHASE_GROUP_USER_NEW)
    @Produces(MediaType.APPLICATION_JSON)
    public Response savePurchaseGroupUser(@BeanParam PurchaseGroupUser purchaseGroupUser,@Context ContainerRequestContext requestContext){
        purchaseGroupBiz.savePurchaseCroupUser(purchaseGroupUser, (AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO));
        return  ResultUtil.createSuccessResult("保存采购组员成功","");
    }
}
