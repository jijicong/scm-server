package org.trc.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.trc.biz.supplier.ISupplierBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.dict.Dict;
import org.trc.domain.supplier.*;
import org.trc.form.supplier.SupplierBrandForm;
import org.trc.form.supplier.SupplierCategoryForm;
import org.trc.form.supplier.SupplierForm;
import org.trc.util.AppResult;
import org.trc.util.Pagenation;
import org.trc.util.ResultUtil;

import javax.ws.rs.*;
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
        return supplierBiz.SupplierPage(form, page);
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
             @BeanParam SupplierAfterSaleInfo supplierAfterSaleInfo) throws Exception {
        supplierBiz.saveSupplier(supplier, certificate, supplierCategory, supplierBrand, supplierFinancialInfo, supplierAfterSaleInfo);
        return ResultUtil.createSucssAppResult("保存供应商成功", "");
    }

/*    @PUT
    @Path(SupplyConstants.Supply.Supplier.SUPPLIER + "/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult updateSupplier(@BeanParam Supplier supplier) throws Exception {
        supplierBiz.updateSupplier(supplier);
        return ResultUtil.createSucssAppResult("修改供应商成功", "");
    }*/

    @PUT
    @Path(SupplyConstants.Supply.Supplier.SUPPLIER + "/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult updateSupplier(@BeanParam Supplier supplier, @BeanParam Certificate certificate, @BeanParam SupplierCategory supplierCategory,
                                  @BeanParam SupplierBrand supplierBrand, @BeanParam SupplierFinancialInfo supplierFinancialInfo,
                                  @BeanParam SupplierAfterSaleInfo supplierAfterSaleInfo) throws Exception {
        supplierBiz.saveSupplier(supplier, certificate, supplierCategory, supplierBrand, supplierFinancialInfo, supplierAfterSaleInfo);
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
    public AppResult<SupplierBrandExt> querySupplierBrand(@QueryParam("supplierCode") String supplierCode) throws Exception {
        return ResultUtil.createSucssAppResult("查询供应商代理品牌成功", supplierBiz.querySupplierBrand(supplierCode));
    }

}
