package org.trc.resource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.trc.biz.goods.IGoodsBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.goods.*;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.enums.ValidEnum;
import org.trc.enums.ZeroToNineEnum;
import org.trc.form.JDModel.SupplyItemsForm;
import org.trc.form.SupplyItemsExt;
import org.trc.form.goods.ExternalItemSkuForm;
import org.trc.form.goods.ItemsForm;
import org.trc.form.goods.SkusForm;
import org.trc.form.supplier.SupplierForm;
import org.trc.util.Pagenation;
import org.trc.util.ResultUtil;

import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by hzwdx on 2017/5/24.
 */
@Api(value = "商品管理")
@Component
@Path(SupplyConstants.Goods.ROOT)
public class GoodsResource {

    private Logger log = LoggerFactory.getLogger(GoodsResource.class);

    @Autowired
    private IGoodsBiz goodsBiz;

    @ApiOperation(value = "自采商品管理分页查询", response = Items.class)
    @GET
    @Path(SupplyConstants.Goods.GOODS_PAGE)
    @Produces(MediaType.APPLICATION_JSON)
    public Response goodsPage(@BeanParam ItemsForm form, @BeanParam Pagenation<Items> page,@Context ContainerRequestContext requestContext) throws Exception {
        //return goodsBiz.itemsPage(form, page);
        return ResultUtil.createSuccessPageResult(goodsBiz.itemsPage(form, page,(AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO)));
    }

    @ApiOperation("商品查询分页查询")
    @GET
    @Path(SupplyConstants.Goods.GOODS_SKU_PAGE)
    @Produces(MediaType.APPLICATION_JSON)
    public Response itemsSkusPage(@BeanParam SkusForm form, @BeanParam Pagenation<Skus> page, @Context ContainerRequestContext requestContext) throws Exception {
        //return goodsBiz.itemsSkusPage(form, page, (AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO));
        return ResultUtil.createSuccessPageResult(goodsBiz.itemsSkusPage(form, page, (AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO)));
    }

    @ApiOperation("新增自采商品")
    @POST
    @Path(SupplyConstants.Goods.GOODS)
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/x-www-form-urlencoded")
    public Response saveGoods(@BeanParam Items items, @BeanParam Skus skus, @BeanParam ItemNaturePropery itemNaturePropery,
                              @BeanParam ItemSalesPropery itemSalesPropery, @Context ContainerRequestContext requestContext) throws Exception {
        goodsBiz.saveItems(items, skus, itemNaturePropery, itemSalesPropery);
        return ResultUtil.createSuccessResult("保存商品成功", "");
    }

    //V3.1增加数据权限
    @ApiOperation("更新自采商品")
    @PUT
    @Path(SupplyConstants.Goods.GOODS + "/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateGoods(@BeanParam Items items, @BeanParam Skus skus, @BeanParam ItemNaturePropery itemNaturePropery,
                                 @BeanParam ItemSalesPropery itemSalesPropery, @Context ContainerRequestContext requestContext) throws Exception {
        goodsBiz.updateItems(items, skus, itemNaturePropery, itemSalesPropery, (AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO));
        return ResultUtil.createSuccessResult("更新商品成功", "");
    }

    @ApiOperation("自采商品起停用")
    //V3.1增加数据权限
    @PUT
    @Path(SupplyConstants.Goods.IS_VALID + "/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateValid(@PathParam("id") Long id, @FormParam("isValid") String isValid, @Context ContainerRequestContext requestContext) throws Exception {
        goodsBiz.updateValid(id, isValid, (AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO));
        String _valid = ZeroToNineEnum.ZERO.getCode();
        if (StringUtils.equals(ZeroToNineEnum.ZERO.getCode(), isValid)) {
            _valid = ZeroToNineEnum.ONE.getCode();
        }
        return ResultUtil.createSuccessResult(String.format("%s成功!", ValidEnum.getValidEnumByCode(_valid).getName()), "");
    }

