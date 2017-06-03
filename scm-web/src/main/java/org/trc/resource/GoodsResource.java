package org.trc.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.trc.biz.goods.IGoodsBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.dict.DictType;
import org.trc.domain.goods.ItemNaturePropery;
import org.trc.domain.goods.ItemSalesPropery;
import org.trc.domain.goods.Items;
import org.trc.domain.goods.Skus;
import org.trc.form.goods.ItemsExt;
import org.trc.form.goods.ItemsForm;
import org.trc.form.goods.SkusForm;
import org.trc.util.AppResult;
import org.trc.util.Pagenation;
import org.trc.util.ResultUtil;

import javax.ws.rs.*;
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
                               @BeanParam ItemSalesPropery itemSalesPropery) throws Exception {
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




}
