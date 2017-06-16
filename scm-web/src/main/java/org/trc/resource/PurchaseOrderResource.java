package org.trc.resource;

import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.ResultActions;
import org.trc.biz.purchase.IPurchaseOrderBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.purchase.PurchaseDetail;
import org.trc.domain.purchase.PurchaseOrder;
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
        return  purchaseOrderBiz.purchaseOrderPage(form , page);
    }

    @POST
    @Path(SupplyConstants.PurchaseOrder.PURCHASE_ORDER)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult savePurchaseOrder(@BeanParam PurchaseOrder purchaseOrder,@Context ContainerRequestContext requestContext) throws Exception{

        purchaseOrderBiz.savePurchaseOrder(purchaseOrder, PurchaseOrderStatusEnum.HOLD.getCode());
        return ResultUtil.createSucssAppResult("保存采购订单成功","");

    }
    @POST
    @Path(SupplyConstants.PurchaseOrder.PURCHASE_ORDER_AUDIT)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult commitAuditPurchaseOrder(@BeanParam PurchaseOrder purchaseOrder,@Context ContainerRequestContext requestContext) throws Exception{
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


}
