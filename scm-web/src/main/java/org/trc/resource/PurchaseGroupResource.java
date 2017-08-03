package org.trc.resource;

import org.springframework.stereotype.Component;
import org.trc.biz.purchase.IPurchaseGroupBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.purchase.PurchaseGroup;
import org.trc.form.purchase.PurchaseGroupForm;
import org.trc.util.AppResult;
import org.trc.util.Pagenation;
import org.trc.util.ResultUtil;

import javax.annotation.Resource;
import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
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
    public Pagenation<PurchaseGroup> purchaseGroupPage(@BeanParam PurchaseGroupForm form, @BeanParam Pagenation<PurchaseGroup> page){
        return purchaseGroupBiz.purchaseGroupPage(form , page);
    }

    @PUT
    @Path(SupplyConstants.PurchaseGroup.UPDATE_STATE+"/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult updatePurchaseStatus(@BeanParam PurchaseGroup purchaseGroup,@Context ContainerRequestContext requestContext){
        purchaseGroupBiz.updatePurchaseStatus(purchaseGroup,(AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO));
        return ResultUtil.createSucssAppResult("修改采购组状态成功","");
    }
    //TODO
    @GET
    @Path(SupplyConstants.PurchaseGroup.PURCHASE_GROUP_CODE_USER)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult<List<AclUserAccreditInfo>> findPurchaseGroupPersons(@QueryParam("purchaseGroupCode") String purchaseGroupCode){
        return ResultUtil.createSucssAppResult("查询采购组人员成功",purchaseGroupBiz.findPurchaseGroupPersons(purchaseGroupCode));
    }


    @GET
    @Path(SupplyConstants.PurchaseGroup.PURCHASE_GROUP_CODE+"/{code}")
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult<PurchaseGroup> findPurcahseByCode(@PathParam("code") String code){
        return ResultUtil.createSucssAppResult("根据编码查询采购组成功",purchaseGroupBiz.findPurchaseGroupByCode(code));
    }

    @GET
    @Path(SupplyConstants.PurchaseGroup.PURCHASE_GROUP)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult findPurchaseByName(@QueryParam("name") String name){
        return ResultUtil.createSucssAppResult("根据name查询采购组信息成功", purchaseGroupBiz.findPurchaseByName(name)==null ? null :"1");
    }

    @GET
    @Path(SupplyConstants.PurchaseGroup.PURCHASE_GROUP_LIST)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult<List<PurchaseGroup>> findPurchaseGroups(){
        return ResultUtil.createSucssAppResult("查询采购组列表",purchaseGroupBiz.findPurchaseGroupList());
    }


    @PUT
    @Path(SupplyConstants.PurchaseGroup.PURCHASE_GROUP+"/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult updatePurchaseGroup(@BeanParam PurchaseGroup purchaseGroup ,@Context ContainerRequestContext requestContext){
        purchaseGroupBiz.updatePurchaseGroup(purchaseGroup,(AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO));
        return ResultUtil.createSucssAppResult("修改采购组成功","");
    }

    @POST
    @Path(SupplyConstants.PurchaseGroup.PURCHASE_GROUP)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult savePurchaseGroup(@BeanParam  PurchaseGroup purchaseGroup,@Context ContainerRequestContext requestContext){
        purchaseGroupBiz.savePurchaseGroup(purchaseGroup,(AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO));
        return  ResultUtil.createSucssAppResult("保存采购组成功","");
    }

    @GET
    @Path(SupplyConstants.PurchaseGroup.PURCHASE_GROUP_USER+"/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult<List<AclUserAccreditInfo>> findPurchaseGroupMemberStateById(@PathParam("id") Long id){
        return ResultUtil.createSucssAppResult("根据采购组id查询无效状态的成员成功",purchaseGroupBiz.findPurchaseGroupMemberStateById(id));
    }

    @GET
    @Path(SupplyConstants.PurchaseGroup.PURCHASE_GROUP+"/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult<PurchaseGroup> findPurchaseGroupById(@PathParam("id") Long id){

        return ResultUtil.createSucssAppResult("根据id查询采购组成功",purchaseGroupBiz.findPurchaseById(id));

    }

}
