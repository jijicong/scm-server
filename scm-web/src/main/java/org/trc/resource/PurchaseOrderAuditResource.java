package org.trc.resource;

import org.springframework.stereotype.Component;
import org.trc.biz.purchase.IPurchaseOrderAuditBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.purchase.PurchaseOrder;
import org.trc.domain.purchase.PurchaseOrderAddAudit;
import org.trc.domain.purchase.PurchaseOrderAudit;
import org.trc.enums.PurchaseOrderAuditEnum;
import org.trc.form.purchase.PurchaseOrderAuditForm;
import org.trc.form.purchase.PurchaseOrderForm;
import org.trc.util.AppResult;
import org.trc.util.Pagenation;
import org.trc.util.ResultUtil;

import javax.annotation.Resource;
import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

/**
 * 采购订单审核
 * Created by sone on 2017/6/20.
 */
@Component
@Path(SupplyConstants.PurchaseOrderAudit.ROOT)
public class PurchaseOrderAuditResource {

    @Resource
    private IPurchaseOrderAuditBiz iPurchaseOrderAuditBiz;

    @GET
    @Path(SupplyConstants.PurchaseOrderAudit.PURCHASE_ORDER_AUDIT_PAGE)
    @Produces(MediaType.APPLICATION_JSON)
    public Pagenation<PurchaseOrderAddAudit> purchaseOrderAuditPagenation(@BeanParam PurchaseOrderAuditForm form, @BeanParam Pagenation<PurchaseOrderAddAudit> page,@Context ContainerRequestContext requestContext)throws Exception{
        if(form.getPurchaseOrderAuditStatus()==null){ //说明是第一次请求
            form.setPurchaseOrderAuditStatus(PurchaseOrderAuditEnum.AUDIT.getCode());
        }
        return iPurchaseOrderAuditBiz.purchaseOrderAuditPage(form,page,requestContext);
    }
    //PURCHASE_ORDER_AUDIT
    @PUT
    @Path(SupplyConstants.PurchaseOrderAudit.PURCHASE_ORDER_AUDIT)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult auditPurchaseOrder(@BeanParam PurchaseOrderAudit purchaseOrderAudit) throws Exception{

        iPurchaseOrderAuditBiz.auditPurchaseOrder(purchaseOrderAudit);
        return ResultUtil.createSucssAppResult("审核采购单信息成功","");

    }


}
