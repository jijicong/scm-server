package org.trc.resource;

import com.sun.org.apache.regexp.internal.RE;
import org.springframework.stereotype.Component;
import org.trc.biz.purchase.IPurchaseGroupBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.impower.UserAccreditInfo;
import org.trc.domain.purchase.PurchaseGroup;
import org.trc.form.purchase.PurchaseGroupForm;
import org.trc.util.AppResult;
import org.trc.util.Pagenation;
import org.trc.util.ResultUtil;

import javax.annotation.Resource;
import javax.ws.rs.*;
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
    public Pagenation<PurchaseGroup> purchaseGroupPage(@BeanParam PurchaseGroupForm form, @BeanParam Pagenation<PurchaseGroup> page) throws Exception{
        return purchaseGroupBiz.purchaseGroupPage(form , page);
    }

    @POST
    @Path(SupplyConstants.PurchaseGroup.UPDATE_STATE+"/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult updatePurchaseStatus(@BeanParam PurchaseGroup purchaseGroup) throws Exception {
        purchaseGroupBiz.updatePurchaseStatus(purchaseGroup);
        return ResultUtil.createSucssAppResult("修改采购组状态成功","");
    }

    @GET
    @Path(SupplyConstants.PurchaseGroup.PURCHASE_GROUP_CODE+"/{code}")
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult<PurchaseGroup> findPurcahseByCode(@PathParam("code") String code) throws Exception{
        return ResultUtil.createSucssAppResult("根据编码查询采购组成功",purchaseGroupBiz.findPurchaseGroupByCode(code));
    }

    @GET
    @Path(SupplyConstants.PurchaseGroup.PURCHASE_GROUP)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult findPurchaseByName(@QueryParam("name") String name) throws Exception{
        return ResultUtil.createSucssAppResult("根据name查询采购组信息成功", purchaseGroupBiz.findPurchaseByName(name)==null ? null :"1");
    }

    @PUT
    @Path(SupplyConstants.PurchaseGroup.PURCHASE_GROUP+"/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult updatePurchaseGroup(@BeanParam PurchaseGroup purchaseGroup ) throws Exception{
        purchaseGroupBiz.updatePurchaseGroup(purchaseGroup);
        return ResultUtil.createSucssAppResult("修改采购组成功","");
    }

    @POST
    @Path(SupplyConstants.PurchaseGroup.PURCHASE_GROUP)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult savePurchaseGroup(@BeanParam  PurchaseGroup purchaseGroup) throws Exception{
        purchaseGroupBiz.savePurchaseGroup(purchaseGroup);
        return  ResultUtil.createSucssAppResult("保存采购组成功","");
    }

    @GET
    @Path(SupplyConstants.PurchaseGroup.PURCHASE_GROUP_USER+"/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult<List<UserAccreditInfo>> findPurchaseGroupMemberStateById(@PathParam("id") Long id) throws Exception{
        return ResultUtil.createSucssAppResult("查询当前采购组id对应的无效状态的成员成功",purchaseGroupBiz.findPurchaseGroupMemberStateById(id));
    }
    @GET
    @Path(SupplyConstants.PurchaseGroup.PURCHASE_GROUP+"/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult<PurchaseGroup> findPurchaseGroupById(@PathParam("id") Long id)throws Exception {

        return ResultUtil.createSucssAppResult("根据id查询采购组成功",purchaseGroupBiz.findPurchaseById(id));

    }

}
