package org.trc.resource;

import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.ResultActions;
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
import java.util.concurrent.ExecutionException;

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
    public Pagenation<PurchaseOrder> purchaseOrderPagenation(@BeanParam PurchaseOrderForm form, @BeanParam Pagenation<PurchaseOrder> page)throws Exception{
        //采夠訂單列表
        return  purchaseOrderBiz.purchaseOrderPage(form , page);
    }

    @POST
    @Path(SupplyConstants.PurchaseOrder.PURCHASE_ORDER)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult savePurchaseOrder(@BeanParam PurchaseOrderAddData purchaseOrder, @Context ContainerRequestContext requestContext) throws Exception{

        purchaseOrderBiz.savePurchaseOrder(purchaseOrder, PurchaseOrderStatusEnum.HOLD.getCode());
        return ResultUtil.createSucssAppResult("保存采购订单成功","");

    }
    @POST
    @Path(SupplyConstants.PurchaseOrder.PURCHASE_ORDER_AUDIT)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult commitAuditPurchaseOrder(@BeanParam PurchaseOrderAddData purchaseOrder,@Context ContainerRequestContext requestContext) throws Exception{
        purchaseOrderBiz.savePurchaseOrder(purchaseOrder,PurchaseOrderStatusEnum.AUDIT.getCode());
        return ResultUtil.createSucssAppResult("提交审核采购单成功","");
    }
    @GET
    @Path(SupplyConstants.PurchaseOrder.SUPPLIERS)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult<List<Supplier>> findSuppliers(@Context ContainerRequestContext requestContext) throws Exception {
        return ResultUtil.createSucssAppResult("根据用户id查询对应的供应商",purchaseOrderBiz.findSuppliersByUserId(requestContext));
    }

    @GET
    @Path(SupplyConstants.PurchaseOrder.SUPPLIERS_ITEMS+"/{supplierCode}")
    @Produces(MediaType.APPLICATION_JSON)
    public Pagenation<PurchaseDetail> findPurchaseDetailBysupplierCode(@PathParam("supplierCode") String supplierCode, @BeanParam ItemForm form, @BeanParam Pagenation<PurchaseDetail> page,@QueryParam("skus") String skus) throws Exception{

        return  purchaseOrderBiz.findPurchaseDetailBySupplierCode(supplierCode,form,page,skus);

    }

    @GET
    @Path(SupplyConstants.PurchaseOrder.PURCHASE_ORDER+"/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult<PurchaseOrder> findPurchaseOrderAddDataById(@PathParam("id") Long id) throws Exception{

        return ResultUtil.createSucssAppResult("根据采购单Id查询采购单信息成功",purchaseOrderBiz.findPurchaseOrderAddDataById(id));

    }

    @POST
    @Path(SupplyConstants.PurchaseOrder.UPDATE_STATE+"/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult updatePurchaseState(@BeanParam PurchaseOrder purchaseOrder) throws Exception{

        purchaseOrderBiz.updatePurchaseOrderState(purchaseOrder);
        return ResultUtil.createSucssAppResult("状态修改成功","");

    }



}