    //V3.1增加数据权限
    @ApiOperation("自采商品SKU起停用")
    @PUT
    @Path(SupplyConstants.Goods.SKU_VALID + "/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateSkusValid(@PathParam("id") Long id, @FormParam("spuCode") String spuCode, @FormParam("isValid") String isValid, @Context ContainerRequestContext requestContext) throws Exception {
        goodsBiz.updateSkusValid(id, spuCode, isValid, (AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO));
        String _valid = ZeroToNineEnum.ZERO.getCode();
        if (StringUtils.equals(ZeroToNineEnum.ZERO.getCode(), isValid)) {
            _valid = ZeroToNineEnum.ONE.getCode();
        }
        return ResultUtil.createSuccessResult(String.format("%s成功!", ValidEnum.getValidEnumByCode(_valid).getName()), "");
    }

    @ApiOperation("查询商品信息")
    @GET
    @Path(SupplyConstants.Goods.GOODS_SPU_CODE+"/{spuCode}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response queryItemsInfo(@PathParam("spuCode") String spuCode, @QueryParam("skuCode") String skuCode, @Context ContainerRequestContext requestContext) throws Exception {
        return ResultUtil.createSuccessResult("查询商品信息成功", goodsBiz.queryItemsInfo(spuCode, skuCode, (AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO)));
    }

    @ApiOperation("查询自采商品分类属性")
    @GET
    @Path(SupplyConstants.Goods.ITEMS_CATEGORY_PROPERTY+"/{spuCode}/{categoryId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response queryItemsCategoryProperty(@PathParam("spuCode") String spuCode, @PathParam("categoryId") Long categoryId) throws Exception {
        return ResultUtil.createSuccessResult("查询商品分类属性成功", goodsBiz.queryItemsCategoryProperty(spuCode, categoryId));
    }

    @ApiOperation("代发商品管理分页查询")
    @GET
    @Path(SupplyConstants.Goods.EXTERNAL_GOODS_PAGE)
    @Produces(MediaType.APPLICATION_JSON)
    public Response externalGoodsPage(@BeanParam ExternalItemSkuForm form, @BeanParam Pagenation<ExternalItemSku> page,@Context ContainerRequestContext requestContext) throws Exception {
        //return goodsBiz.externalGoodsPage(form, page);
        AclUserAccreditInfo aclUserAccreditInfo = (AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO);
        return ResultUtil.createSuccessPageResult(goodsBiz.externalGoodsPage(form, page,aclUserAccreditInfo));
    }

    @ApiOperation("代发供应商代发商品分页查询")
    @GET
    @Path(SupplyConstants.Goods.EXTERNAL_GOODS_PAGE_2)
    @Produces(MediaType.APPLICATION_JSON)
    public Response externalGoodsPage2(@BeanParam SupplyItemsForm form, @BeanParam Pagenation<SupplyItemsExt> page, @Context ContainerRequestContext requestContext) throws Exception {
        //return goodsBiz.externalGoodsPage2(form, page);
        return ResultUtil.createSuccessPageResult(goodsBiz.externalGoodsPage2(form, page, (AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO)));
    }

    @ApiOperation("查询代发商品列表")
    @GET
    @Path(SupplyConstants.Goods.EXTERNAL_ITEM_SKU_LIST)
    @Produces(MediaType.APPLICATION_JSON)
    public Response queryExternalItems(@BeanParam ExternalItemSkuForm form) throws Exception {
        return ResultUtil.createSuccessResult("查询代发商品列表",goodsBiz.queryExternalItems(form));
    }

    @ApiOperation("新增代发商品")
    @POST
    @Path(SupplyConstants.Goods.EXTERNAL_ITEM_SKU)
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/x-www-form-urlencoded")
    public Response saveExternalItems(@FormParam("supplySkus") String supplySkus, @Context ContainerRequestContext requestContext) throws Exception {
        goodsBiz.saveExternalItems(supplySkus, (AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO));
        return ResultUtil.createSuccessResult("新增代发商品成功", "");
    }

