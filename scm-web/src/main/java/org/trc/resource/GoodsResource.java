package org.trc.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.trc.biz.goods.IGoodsBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.category.CategoryProperty;
import org.trc.domain.dict.DictType;
import org.trc.domain.goods.*;
import org.trc.form.goods.*;
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

    @Autowired
    private IGoodsBiz goodsBiz;

    @GET
    @Path(SupplyConstants.Goods.GOODS_PAGE)
    @Produces(MediaType.APPLICATION_JSON)
    public Pagenation<Items> goodsPage(@BeanParam ItemsForm form, @BeanParam Pagenation<Items> page) throws Exception {
        return goodsBiz.itemsPage(form, page);
    }

    @POST
    @Path(SupplyConstants.Goods.GOODS)
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/x-www-form-urlencoded")
    public AppResult saveGoods(@BeanParam Items items, @BeanParam Skus skus, @BeanParam ItemNaturePropery itemNaturePropery,
                               @BeanParam ItemSalesPropery itemSalesPropery, @Context ContainerRequestContext requestContext) throws Exception {
        goodsBiz.saveItems(items, skus, itemNaturePropery, itemSalesPropery);
        return ResultUtil.createSucssAppResult("保存商品成功", "");
    }

    @PUT
    @Path(SupplyConstants.Goods.GOODS + "/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult updateGoods(@BeanParam Items items, @BeanParam Skus skus, @BeanParam ItemNaturePropery itemNaturePropery,
                                 @BeanParam ItemSalesPropery itemSalesPropery) throws Exception {
        goodsBiz.updateItems(items, skus, itemNaturePropery, itemSalesPropery);
        return ResultUtil.createSucssAppResult("编辑商品成功", "");
    }

    @POST
    @Path(SupplyConstants.Goods.IS_VALID + "/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult updateValid(@PathParam("id") Long id, @FormParam("isValid") String isValid) throws Exception {
        goodsBiz.updateValid(id, isValid);
        return ResultUtil.createSucssAppResult("启停用商品成功", "");
    }

    @POST
    @Path(SupplyConstants.Goods.SKU_VALID + "/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult updateSkusValid(@PathParam("id") Long id, @FormParam("spuCode") String spuCode, @FormParam("isValid") String isValid) throws Exception {
        goodsBiz.updateSkusValid(id, spuCode, isValid);
        return ResultUtil.createSucssAppResult("启停用SKU成功", "");
    }


    @GET
    @Path(SupplyConstants.Goods.GOODS_SPU_CODE+"/{spuCode}")
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult<ItemsExt> queryItemsInfo(@PathParam("spuCode") String spuCode) throws Exception {
        return ResultUtil.createSucssAppResult("查询商品信息成功", goodsBiz.queryItemsInfo(spuCode));
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
    public Pagenation<SupplyItems> externalGoodsPage2(@BeanParam SupplyItemsForm form, @BeanParam Pagenation<SupplyItems> page) throws Exception {
        return goodsBiz.externalGoodsPage2(form, page);
    }

    @POST
    @Path(SupplyConstants.Goods.EXTERNAL_ITEM_SKU)
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/x-www-form-urlencoded")
    public AppResult saveExternalItems(@FormParam("supplySkus") String supplySkus) throws Exception {
        goodsBiz.saveExternalItems(supplySkus);
        return ResultUtil.createSucssAppResult("新增代发商品成功", "");
    }

    @POST
    @Path(SupplyConstants.Goods.EXTERNAL_ITEM__VALID + "/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult updateExternalItemsValid(@PathParam("id") Long id, @FormParam("isValid") String isValid) throws Exception {
        goodsBiz.updateExternalItemsValid(id, isValid);
        return ResultUtil.createSucssAppResult("启停用商品成功", "");
    }

}
