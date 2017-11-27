package org.trc.resource;

import com.sun.org.apache.regexp.internal.RE;
import org.springframework.stereotype.Component;
import org.trc.biz.purchase.IPurchaseOrderBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.purchase.PurchaseDetail;
import org.trc.domain.purchase.PurchaseOrder;
import org.trc.domain.purchase.PurchaseOrderAddData;
import org.trc.domain.supplier.Supplier;
import org.trc.domain.supplier.SupplierBrandExt;
import org.trc.enums.PurchaseOrderStatusEnum;
import org.trc.form.purchase.ItemForm;
import org.trc.form.purchase.PurchaseOrderForm;
import org.trc.util.AppResult;
import org.trc.util.AssertUtil;
import org.trc.util.Pagenation;
import org.trc.util.ResultUtil;

import javax.annotation.Resource;
import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by sone on 2017/5/25.
 */
@Component
@Path(SupplyConstants.PurchaseOrder.ROOT)
public class PurchaseOrderResource {

    @Resource
    private IPurchaseOrderBiz purchaseOrderBiz;

    @GET
    @Path(SupplyConstants.PurchaseOrder.PURCHASE_ORDER_PAGE)
    @Produces(MediaType.APPLICATION_JSON)
    public Response purchaseOrderPagenation(@BeanParam PurchaseOrderForm form, @BeanParam Pagenation<PurchaseOrder> page,@Context ContainerRequestContext requestContext){

        Object obj = requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO);
        AssertUtil.notNull(obj,"查询订单分页中,获得授权信息失败");
        AclUserAccreditInfo aclUserAccreditInfo=(AclUserAccreditInfo)obj;
        String  channelCode = aclUserAccreditInfo.getChannelCode(); //获得渠道的编码
        //采购订单分页查询列表
        return  ResultUtil.createSuccessPageResult(purchaseOrderBiz.purchaseOrderPage(form , page,channelCode));

    }

    @POST
    @Path(SupplyConstants.PurchaseOrder.PURCHASE_ORDER)
    @Produces(MediaType.APPLICATION_JSON)
    public Response savePurchaseOrder(@BeanParam PurchaseOrderAddData purchaseOrder, @Context ContainerRequestContext requestContext) {

        purchaseOrderBiz.savePurchaseOrder(purchaseOrder, PurchaseOrderStatusEnum.HOLD.getCode(),(AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO));
        return ResultUtil.createSuccessResult("保存采购订单成功","");

    }
    @POST
    @Path(SupplyConstants.PurchaseOrder.PURCHASE_ORDER_AUDIT)
    @Produces(MediaType.APPLICATION_JSON)//因为aop只拦截了save***开始的方法，注入创建人，因此这里的提交审核，也为save开始
    public Response saveCommitAuditPurchaseOrder(@BeanParam PurchaseOrderAddData purchaseOrder,@Context ContainerRequestContext requestContext) {
        purchaseOrderBiz.savePurchaseOrder(purchaseOrder,PurchaseOrderStatusEnum.AUDIT.getCode(),(AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO));
        return ResultUtil.createSuccessResult("提交审核采购单成功","");
    }
    @GET
    @Path(SupplyConstants.PurchaseOrder.SUPPLIERS)
    @Produces(MediaType.APPLICATION_JSON)
    public Response findSuppliers(@Context ContainerRequestContext requestContext)  {

        String userId = (String)requestContext.getProperty(SupplyConstants.Authorization.USER_ID);

        return ResultUtil.createSuccessResult("根据用户id查询对应的供应商",purchaseOrderBiz.findSuppliersByUserId(userId));

    }

    @GET
    @Path(SupplyConstants.PurchaseOrder.SUPPLIERS_ITEMS+"/{supplierCode}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findPurchaseDetailBysupplierCode(@PathParam("supplierCode") String supplierCode, @BeanParam ItemForm form, @BeanParam Pagenation<PurchaseDetail> page,@QueryParam("skus") String skus) {

        return  ResultUtil.createSuccessPageResult(purchaseOrderBiz.findPurchaseDetailBySupplierCode(supplierCode,form,page,skus));

    }
    @GET
    @Path(SupplyConstants.PurchaseOrder.SUPPLIER_BRAND+"/{supplierCode}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findSupplierBrand(@PathParam("supplierCode") String supplierCode) throws Exception {

        return   ResultUtil.createSuccessResult("根据供应商编码,查询该供应商对应的品牌成功!",purchaseOrderBiz.findSupplierBrand(supplierCode));

    }


    @PUT
    @Path(SupplyConstants.PurchaseOrder.PURCHASE_ORDER+"/{id}")// 保存修改
    @Produces(MediaType.APPLICATION_JSON)
    public Response updatePurchaseOrder(@BeanParam PurchaseOrderAddData purchaseOrderAddData, @Context ContainerRequestContext requestContext) {

        purchaseOrderBiz.updatePurchaseOrder(purchaseOrderAddData,(AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO));
        return  ResultUtil.createSuccessResult("修改采购订单信息成功","");

    }
    @PUT
    @Path(SupplyConstants.PurchaseOrder.PURCHASE_ORDER_AUDIT+"/{id}")//提交审核修改
    @Produces(MediaType.APPLICATION_JSON)
    public Response updatePurchaseOrderAudit(@BeanParam PurchaseOrderAddData purchaseOrderAddData,@Context ContainerRequestContext requestContext) {

        purchaseOrderAddData.setStatus(PurchaseOrderStatusEnum.AUDIT.getCode());
        purchaseOrderBiz.updatePurchaseOrder(purchaseOrderAddData,(AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO));
        return  ResultUtil.createSuccessResult("提交审核修改采购订单信息成功","");

    }
    @PUT
    @Path(SupplyConstants.PurchaseOrder.WAREHOUSE_UPDATE+"/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateWarahouseAdviceUpdate(@BeanParam PurchaseOrder purchaseOrder, @Context ContainerRequestContext requestContext) {

        purchaseOrderBiz.cancelWarahouseAdvice(purchaseOrder,(AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO));
        return ResultUtil.createSuccessResult("入库通知作废成功！","");

    }

    @GET
    @Path(SupplyConstants.PurchaseOrder.SUPPLIERS_ALL_ITEMS+"/{supplierCode}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findAllPurchaseDetailBysupplierCode(@PathParam("supplierCode") String supplierCode) {
        return ResultUtil.createSuccessResult("根据供应商编码查询所有的有效商品成功",purchaseOrderBiz.findAllPurchaseDetailBysupplierCode(supplierCode));
    }


    @GET
    @Path(SupplyConstants.PurchaseOrder.PURCHASE_ORDER+"/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findPurchaseOrderAddDataById(@PathParam("id") Long id) {

        return ResultUtil.createSuccessResult("根据采购单Id查询采购单信息成功",purchaseOrderBiz.findPurchaseOrderAddDataById(id));

    }
    @GET
    @Path(SupplyConstants.PurchaseOrder.PURCHASE_ORDER_BY_CODE+"/{purchaseOrderCode}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findPurchaseOrderAddDataById(@PathParam("purchaseOrderCode") String purchaseOrderCode) {

        return ResultUtil.createSuccessResult("根据采购单编码查询采购单信息成功",purchaseOrderBiz.findPurchaseOrderAddDataByCode(purchaseOrderCode));

    }

    @PUT
    @Path(SupplyConstants.PurchaseOrder.UPDATE_STATE+"/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updatePurchaseState(@BeanParam PurchaseOrder purchaseOrder, @Context ContainerRequestContext requestContext) {

       String msg = purchaseOrderBiz.updatePurchaseOrderState(purchaseOrder,(AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO));
        return ResultUtil.createSuccessResult(msg,"");

    }

    @PUT
    @Path(SupplyConstants.PurchaseOrder.FREEZE+"/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updatePurchaseStateFreeze(@BeanParam PurchaseOrder purchaseOrder, @Context ContainerRequestContext requestContext) {

        purchaseOrderBiz.updatePurchaseStateFreeze(purchaseOrder,(AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO));
        return ResultUtil.createSuccessResult("采购单冻结成功!","");

    }

    @PUT
    @Path(SupplyConstants.PurchaseOrder.WAREHOUSE_ADVICE+"/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response saveWarahouseAdvice(@BeanParam PurchaseOrder purchaseOrder,@Context ContainerRequestContext requestContext) {
        purchaseOrderBiz.warahouseAdvice(purchaseOrder,(AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO));
        return ResultUtil.createSuccessResult("入库通知单添加成功!","");
    }

    @GET
    @Path(SupplyConstants.PurchaseOrder.WAREHOUSE)
    @Produces(MediaType.APPLICATION_JSON)
    public Response findWarehouses(@Context ContainerRequestContext requestContext)  {
        AclUserAccreditInfo aclUserAccreditInfo = (AclUserAccreditInfo)requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO);
        String channelCode = aclUserAccreditInfo.getChannelCode();
        return purchaseOrderBiz.findWarehousesByChannelCode(channelCode);
    }

    @GET
    @Path(SupplyConstants.PurchaseOrder.PURCHASE_ORDER_ITEM)
    @Produces(MediaType.APPLICATION_JSON)
    public Response findAllPurchaseDetail(@BeanParam PurchaseOrder purchaseOrder, @BeanParam ItemForm form, @BeanParam Pagenation<PurchaseDetail> page,@QueryParam("skus") String skus) {
        return ResultUtil.createSuccessPageResult(purchaseOrderBiz.findPurchaseDetail(purchaseOrder,form,page,skus));
    }

}
