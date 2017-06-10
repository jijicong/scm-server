package org.trc.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.trc.biz.supplier.ISupplierBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.dict.Dict;
import org.trc.domain.supplier.*;
import org.trc.form.supplier.SupplierChannelRelationForm;
import org.trc.form.supplier.SupplierForm;
import org.trc.util.AppResult;
import org.trc.util.Pagenation;
import org.trc.util.ResultUtil;

import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by hzwdx on 2017/5/5.
 */
@Component
@Path(SupplyConstants.Supply.ROOT)
public class SupplierResource {

    @Autowired
    private ISupplierBiz supplierBiz;

    @GET
    @Path(SupplyConstants.Supply.Supplier.SUPPLIER_PAGE)
    @Produces(MediaType.APPLICATION_JSON)
    public Pagenation<Supplier> supplierPage(@BeanParam SupplierForm form, @BeanParam Pagenation<Supplier> page) throws Exception {
        return supplierBiz.supplierPage(form, page);
    }

    @GET
    @Path(SupplyConstants.Supply.Supplier.APPLY_SUPPLIER_PAGE)
    @Produces(MediaType.APPLICATION_JSON)
    public Pagenation<Supplier> supplierPage(@BeanParam Pagenation<Supplier> page,@Context ContainerRequestContext requestContext) throws Exception {
        return supplierBiz.supplierPage(page,requestContext);
    }

    @GET
    @Path(SupplyConstants.Supply.Supplier.SUPPLIER_LIST)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult<List<Supplier>> querySuppliers(@BeanParam SupplierForm form) throws Exception {
        return ResultUtil.createSucssAppResult("查询供应商列表成功", supplierBiz.querySuppliers(form));
    }

    @POST
    @Path(SupplyConstants.Supply.Supplier.SUPPLIER)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult saveSupplier(@BeanParam Supplier supplier, @BeanParam Certificate certificate, @BeanParam SupplierCategory supplierCategory,
             @BeanParam SupplierBrand supplierBrand, @BeanParam SupplierFinancialInfo supplierFinancialInfo,
             @BeanParam SupplierAfterSaleInfo supplierAfterSaleInfo, @Context ContainerRequestContext requestContext) throws Exception {
        supplierBiz.saveSupplier(supplier, certificate, supplierCategory, supplierBrand, supplierFinancialInfo, supplierAfterSaleInfo);
        return ResultUtil.createSucssAppResult("保存供应商成功", "");
    }

    @PUT
    @Path(SupplyConstants.Supply.Supplier.SUPPLIER + "/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult updateSupplier(@BeanParam Supplier supplier, @BeanParam Certificate certificate, @BeanParam SupplierCategory supplierCategory,
                                  @BeanParam SupplierBrand supplierBrand, @BeanParam SupplierFinancialInfo supplierFinancialInfo,
                                  @BeanParam SupplierAfterSaleInfo supplierAfterSaleInfo) throws Exception {
        supplierBiz.updateSupplier(supplier, certificate, supplierCategory, supplierBrand, supplierFinancialInfo, supplierAfterSaleInfo);
        return ResultUtil.createSucssAppResult("保存供应商成功", "");
    }

    @POST
    @Path(SupplyConstants.Supply.Supplier.IS_VALID + "/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult updateValid(@PathParam("id") Long id, @FormParam("isValid") String isValid) throws Exception {
        supplierBiz.updateValid(id, isValid);
        return ResultUtil.createSucssAppResult("保存供应商成功", "");
    }

    @GET
    @Path(SupplyConstants.Supply.Supplier.SUPPLIER + "/{supplierCode}")
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult<Dict> findSupplierByCode(@PathParam("supplierCode") String supplierCode) throws Exception {
        return ResultUtil.createSucssAppResult("查询供应商成功", supplierBiz.querySupplierInfo(supplierCode));
    }

    @GET
    @Path(SupplyConstants.Supply.SupplierCategory.SUPPLIER_CATEGORY_LIST)
    @Produces(MediaType.APPLICATION_JSON)
    public List<SupplierCategoryExt> querySupplierCategory(@QueryParam("supplierCode") String supplierCode) throws Exception {
        return supplierBiz.querySupplierCategory(supplierCode);
    }

    @GET
    @Path(SupplyConstants.Supply.SupplierBrand.SUPPLIER_BRAND_LIST)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult<List<SupplierBrandExt>> querySupplierBrand(@QueryParam("supplierCode") String supplierCode) throws Exception {
        return ResultUtil.createSucssAppResult("查询供应商品牌成功", supplierBiz.querySupplierBrand(supplierCode));
    }

    @GET
    @Path(SupplyConstants.Supply.SupplierChannel.CHANNELS)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult<SupplierChannelRelationExt> queryChannelRelation(@BeanParam SupplierChannelRelationForm form) throws Exception {
        return ResultUtil.createSucssAppResult("查询供应商渠道关系成功", supplierBiz.queryChannelRelation(form));
    }



}
