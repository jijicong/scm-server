package org.trc.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.trc.biz.supplier.ISupplierApplyBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.supplier.Supplier;
import org.trc.domain.supplier.SupplierApply;
import org.trc.domain.supplier.SupplierApplyAudit;
import org.trc.form.supplier.SupplierApplyAuditForm;
import org.trc.form.supplier.SupplierApplyForm;
import org.trc.util.AppResult;
import org.trc.util.Pagenation;
import org.trc.util.ResultUtil;

import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

/**
 * Created by hzqph on 2017/5/12.
 */
@Component
@Path(SupplyConstants.Supply.ROOT)
public class SupplierApplyResource {

    @Autowired
    private ISupplierApplyBiz supplierApplyBiz;


    @GET
    @Path(SupplyConstants.Supply.SupplierApplyAudit.SUPPLIER_APPLY_AUDIT_PAGE)
    @Produces(MediaType.APPLICATION_JSON)
    public Pagenation<SupplierApplyAudit> supplierApplyAuditPage(@BeanParam SupplierApplyAuditForm form, @BeanParam Pagenation<SupplierApplyAudit> page) throws Exception {
        return supplierApplyBiz.supplierApplyAuditPage(page, form);
    }

    @GET
    @Path(SupplyConstants.Supply.SupplierApplyAudit.SUPPLIER_APPLY_AUDIT+"/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult selectOneById(@PathParam("id") Long id) throws Exception {
        return ResultUtil.createSucssAppResult("供应商审核信息查询成功", supplierApplyBiz.selectOneById(id));
    }

    @PUT
    @Path(SupplyConstants.Supply.SupplierApplyAudit.SUPPLIER_APPLY_AUDIT+"/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult auditSupplierApply(@BeanParam SupplierApplyAudit SupplierApplyAudit,@Context ContainerRequestContext requestContext) throws Exception {
        supplierApplyBiz.auditSupplierApply(SupplierApplyAudit,requestContext);
        return ResultUtil.createSucssAppResult("供应商审核成功","");
    }

    @GET
    @Path(SupplyConstants.Supply.SupplierApply.SUPPLIER_APPLY_PAGE)
    @Produces(MediaType.APPLICATION_JSON)
    public Pagenation<SupplierApply> supplierApplyPage(@BeanParam SupplierApplyForm form, @BeanParam Pagenation<SupplierApply> page, @Context ContainerRequestContext requestContext) throws Exception {
        return supplierApplyBiz.supplierApplyPage(page, form,requestContext);
    }

    @POST
    @Path(SupplyConstants.Supply.SupplierApply.SUPPLIER_APPLY)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult saveSupplierApply(@BeanParam SupplierApply supplierApply,@Context ContainerRequestContext requestContext)throws Exception{
        supplierApplyBiz.saveSupplierApply(supplierApply,requestContext);
        return ResultUtil.createSucssAppResult("供应商申请成功","");
    }

    @PUT
    @Path(SupplyConstants.Supply.SupplierApply.SUPPLIER_APPLY+"/{id}")
    public AppResult updateSupplierApply(@BeanParam SupplierApply supplierApply,@Context ContainerRequestContext requestContext)throws Exception{
        supplierApplyBiz.updateSupplierApply(supplierApply,requestContext);
        return ResultUtil.createSucssAppResult("供应商申请修改成功","");
    }

    @POST
    @Path(SupplyConstants.Supply.SupplierApply.SUPPLIER_APPLY+"/{id}")
    public AppResult deleteSupplierApply(@PathParam("id")Long supplierApplyId)throws Exception{
        supplierApplyBiz.deleteSupplierApply(supplierApplyId);
        return ResultUtil.createSucssAppResult("供应商申请删除成功","");
    }

    @GET
    @Path(SupplyConstants.Supply.SupplierApply.SUPPLIER_APPLY+"/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult selectSupplierById(@PathParam("id") Long id) throws Exception {
        return ResultUtil.createSucssAppResult("供应商审核信息查询成功", supplierApplyBiz.selectSupplierApplyById(id));
    }
}
