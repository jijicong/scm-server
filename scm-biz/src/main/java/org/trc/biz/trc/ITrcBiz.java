package org.trc.biz.trc;

import org.trc.domain.category.*;
import org.trc.domain.goods.*;
import org.trc.enums.TrcActionTypeEnum;
import org.trc.model.ResultModel;

import java.util.List;

/**
 * 泰然城交互
 * Created by hzdzf on 2017/6/7.
 */
public interface ITrcBiz {

    /**
     * @param action      行为
     * @param oldBrand    旧品牌信息
     * @param brand       品牌信息
     * @param operateTime 时间戳
     * @return 渠道调回信息
     */
    ResultModel sendBrand(TrcActionTypeEnum action, Brand oldBrand, Brand brand, long operateTime) throws Exception;

    /**
     * @param action      行为
     * @param oldProperty 旧属性信息
     * @param property    属性信息
     * @param valueList   修改后属性值信息
     * @param operateTime 时间戳
     * @return 渠道调回信息
     * @throws Exception
     */
    ResultModel sendProperty(TrcActionTypeEnum action, Property oldProperty, Property property, List<PropertyValue> valueList, long operateTime) throws Exception;

    /**
     * @param action               行为
     * @param oldCategory          旧分类信息
     * @param category             分类信息
     * @param categoryBrandList    分类品牌列表信息
     * @param categoryPropertyList 分类属性列表信息
     * @param operateTime          时间戳
     * @return 渠道调回信息
     * @throws Exception
     */
    ResultModel sendCategory(TrcActionTypeEnum action, Category oldCategory, Category category,
                             List<CategoryBrand> categoryBrandList, List<CategoryProperty> categoryPropertyList, long operateTime) throws Exception;


    /**
     * 通知商品变更信息
     *
     * @param action            行为
     * @param items             商品信息
     * @param itemNaturePropery 自然属性信息
     * @param itemSalesPropery  采购属性信息
     * @param skus              规格信息
     * @param operateTime       时间戳
     * @return
     * @throws Exception
     */
    ResultModel sendItem(TrcActionTypeEnum action, Items items, ItemNaturePropery itemNaturePropery, ItemSalesPropery itemSalesPropery, Skus skus, Long operateTime) throws Exception;


    /**
     * 一件代发商品的sku变更信息
     *
     * @param action                 行为
     * @param oldExternalItemSkuList 旧的sku列表
     * @param externalItemSkuList    更新的sku列表
     * @param operateTime            时间戳
     * @return
     */
    ResultModel sendExternalItemSkuUpdation(TrcActionTypeEnum action, List<ExternalItemSku> oldExternalItemSkuList, List<ExternalItemSku> externalItemSkuList, Long operateTime) throws Exception;
}
