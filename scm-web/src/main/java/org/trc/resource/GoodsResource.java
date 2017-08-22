package org.trc.resource;

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
import org.trc.enums.SuccessFailureEnum;
import org.trc.enums.ValidEnum;
import org.trc.enums.ZeroToNineEnum;
import org.trc.form.JDModel.SupplyItemsForm;
import org.trc.form.SupplyItemsExt;
import org.trc.form.goods.ExternalItemSkuForm;
import org.trc.form.goods.ItemsExt;
import org.trc.form.goods.ItemsForm;
import org.trc.form.goods.SkusForm;
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
 * Created by hzwdx on 2017/5/24.
 */
@Component
@Path(SupplyConstants.Goods.ROOT)
public class GoodsResource {

    private Logger log = LoggerFactory.getLogger(GoodsResource.class);

    @Autowired
    private IGoodsBiz goodsBiz;

    @GET
    @Path(SupplyConstants.Goods.GOODS_PAGE)
    @Produces(MediaType.APPLICATION_JSON)
    public Response goodsPage(@BeanParam ItemsForm form, @BeanParam Pagenation<Items> page) throws Exception {
        //return goodsBiz.itemsPage(form, page);
        return ResultUtil.createSuccessPageResult(goodsBiz.itemsPage(form, page));
    }

    @GET
    @Path(SupplyConstants.Goods.GOODS_SKU_PAGE)
    @Produces(MediaType.APPLICATION_JSON)
    public Response itemsSkusPage(@BeanParam SkusForm form, @BeanParam Pagenation<Skus> page, @Context ContainerRequestContext requestContext) throws Exception {
        //return goodsBiz.itemsSkusPage(form, page, (AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO));
        return ResultUtil.createSuccessPageResult(goodsBiz.itemsSkusPage(form, page, (AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO)));
    }

    @POST
    @Path(SupplyConstants.Goods.GOODS)
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/x-www-form-urlencoded")
    public Response saveGoods(@BeanParam Items items, @BeanParam Skus skus, @BeanParam ItemNaturePropery itemNaturePropery,
                              @BeanParam ItemSalesPropery itemSalesPropery, @Context ContainerRequestContext requestContext) throws Exception {
        goodsBiz.saveItems(items, skus, itemNaturePropery, itemSalesPropery);
        return ResultUtil.createSuccessResult("保存商品成功", "");
    }

    @PUT
    @Path(SupplyConstants.Goods.GOODS + "/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateGoods(@BeanParam Items items, @BeanParam Skus skus, @BeanParam ItemNaturePropery itemNaturePropery,
                                 @BeanParam ItemSalesPropery itemSalesPropery, @Context ContainerRequestContext requestContext) throws Exception {
        goodsBiz.updateItems(items, skus, itemNaturePropery, itemSalesPropery, (AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO));
        return ResultUtil.createSuccessResult("更新商品成功", "");
    }

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

    @GET
    @Path(SupplyConstants.Goods.GOODS_SPU_CODE+"/{spuCode}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response queryItemsInfo(@PathParam("spuCode") String spuCode, @QueryParam("skuCode") String skuCode, @Context ContainerRequestContext requestContext) throws Exception {
        return ResultUtil.createSuccessResult("查询商品信息成功", goodsBiz.queryItemsInfo(spuCode, skuCode, (AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO)));
    }

    @GET
    @Path(SupplyConstants.Goods.ITEMS_CATEGORY_PROPERTY+"/{spuCode}/{categoryId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response queryItemsCategoryProperty(@PathParam("spuCode") String spuCode, @PathParam("categoryId") Long categoryId) throws Exception {
        return ResultUtil.createSuccessResult("查询商品分类属性成功", goodsBiz.queryItemsCategoryProperty(spuCode, categoryId));
    }

    @GET
    @Path(SupplyConstants.Goods.EXTERNAL_GOODS_PAGE)
    @Produces(MediaType.APPLICATION_JSON)
    public Response externalGoodsPage(@BeanParam ExternalItemSkuForm form, @BeanParam Pagenation<ExternalItemSku> page) throws Exception {
        //return goodsBiz.externalGoodsPage(form, page);
        return ResultUtil.createSuccessPageResult(goodsBiz.externalGoodsPage(form, page));
    }

    @GET
    @Path(SupplyConstants.Goods.EXTERNAL_GOODS_PAGE_2)
    @Produces(MediaType.APPLICATION_JSON)
    public Response externalGoodsPage2(@BeanParam SupplyItemsForm form, @BeanParam Pagenation<SupplyItemsExt> page) throws Exception {
        //return goodsBiz.externalGoodsPage2(form, page);
        return ResultUtil.createSuccessPageResult(goodsBiz.externalGoodsPage2(form, page));
    }

    @GET
    @Path(SupplyConstants.Goods.EXTERNAL_ITEM_SKU_LIST)
    @Produces(MediaType.APPLICATION_JSON)
    public Response queryExternalItems(@BeanParam ExternalItemSkuForm form) throws Exception {
        return ResultUtil.createSuccessResult("查询代发商品列表",goodsBiz.queryExternalItems(form));
    }

    @POST
    @Path(SupplyConstants.Goods.EXTERNAL_ITEM_SKU)
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/x-www-form-urlencoded")
    public Response saveExternalItems(@FormParam("supplySkus") String supplySkus, @Context ContainerRequestContext requestContext) throws Exception {
        goodsBiz.saveExternalItems(supplySkus, (AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO));
        return ResultUtil.createSuccessResult("新增代发商品成功", "");
    }

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

    @PUT
    @Path(SupplyConstants.Goods.EXTERNAL_ITEM_SKU + "/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/x-www-form-urlencoded")
    public Response updateExternalItems(@BeanParam ExternalItemSku externalItemSku, @Context ContainerRequestContext requestContext) throws Exception {
        goodsBiz.updateExternalItems(externalItemSku, (AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO));
        return ResultUtil.createSuccessResult("更新代发商品成功", "");
    }

}
