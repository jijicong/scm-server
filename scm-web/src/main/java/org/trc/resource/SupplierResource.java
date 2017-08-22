package org.trc.resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.trc.biz.supplier.ISupplierBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.dict.Dict;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.supplier.*;
import org.trc.enums.SuccessFailureEnum;
import org.trc.enums.ValidEnum;
import org.trc.enums.ZeroToNineEnum;
import org.trc.exception.GoodsException;
import org.trc.form.supplier.SupplierChannelRelationForm;
import org.trc.form.supplier.SupplierForm;
import org.trc.util.AppResult;
import org.trc.util.Pagenation;
import org.trc.util.ResultUtil;

import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by hzwdx on 2017/5/5.
 */
@Component
@Path(SupplyConstants.Supply.ROOT)
public class SupplierResource {

    private Logger log = LoggerFactory.getLogger(SupplierResource.class);

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
    public Pagenation<Supplier> supplierPage(@BeanParam Pagenation<Supplier> page,@Context ContainerRequestContext requestContext,@BeanParam SupplierForm form) throws Exception {
        return supplierBiz.supplierPage(page,(AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO),form);
    }

    @GET
    @Path(SupplyConstants.Supply.Supplier.SUPPLIER_LIST)
    @Produces(MediaType.APPLICATION_JSON)
    public Response querySuppliers(@BeanParam SupplierForm form) throws Exception {
        return ResultUtil.createSuccessResult("查询供应商列表成功", supplierBiz.querySuppliers(form));
    }

    @POST
    @Path(SupplyConstants.Supply.Supplier.SUPPLIER)
    @Produces(MediaType.APPLICATION_JSON)
    public Response saveSupplier(@BeanParam Supplier supplier, @BeanParam Certificate certificate, @BeanParam SupplierCategory supplierCategory,
             @BeanParam SupplierBrand supplierBrand, @BeanParam SupplierFinancialInfo supplierFinancialInfo,
             @BeanParam SupplierAfterSaleInfo supplierAfterSaleInfo, @Context ContainerRequestContext requestContext) throws Exception {
        supplierBiz.saveSupplier(supplier, certificate, supplierCategory, supplierBrand, supplierFinancialInfo, supplierAfterSaleInfo, (AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO));
        return ResultUtil.createSuccessResult("保存供应商成功","");
    }

    @PUT
    @Path(SupplyConstants.Supply.Supplier.SUPPLIER + "/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateSupplier(@BeanParam Supplier supplier, @BeanParam Certificate certificate, @BeanParam SupplierCategory supplierCategory,
                                  @BeanParam SupplierBrand supplierBrand, @BeanParam SupplierFinancialInfo supplierFinancialInfo,
                                  @BeanParam SupplierAfterSaleInfo supplierAfterSaleInfo, @Context ContainerRequestContext requestContext) throws Exception {
        supplierBiz.updateSupplier(supplier, certificate, supplierCategory, supplierBrand, supplierFinancialInfo, supplierAfterSaleInfo, (AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO));
        return ResultUtil.createSuccessResult("更新供应商成功","");
    }

    @PUT
    @Path(SupplyConstants.Supply.Supplier.IS_VALID + "/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateValid(@PathParam("id") Long id, @FormParam("isValid") String isValid, @Context ContainerRequestContext requestContext) throws Exception {
        supplierBiz.updateValid(id, isValid, (AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO));
        String _valid = ZeroToNineEnum.ZERO.getCode();
        if (StringUtils.equals(ZeroToNineEnum.ZERO.getCode(), isValid)) {
            _valid = ZeroToNineEnum.ONE.getCode();
        }
        return ResultUtil.createSuccessResult(String.format("%s成功!", ValidEnum.getValidEnumByCode(_valid).getName()), "");
    }

    @GET
    @Path(SupplyConstants.Supply.Supplier.SUPPLIER + "/{supplierCode}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findSupplierByCode(@PathParam("supplierCode") String supplierCode) throws Exception {
        return ResultUtil.createSuccessResult("查询供应商成功", supplierBiz.querySupplierInfo(supplierCode));
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
    public Response querySupplierBrand(@QueryParam("supplierCode") String supplierCode) throws Exception {
        return ResultUtil.createSuccessResult("查询供应商品牌成功", supplierBiz.querySupplierBrand(supplierCode));
    }

    @GET
    @Path(SupplyConstants.Supply.SupplierChannel.CHANNELS)
    @Produces(MediaType.APPLICATION_JSON)
    public Response queryChannelRelation(@BeanParam SupplierChannelRelationForm form) throws Exception {
        return ResultUtil.createSuccessResult("查询供应商渠道关系成功", supplierBiz.queryChannelRelation(form));
    }

    @GET
    @Path(SupplyConstants.Supply.SupplierBrand.CHECK_CATEGORY_BRAND_VALID_STATUS+"/{categoryId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response checkCategoryBrandValidStatus(@PathParam("categoryId") Long categoryId, @QueryParam("brandId") Long brandId) throws Exception {
        supplierBiz.checkCategoryBrandValidStatus(categoryId, brandId);
        return ResultUtil.createSuccessResult("检查分类品牌启停用状态成功","");
    }


}