    @ApiOperation("代发商品起停用")
    @PUT
    @Path(SupplyConstants.Goods.EXTERNAL_ITEM__VALID + "/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateExternalItemsValid(@PathParam("id") Long id, @FormParam("isValid") String isValid, @Context ContainerRequestContext requestContext) throws Exception {
        goodsBiz.updateExternalItemsValid(id, isValid, (AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO));
        String _valid = ZeroToNineEnum.ZERO.getCode();
        if (StringUtils.equals(ZeroToNineEnum.ZERO.getCode(), isValid)) {
            _valid = ZeroToNineEnum.ONE.getCode();
        }
        return ResultUtil.createSuccessResult(String.format("%s成功!", ValidEnum.getValidEnumByCode(_valid).getName()), "");
    }

    @ApiOperation("更新代发商品")
    @PUT
    @Path(SupplyConstants.Goods.EXTERNAL_ITEM_SKU + "/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/x-www-form-urlencoded")
    public Response updateExternalItems(@BeanParam ExternalItemSku externalItemSku, @Context ContainerRequestContext requestContext) throws Exception {
        goodsBiz.updateExternalItems(externalItemSku, (AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO));
        return ResultUtil.createSuccessResult("更新代发商品成功", "");
    }

    @ApiOperation("检查属性启停用状态")
    @GET
    @Path(SupplyConstants.Goods.CHECK_PROPERTY_STATUS)
    @Produces(MediaType.APPLICATION_JSON)
    public Response checkPropetyStatus(@QueryParam("propertyInfo") String propertyInfo) throws Exception {
        goodsBiz.checkPropetyStatus(propertyInfo);
        return ResultUtil.createSuccessResult("检查属性启停用状态成功","");
    }

    @ApiOperation("查询供应商列表")
    @GET
    @Path(SupplyConstants.Goods.SUPPLIERS_LIST)
    @Produces(MediaType.APPLICATION_JSON)
    public Response querySupplierList(@BeanParam SupplierForm form, @Context ContainerRequestContext requestContext) throws Exception {
        return ResultUtil.createSuccessResult("查询供应商列表成功", goodsBiz.querySuppliers(form, (AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO)));

    }

    @ApiOperation("检查条形码是否可用")
    @POST
    @Path(SupplyConstants.Goods.CHECK_BARCODE_ONLY)
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/x-www-form-urlencoded")
    public Response checkBarcodeOnly(@FormParam("barCode") String barCode,@FormParam("skuCode") String skuCode,@FormParam("isValid") String isValid,@FormParam("notIn")String notIn) throws Exception {
        if (StringUtils.equals(isValid,ValidEnum.NOVALID.getCode())){
            return ResultUtil.createSuccessResult("条形码可用", "");
        }else {
            goodsBiz.checkBarcodeOnly(barCode,skuCode,notIn);
            return ResultUtil.createSuccessResult("条形码可用", "");
        }
    }

    @ApiOperation("条形码校验")
    @POST
    @Path(SupplyConstants.Goods.SKU_INFO_BAR)
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/x-www-form-urlencoded")
    public Response skuInfoBarCode(@FormParam("skuInfo") String skuInfo){
        goodsBiz.skuInfoBarCode(skuInfo);
        return ResultUtil.createSuccessResult("条形码可用", "");
    }

    @ApiOperation("导出代发商品")
    @GET
    @Path(SupplyConstants.Goods.EXTERNAL_ITEM_EXPORT)
    @Consumes("text/plain;charset=utf-8")
    @Produces("application/octet-stream")
    public Response exportExternalGoods(@BeanParam ExternalItemSkuForm queryModel, @Context ContainerRequestContext requestContext) {
        return goodsBiz.exportExternalGoods(queryModel, (AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO));
    }

    @ApiOperation("导出自采商品")
    @GET
    @Path(SupplyConstants.Goods.ITEMS_EXPORT)
    @Consumes("text/plain;charset=utf-8")
    @Produces("application/octet-stream")
    public Response exportItemGoods(@BeanParam SkusForm queryModel, @Context ContainerRequestContext requestContext) {
        return goodsBiz.exportItemGoods(queryModel,(AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO));
    }
}
