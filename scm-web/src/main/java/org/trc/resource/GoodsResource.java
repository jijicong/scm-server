package org.trc.resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.trc.biz.goods.IGoodsBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.category.CategoryProperty;
import org.trc.domain.goods.*;
import org.trc.enums.SuccessFailureEnum;
import org.trc.form.SupplyItemsExt;
import org.trc.form.JDModel.SupplyItemsForm;
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
    public Pagenation<Items> goodsPage(@BeanParam ItemsForm form, @BeanParam Pagenation<Items> page) throws Exception {
        return goodsBiz.itemsPage(form, page);
    }

    @GET
    @Path(SupplyConstants.Goods.GOODS_SKU_PAGE)
    @Produces(MediaType.APPLICATION_JSON)
    public Pagenation<Skus> itemsSkusPage(@BeanParam SkusForm form, @BeanParam Pagenation<Skus> page, @Context ContainerRequestContext requestContext) throws Exception {
        return goodsBiz.itemsSkusPage(form, page, requestContext);
    }

    @POST
    @Path(SupplyConstants.Goods.GOODS)
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/x-www-form-urlencoded")
    public AppResult saveGoods(@BeanParam Items items, @BeanParam Skus skus, @BeanParam ItemNaturePropery itemNaturePropery,
                               @BeanParam ItemSalesPropery itemSalesPropery, @Context ContainerRequestContext requestContext) throws Exception {
        AppResult appResult = ResultUtil.createSucssAppResult("保存商品成功", "");
        try {
            goodsBiz.saveItems(items, skus, itemNaturePropery, itemSalesPropery);
        }catch (Exception e){
            log.error("保存商品异常", e);
            appResult.setAppcode(SuccessFailureEnum.FAILURE.getCode());
            appResult.setDatabuffer(e.getMessage());
        }
        return appResult;
    }

    @PUT
    @Path(SupplyConstants.Goods.GOODS + "/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult updateGoods(@BeanParam Items items, @BeanParam Skus skus, @BeanParam ItemNaturePropery itemNaturePropery,
                                 @BeanParam ItemSalesPropery itemSalesPropery, @Context ContainerRequestContext requestContext) throws Exception {
        AppResult appResult = ResultUtil.createSucssAppResult("更新商品成功", "");
        try {
            goodsBiz.updateItems(items, skus, itemNaturePropery, itemSalesPropery, requestContext);
        }catch (Exception e){
            log.error("更新商品异常", e);
            appResult.setAppcode(SuccessFailureEnum.FAILURE.getCode());
            appResult.setDatabuffer(e.getMessage());
        }
        return appResult;
    }

    @POST
    @Path(SupplyConstants.Goods.IS_VALID + "/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult updateValid(@PathParam("id") Long id, @FormParam("isValid") String isValid, @Context ContainerRequestContext requestContext) throws Exception {
        return goodsBiz.updateValid(id, isValid, requestContext);
    }

    @POST
    @Path(SupplyConstants.Goods.SKU_VALID + "/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult updateSkusValid(@PathParam("id") Long id, @FormParam("spuCode") String spuCode, @FormParam("isValid") String isValid, @Context ContainerRequestContext requestContext) throws Exception {
        goodsBiz.updateSkusValid(id, spuCode, isValid, requestContext);
        return ResultUtil.createSucssAppResult("启停用SKU成功", "");
    }


    @GET
    @Path(SupplyConstants.Goods.GOODS_SPU_CODE+"/{spuCode}")
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult<ItemsExt> queryItemsInfo(@PathParam("spuCode") String spuCode, @QueryParam("skuCode") String skuCode, @Context ContainerRequestContext requestContext) throws Exception {
        return ResultUtil.createSucssAppResult("查询商品信息成功", goodsBiz.queryItemsInfo(spuCode, skuCode, requestContext));
    }

    @GET
    @Path(SupplyConstants.Goods.ITEMS_CATEGORY_PROPERTY+"/{spuCode}/{categoryId}")
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult<List<CategoryProperty>> queryItemsCategoryProperty(@PathParam("spuCode") String spuCode, @PathParam("categoryId") Long categoryId) throws Exception {
        return ResultUtil.createSucssAppResult("查询商品分类属性成功", goodsBiz.queryItemsCategoryProperty(spuCode, categoryId));
    }

    @GET
    @Path(SupplyConstants.Goods.EXTERNAL_GOODS_PAGE)
    @Produces(MediaType.APPLICATION_JSON)
    public Pagenation<ExternalItemSku> externalGoodsPage(@BeanParam ExternalItemSkuForm form, @BeanParam Pagenation<ExternalItemSku> page) throws Exception {
        return goodsBiz.externalGoodsPage(form, page);
    }

    @GET
    @Path(SupplyConstants.Goods.EXTERNAL_GOODS_PAGE_2)
    @Produces(MediaType.APPLICATION_JSON)
    public Pagenation<SupplyItemsExt> externalGoodsPage2(@BeanParam SupplyItemsForm form, @BeanParam Pagenation<SupplyItemsExt> page) throws Exception {
        return goodsBiz.externalGoodsPage2(form, page);
    }

    @GET
    @Path(SupplyConstants.Goods.EXTERNAL_ITEM_SKU_LIST)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult<List<ExternalItemSku>> queryExternalItems(@BeanParam ExternalItemSkuForm form) throws Exception {
        return ResultUtil.createSucssAppResult("查询代发商品列表",goodsBiz.queryExternalItems(form));
    }

    @POST
    @Path(SupplyConstants.Goods.EXTERNAL_ITEM_SKU)
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/x-www-form-urlencoded")
    public AppResult saveExternalItems(@FormParam("supplySkus") String supplySkus, @Context ContainerRequestContext requestContext) throws Exception {
        goodsBiz.saveExternalItems(supplySkus, requestContext);
        return ResultUtil.createSucssAppResult("新增代发商品成功", "");
    }

    @POST
    @Path(SupplyConstants.Goods.EXTERNAL_ITEM__VALID + "/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult updateExternalItemsValid(@PathParam("id") Long id, @FormParam("isValid") String isValid, @Context ContainerRequestContext requestContext) throws Exception {
        goodsBiz.updateExternalItemsValid(id, isValid, requestContext);
        return ResultUtil.createSucssAppResult("启停用商品成功", "");
    }

    @POST
    @Path(SupplyConstants.Goods.EXTERNAL_ITEM_SKU + "/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/x-www-form-urlencoded")
    public AppResult updateExternalItems(@BeanParam ExternalItemSku externalItemSku, @Context ContainerRequestContext requestContext) throws Exception {
        goodsBiz.updateExternalItems(externalItemSku, requestContext);
        return ResultUtil.createSucssAppResult("更新代发商品成功", "");
    }

}
