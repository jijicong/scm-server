package org.trc.resource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.trc.biz.goods.IGoodsBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.category.CategoryProperty;
import org.trc.domain.goods.*;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.supplier.Supplier;
import org.trc.enums.ValidEnum;
import org.trc.enums.ZeroToNineEnum;
import org.trc.form.JDModel.SupplyItemsForm;
import org.trc.form.SupplyItemsExt;
import org.trc.form.goods.ExternalItemSkuForm;
import org.trc.form.goods.ItemsExt;
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

    @ApiOperation(value = "商品查询分页查询", response = Skus.class)
    @GET
    @Path(SupplyConstants.Goods.GOODS_SKU_PAGE)
    @Produces(MediaType.APPLICATION_JSON)
    public Response itemsSkusPage(@BeanParam SkusForm form, @BeanParam Pagenation<Skus> page, @Context ContainerRequestContext requestContext) throws Exception {
        //return goodsBiz.itemsSkusPage(form, page, (AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO));
        return ResultUtil.createSuccessPageResult(goodsBiz.itemsSkusPage(form, page, (AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO)));
    }

    @ApiOperation(value = "新增自采商品", response = Response.class)
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
    @ApiOperation(value = "更新自采商品", response = Response.class)
    @PUT
    @Path(SupplyConstants.Goods.GOODS + "/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateGoods(@BeanParam Items items, @BeanParam Skus skus, @BeanParam ItemNaturePropery itemNaturePropery,
                                 @BeanParam ItemSalesPropery itemSalesPropery, @Context ContainerRequestContext requestContext) throws Exception {
        goodsBiz.updateItems(items, skus, itemNaturePropery, itemSalesPropery, (AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO));
        return ResultUtil.createSuccessResult("更新商品成功", "");
    }

    @ApiOperation(value = "自采商品起停用", response = Response.class)
    //V3.1增加数据权限
    @PUT
    @Path(SupplyConstants.Goods.IS_VALID + "/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateValid(@ApiParam(value = "商品主键ID") @PathParam("id") Long id, @ApiParam(value = "0-停用,1-启用") @FormParam("isValid") String isValid, @Context ContainerRequestContext requestContext) throws Exception {
        goodsBiz.updateValid(id, isValid, (AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO));
        String _valid = ZeroToNineEnum.ZERO.getCode();
        if (StringUtils.equals(ZeroToNineEnum.ZERO.getCode(), isValid)) {
            _valid = ZeroToNineEnum.ONE.getCode();
        }
        return ResultUtil.createSuccessResult(String.format("%s成功!", ValidEnum.getValidEnumByCode(_valid).getName()), "");
    }

    //V3.1增加数据权限
    @ApiOperation(value = "自采商品SKU起停用", response = Response.class)
    @PUT
    @Path(SupplyConstants.Goods.SKU_VALID + "/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateSkusValid(@ApiParam(value = "商品主键ID") @PathParam("id") Long id, @ApiParam(value = "商品SPU编码") @FormParam("spuCode") String spuCode, @ApiParam(value = "0-停用,1-启用") @FormParam("isValid") String isValid, @Context ContainerRequestContext requestContext) throws Exception {
        goodsBiz.updateSkusValid(id, spuCode, isValid, (AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO));
        String _valid = ZeroToNineEnum.ZERO.getCode();
        if (StringUtils.equals(ZeroToNineEnum.ZERO.getCode(), isValid)) {
            _valid = ZeroToNineEnum.ONE.getCode();
        }
        return ResultUtil.createSuccessResult(String.format("%s成功!", ValidEnum.getValidEnumByCode(_valid).getName()), "");
    }

    @ApiOperation(value = "查询商品信息", response = ItemsExt.class)
    @GET
    @Path(SupplyConstants.Goods.GOODS_SPU_CODE+"/{spuCode}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response queryItemsInfo(@ApiParam(value = "商品SPU编码") @PathParam("spuCode") String spuCode, @ApiParam(value = "商品SKU编码") @QueryParam("skuCode") String skuCode, @Context ContainerRequestContext requestContext) throws Exception {
        return ResultUtil.createSuccessResult("查询商品信息成功", goodsBiz.queryItemsInfo(spuCode, skuCode, (AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO)));
    }

    @ApiOperation(value = "查询自采商品分类属性", response = CategoryProperty.class)
    @GET
    @Path(SupplyConstants.Goods.ITEMS_CATEGORY_PROPERTY+"/{spuCode}/{categoryId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response queryItemsCategoryProperty(@ApiParam(value = "商品SPU编码") @PathParam("spuCode") String spuCode, @ApiParam(value = "分类ID") @PathParam("categoryId") Long categoryId) throws Exception {
        return ResultUtil.createSuccessResult("查询商品分类属性成功", goodsBiz.queryItemsCategoryProperty(spuCode, categoryId));
    }

    @ApiOperation(value = "代发商品管理分页查询", response = ExternalItemSku.class)
    @GET
    @Path(SupplyConstants.Goods.EXTERNAL_GOODS_PAGE)
    @Produces(MediaType.APPLICATION_JSON)
    public Response externalGoodsPage(@BeanParam ExternalItemSkuForm form, @BeanParam Pagenation<ExternalItemSku> page,@Context ContainerRequestContext requestContext) throws Exception {
        //return goodsBiz.externalGoodsPage(form, page);
        AclUserAccreditInfo aclUserAccreditInfo = (AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO);
        return ResultUtil.createSuccessPageResult(goodsBiz.externalGoodsPage(form, page,aclUserAccreditInfo));
    }

    @ApiOperation(value = "代发供应商代发商品分页查询", response = SupplyItemsExt.class)
    @GET
    @Path(SupplyConstants.Goods.EXTERNAL_GOODS_PAGE_2)
    @Produces(MediaType.APPLICATION_JSON)
    public Response externalGoodsPage2(@BeanParam SupplyItemsForm form, @BeanParam Pagenation<SupplyItemsExt> page, @Context ContainerRequestContext requestContext) throws Exception {
        //return goodsBiz.externalGoodsPage2(form, page);
        return ResultUtil.createSuccessPageResult(goodsBiz.externalGoodsPage2(form, page, (AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO)));
    }

    @ApiOperation(value = "查询代发商品列表", response = ExternalItemSku.class)
    @GET
    @Path(SupplyConstants.Goods.EXTERNAL_ITEM_SKU_LIST)
    @Produces(MediaType.APPLICATION_JSON)
    public Response queryExternalItems(@BeanParam ExternalItemSkuForm form) throws Exception {
        return ResultUtil.createSuccessResult("查询代发商品列表",goodsBiz.queryExternalItems(form));
    }

    @ApiOperation(value = "新增代发商品", response = Response.class)
    @POST
    @Path(SupplyConstants.Goods.EXTERNAL_ITEM_SKU)
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/x-www-form-urlencoded")
    public Response saveExternalItems(@ApiParam(value = "供应商sku信息,参数格式：[{\n" +
            "    \"id\":\"主键ID\",\n" +
            "    \"supplierCode\":\"供应商编码\",\n" +
            "    \"supplyName\":\"供应商名称\",\n" +
            "    \"supplySku\":\"供应商商品Sku\",\n" +
            "    \"upc\":\"商品名称\",\n" +
            "    \"supplierPrice\":\"供应商售价\",\n" +
            "    \"supplyPrice\":\"供货价\",\n" +
            "    \"marketPrice\":\"市场价\",\n" +
            "    \"category\":\"分类名称\",\n" +
            "    \"categoryCode\":\"分类编码\",\n" +
            "    \"brand\":\"品牌\",\n" +
            "    \"skuType\":\"商品类型\",\n" +
            "    \"weight\":\"重量\",\n" +
            "    \"productArea\":\"产地\",\n" +
            "    \"saleUnit\":\"销售单位\",\n" +
            "    \"state\":\"上下架状态:0-下架,1-上架\",\n" +
            "    \"introduction\":\"商品详情文本\",\n" +
            "    \"imagePath\":\"商品主图地址\",\n" +
            "    \"skuName\":\"商品名称\",\n" +
            "    \"detailImagePath\":\"商品明细图片地址\",\n" +
            "    \"isUsed\":\"是否已经使用：0-未使用,1-已使用\",\n" +
            "    \"warehouse\":\"仓库名称\",\n" +
            "    \"stock\":\"库存\",\n" +
            "    \"updateFlag\":\"8\",\n" +
            "    \"notifyTime\":\"同步时间\",\n" +
            "    \"minBuyCount\":\"最小购买量\",\n" +
            "    \"isValid\":\"是否有效:0-否,1-是\"\n" +
            "}]") @FormParam("supplySkus") String supplySkus, @Context ContainerRequestContext requestContext) throws Exception {
        goodsBiz.saveExternalItems(supplySkus, (AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO));
        return ResultUtil.createSuccessResult("新增代发商品成功", "");
    }

    @ApiOperation(value = "代发商品起停用", response = Response.class)
    @PUT
    @Path(SupplyConstants.Goods.EXTERNAL_ITEM__VALID + "/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateExternalItemsValid(@ApiParam(value = "主键ID") @PathParam("id") Long id, @ApiParam(value = "0-停用,1-启用") @FormParam("isValid") String isValid, @Context ContainerRequestContext requestContext) throws Exception {
        goodsBiz.updateExternalItemsValid(id, isValid, (AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO));
        String _valid = ZeroToNineEnum.ZERO.getCode();
        if (StringUtils.equals(ZeroToNineEnum.ZERO.getCode(), isValid)) {
            _valid = ZeroToNineEnum.ONE.getCode();
        }
        return ResultUtil.createSuccessResult(String.format("%s成功!", ValidEnum.getValidEnumByCode(_valid).getName()), "");
    }

    @ApiOperation(value = "更新代发商品", response = Response.class)
    @PUT
    @Path(SupplyConstants.Goods.EXTERNAL_ITEM_SKU + "/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/x-www-form-urlencoded")
    public Response updateExternalItems(@BeanParam ExternalItemSku externalItemSku, @Context ContainerRequestContext requestContext) throws Exception {
        goodsBiz.updateExternalItems(externalItemSku, (AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO));
        return ResultUtil.createSuccessResult("更新代发商品成功", "");
    }

    @ApiOperation(value = "检查属性启停用状态", response = Response.class)
    @GET
    @Path(SupplyConstants.Goods.CHECK_PROPERTY_STATUS)
    @Produces(MediaType.APPLICATION_JSON)
    public Response checkPropetyStatus(@ApiParam(value = "{\n" +
            "    \"naturePropertys\": [{\n" +
                "        \"categoryId\": \"分类ID\",\n" +
                "        \"propertyId\": \"属性ID\",\n" +
                "        \"name\": \"属性名称\"\n" +
                "    }],\n" +
            "    \"purchasPropertys\": [{\n" +
                "        \"categoryId\": \"分类ID\",\n" +
                "        \"propertyId\": \"属性ID\",\n" +
                "        \"name\": \"属性名称\"\n" +
                "    }]\n" +
            "}") @QueryParam("propertyInfo") String propertyInfo) throws Exception {
        goodsBiz.checkPropetyStatus(propertyInfo);
        return ResultUtil.createSuccessResult("检查属性启停用状态成功","");
    }

    @ApiOperation(value = "查询供应商列表", response = Supplier.class)
    @GET
    @Path(SupplyConstants.Goods.SUPPLIERS_LIST)
    @Produces(MediaType.APPLICATION_JSON)
    public Response querySupplierList(@BeanParam SupplierForm form, @Context ContainerRequestContext requestContext) throws Exception {
        return ResultUtil.createSuccessResult("查询供应商列表成功", goodsBiz.querySuppliers(form, (AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO)));

    }

    @ApiOperation(value = "检查条形码是否可用", response = Response.class)
    @POST
    @Path(SupplyConstants.Goods.CHECK_BARCODE_ONLY)
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/x-www-form-urlencoded")
    public Response checkBarcodeOnly(@ApiParam(value = "条形码") @FormParam("barCode") String barCode,@ApiParam(value = "SKU编码") @FormParam("skuCode") String skuCode,
                                     @ApiParam(value = "0-停用,1-启用") @FormParam("isValid") String isValid,@ApiParam(value = "页面选择停用的sku条形码") @FormParam("notIn")String notIn) throws Exception {
        if (StringUtils.equals(isValid,ValidEnum.NOVALID.getCode())){
            return ResultUtil.createSuccessResult("条形码可用", "");
        }else {
            goodsBiz.checkBarcodeOnly(barCode,skuCode,notIn);
            return ResultUtil.createSuccessResult("条形码可用", "");
        }
    }

    /*@ApiOperation(value = "条形码校验", response = Response.class)
    @POST
    @Path(SupplyConstants.Goods.SKU_INFO_BAR)
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/x-www-form-urlencoded")
    public Response skuInfoBarCode(@FormParam("skuInfo") String skuInfo){
        goodsBiz.skuInfoBarCode(skuInfo);
        return ResultUtil.createSuccessResult("条形码可用", "");
    }*/

    @ApiOperation(value = "导出代发商品", response = Response.class)
    @GET
    @Path(SupplyConstants.Goods.EXTERNAL_ITEM_EXPORT)
    @Consumes("text/plain;charset=utf-8")
    @Produces("application/octet-stream")
    public Response exportExternalGoods(@BeanParam ExternalItemSkuForm queryModel, @Context ContainerRequestContext requestContext) {
        return goodsBiz.exportExternalGoods(queryModel, (AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO));
    }

    @ApiOperation(value = "导出自采商品", response = Response.class)
    @GET
    @Path(SupplyConstants.Goods.ITEMS_EXPORT)
    @Consumes("text/plain;charset=utf-8")
    @Produces("application/octet-stream")
    public Response exportItemGoods(@BeanParam SkusForm queryModel, @Context ContainerRequestContext requestContext) {
        return goodsBiz.exportItemGoods(queryModel,(AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO));
    }
}
