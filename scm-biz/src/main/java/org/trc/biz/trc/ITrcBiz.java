package org.trc.biz.trc;

import com.alibaba.fastjson.JSONArray;
import org.trc.domain.category.*;
import org.trc.domain.goods.*;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.supplier.Supplier;
import org.trc.enums.TrcActionTypeEnum;
import org.trc.form.category.BrandForm;
import org.trc.form.category.CategoryForm;
import org.trc.form.goods.ExternalItemSkuForm;
import org.trc.form.goods.ItemsForm;
import org.trc.form.goods.SkusForm;
import org.trc.form.supplier.SupplierForm;
import org.trc.form.trc.BrandForm2;
import org.trc.form.trc.CategoryForm2;
import org.trc.form.trc.ItemsForm2;
import org.trc.form.trcForm.PropertyFormForTrc;
import org.trc.model.ToGlyResultDO;
import org.trc.util.Pagenation;

import javax.ws.rs.BeanParam;
import java.util.Date;
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
    ToGlyResultDO sendBrand(TrcActionTypeEnum action, Brand oldBrand, Brand brand, long operateTime) throws Exception;

    /**
     * @param action      行为
     * @param oldProperty 旧属性信息
     * @param property    属性信息
     * @param oldValueList 旧属性值信息
     * @param valueList   修改后属性值信息
     * @param operateTime 时间戳
     * @return 渠道调回信息
     * @throws Exception
     */
    ToGlyResultDO sendProperty(TrcActionTypeEnum action, Property oldProperty, Property property, List<PropertyValue> oldValueList, List<PropertyValue> valueList, long operateTime) throws Exception;

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
    ToGlyResultDO sendCategory(TrcActionTypeEnum action, Category oldCategory, Category category,
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
    ToGlyResultDO sendItem(TrcActionTypeEnum action, Items items, List<ItemNaturePropery> itemNaturePropery, List<ItemSalesPropery> itemSalesPropery, List<Skus> skus, Long operateTime) throws Exception;


    /**
     * 一件代发商品的sku变更信息
     *
     * @param action                 行为
     * @param oldExternalItemSkuList 旧的sku列表
     * @param externalItemSkuList    更新的sku列表
     * @param operateTime            时间戳
     * @return
     */
    ToGlyResultDO sendExternalItemSkuUpdation(TrcActionTypeEnum action, List<ExternalItemSku> oldExternalItemSkuList, List<ExternalItemSku> externalItemSkuList, Long operateTime) throws Exception;

    /**
     * 通知物流信息
     *
     * @param action                   行为
     * @param channelPlatformOrderCode 平台订单号  对应泰然成orderId
     * @param channelShopOrderCode     渠道店铺订单号  对应泰然成shopOrderId
     * @param supplierCode             供应商编号
     * @param jdLogistic               京东物流信息
     * @param waybillNumbers           粮油运单号
     * @return
     */
    ToGlyResultDO sendLogistic(TrcActionTypeEnum action, String channelPlatformOrderCode, String channelShopOrderCode, String supplierCode,
                               JSONArray jdLogistic, JSONArray waybillNumbers) throws Exception;

    //添加流水
    void addRequestFlow(String requester, String responder, String type, String requestNum, String status, String requestParam, String responseParam, Date requestTime, String remark) throws Exception;

    /**
     *
     */
    Pagenation<ExternalItemSku> externalItemSkuPage(ExternalItemSkuForm queryModel, Pagenation<ExternalItemSku> page) throws Exception;

    void updateRelation(String action, JSONArray relations) throws Exception;

    /**
     * 供应商分页查询
     * @param page
     * @param queryModel
     * @return
     * @throws Exception
     */
    Pagenation<Supplier> supplierPage(SupplierForm queryModel, Pagenation<Supplier> page) throws Exception;

    /**
     * 自采商品SKU查询
     * @param form
     * @param page
     * @return
     */
    Pagenation<Skus> skusPage(SkusForm form, Pagenation<Skus> page);

    /**
     * 自采商品分页查询
     * @param form
     * @param page
     * @return
     * @throws Exception
     */
    Pagenation<Items> itemsPage(ItemsForm2 form, Pagenation<Items> page);

    /**
     * for channel's propertyPage
     * @param form
     * @param page
     * @return
     * @throws Exception
     */
    Pagenation<Property> propertyPage(PropertyFormForTrc form,  Pagenation<Property> page) throws Exception;

    /**
     * 对泰然城提供分页
     *
     * @param form
     * @param page
     * @return
     * @throws Exception
     */
    Pagenation<Brand> brandList(BrandForm2 form, Pagenation<Brand> page) throws Exception;

    /**
     * 分类分页查询
     *
     * @param queryModel
     * @param page
     * @return
     * @throws Exception
     */
    Pagenation<Category> categoryPage(CategoryForm2 queryModel, Pagenation<Category> page) throws Exception;

}
