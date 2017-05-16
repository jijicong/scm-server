package org.trc.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.trc.biz.supplier.ISupplierApplyBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.category.Property;
import org.trc.domain.supplier.SupplierApply;
import org.trc.form.category.PropertyForm;
import org.trc.form.supplier.SupplierApplyForm;
import org.trc.util.AppResult;
import org.trc.util.Pagenation;
import org.trc.util.ResultUtil;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.awt.*;

/**
 * Created by hzqph on 2017/5/12.
 */
@Path(SupplyConstants.Supply.ROOT)
public class SupplierApplyResource {

    @Autowired
    private ISupplierApplyBiz supplierApplyBiz;


    @GET
    @Path(SupplyConstants.Supply.SupplierApply.SUPPLIER_APPLY_PAGE)
    @Produces(MediaType.APPLICATION_JSON)
    public Pagenation<SupplierApply> supplierApplyPage(@BeanParam SupplierApplyForm form, @BeanParam Pagenation<SupplierApply> page) throws Exception {
        return supplierApplyBiz.supplierApplyPage(page, form);
    }

    @GET
    @Path(SupplyConstants.Supply.SupplierApply.SUPPLIER_APPLY+"/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult selectOneById(@PathParam("id") Long id) throws Exception {
        return ResultUtil.createSucssAppResult("供应商审核信息查询成功", supplierApplyBiz.selectOneById(id));
    }

    @PUT
    @Path(SupplyConstants.Supply.SupplierApply.SUPPLIER_APPLY+"/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult selectOneById(@BeanParam SupplierApply SupplierApply) throws Exception {
        supplierApplyBiz.auditSupplierApply(SupplierApply);
        return ResultUtil.createSucssAppResult("供应商审核成功","");
    }
}
