package org.trc.resource;

import org.springframework.stereotype.Component;
import org.trc.biz.purchase.IPurchaseOrderBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.purchase.PurchaseDetail;
import org.trc.domain.purchase.PurchaseOrder;
import org.trc.domain.purchase.PurchaseOrderAddData;
import org.trc.domain.supplier.Supplier;
import org.trc.enums.PurchaseOrderStatusEnum;
import org.trc.form.purchase.ItemForm;
import org.trc.form.purchase.PurchaseOrderForm;
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
    public Pagenation<PurchaseOrder> purchaseOrderPagenation(@BeanParam PurchaseOrderForm form, @BeanParam Pagenation<PurchaseOrder> page,@Context ContainerRequestContext requestContext){

        //采购订单分页查询列表
        return  purchaseOrderBiz.purchaseOrderPage(form , page,requestContext);

    }

    @POST
    @Path(SupplyConstants.PurchaseOrder.PURCHASE_ORDER)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult savePurchaseOrder(@BeanParam PurchaseOrderAddData purchaseOrder, @Context ContainerRequestContext requestContext) {

        purchaseOrderBiz.savePurchaseOrder(purchaseOrder, PurchaseOrderStatusEnum.HOLD.getCode());
        return ResultUtil.createSucssAppResult("保存采购订单成功","");

    }
    @POST
    @Path(SupplyConstants.PurchaseOrder.PURCHASE_ORDER_AUDIT)
    @Produces(MediaType.APPLICATION_JSON)//因为aop只拦截了save***开始的方法，注入创建人，因此这里的提交审核，也为save开始
    public AppResult saveCommitAuditPurchaseOrder(@BeanParam PurchaseOrderAddData purchaseOrder,@Context ContainerRequestContext requestContext) {
        purchaseOrderBiz.savePurchaseOrder(purchaseOrder,PurchaseOrderStatusEnum.AUDIT.getCode());
        return ResultUtil.createSucssAppResult("提交审核采购单成功","");
    }
    @GET
    @Path(SupplyConstants.PurchaseOrder.SUPPLIERS)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult<List<Supplier>> findSuppliers(@Context ContainerRequestContext requestContext)  {

        return ResultUtil.createSucssAppResult("根据用户id查询对应的供应商",purchaseOrderBiz.findSuppliersByUserId(requestContext));

    }

    @GET
    @Path(SupplyConstants.PurchaseOrder.SUPPLIERS_ITEMS+"/{supplierCode}")
    @Produces(MediaType.APPLICATION_JSON)
    public Pagenation<PurchaseDetail> findPurchaseDetailBysupplierCode(@PathParam("supplierCode") String supplierCode, @BeanParam ItemForm form, @BeanParam Pagenation<PurchaseDetail> page,@QueryParam("skus") String skus) {

        return  purchaseOrderBiz.findPurchaseDetailBySupplierCode(supplierCode,form,page,skus);

    }
    @PUT
    @Path(SupplyConstants.PurchaseOrder.PURCHASE_ORDER+"/{id}")//保存修改
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult updatePurchaseOrder(@BeanParam PurchaseOrderAddData purchaseOrderAddData,@Context ContainerRequestContext requestContext) {

        purchaseOrderBiz.updatePurchaseOrder(purchaseOrderAddData,requestContext);
        return  ResultUtil.createSucssAppResult("修改采购订单信息成功","");

    }
    @PUT
    @Path(SupplyConstants.PurchaseOrder.PURCHASE_ORDER_AUDIT+"/{id}")//提交审核修改
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult updatePurchaseOrderAudit(@BeanParam PurchaseOrderAddData purchaseOrderAddData,@Context ContainerRequestContext requestContext) {

        purchaseOrderAddData.setStatus(PurchaseOrderStatusEnum.AUDIT.getCode());
        purchaseOrderBiz.updatePurchaseOrder(purchaseOrderAddData,requestContext);
        return  ResultUtil.createSucssAppResult("提交审核修改采购订单信息成功","");

    }

    @GET
    @Path(SupplyConstants.PurchaseOrder.SUPPLIERS_ALL_ITEMS+"/{supplierCode}")
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult<PurchaseDetail> findAllPurchaseDetailBysupplierCode(@PathParam("supplierCode") String supplierCode) {
        return ResultUtil.createSucssAppResult("根据供应商编码查询所有的有效商品成功",purchaseOrderBiz.findAllPurchaseDetailBysupplierCode(supplierCode));
    }


    @GET
    @Path(SupplyConstants.PurchaseOrder.PURCHASE_ORDER+"/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult<PurchaseOrder> findPurchaseOrderAddDataById(@PathParam("id") Long id) {

        return ResultUtil.createSucssAppResult("根据采购单Id查询采购单信息成功",purchaseOrderBiz.findPurchaseOrderAddDataById(id));

    }

    @POST
    @Path(SupplyConstants.PurchaseOrder.UPDATE_STATE+"/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult updatePurchaseState(@BeanParam PurchaseOrder purchaseOrder) {

        purchaseOrderBiz.updatePurchaseOrderState(purchaseOrder);
        return ResultUtil.createSucssAppResult("状态修改成功","");

    }

    @POST
    @Path(SupplyConstants.PurchaseOrder.FREEZE+"/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult updatePurchaseStateFreeze(@BeanParam PurchaseOrder purchaseOrder) {

        purchaseOrderBiz.updatePurchaseStateFreeze(purchaseOrder);
        return ResultUtil.createSucssAppResult("采购单冻结状态修改成功","");

    }

    @POST
    @Path(SupplyConstants.PurchaseOrder.WAREHOUSE_ADVICE+"/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult saveWarahouseAdvice(@BeanParam PurchaseOrder purchaseOrder,@Context ContainerRequestContext requestContext) {
        purchaseOrderBiz.warahouseAdvice(purchaseOrder,requestContext);
        return ResultUtil.createSucssAppResult("入库通知单添加成功","");
    }

}
