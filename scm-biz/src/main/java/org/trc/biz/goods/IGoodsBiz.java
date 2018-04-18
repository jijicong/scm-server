package org.trc.biz.goods;

import org.trc.domain.category.CategoryProperty;
import org.trc.domain.goods.*;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.supplier.Supplier;
import org.trc.form.JDModel.SupplyItemsForm;
import org.trc.form.SupplyItemsExt;
import org.trc.form.goods.ExternalItemSkuForm;
import org.trc.form.goods.ItemsExt;
import org.trc.form.goods.ItemsForm;
import org.trc.form.goods.SkusForm;
import org.trc.form.supplier.SupplierForm;
import org.trc.util.AppResult;
import org.trc.util.Pagenation;

import java.util.List;

/**
 * Created by hzwdx on 2017/5/24.
 */
public interface IGoodsBiz {

    /**
     * 商品分页查询
     * @param form
     * @return
     * @throws Exception
     */
    Pagenation<Items> itemsPage(ItemsForm form, Pagenation<Items> page) throws Exception;

    /**
     * 商品SKU分页查询
     * @param form
     * @return
     * @throws Exception
     */
    Pagenation<Skus> itemsSkusPage(SkusForm form, Pagenation<Skus> page, AclUserAccreditInfo aclUserAccreditInfo) throws Exception;

    /**
     * 查询商品列表
     * @return
     * @throws Exception
     */
    List<Items> queryItems(ItemsForm itemsForm) throws Exception;

    /**
     * 保存商品
     * @param items
     * @param skus
     * @param itemNaturePropery
     * @param itemSalesPropery
     * @throws Exception
     */
    void saveItems(Items items, Skus skus, ItemNaturePropery itemNaturePropery, ItemSalesPropery itemSalesPropery) throws Exception;

    /**
     * 修改商品
     * @param items
     * @param skus
     * @param itemNaturePropery
     * @param itemSalesPropery
     * @throws Exception
     */
    void updateItems(Items items, Skus skus, ItemNaturePropery itemNaturePropery, ItemSalesPropery itemSalesPropery, AclUserAccreditInfo aclUserAccreditInfo) throws Exception;

    /**
     * 商品启用/停用
     * @param isValid
     * @throws Exception
     */
    AppResult updateValid(Long id, String isValid, AclUserAccreditInfo aclUserAccreditInfo) throws Exception;

    /**
     * SKU启用/停用
     * @param id
     * @param spuCode
     * @param isValid
     * @throws Exception
     */
    void updateSkusValid(Long id, String spuCode, String isValid, AclUserAccreditInfo aclUserAccreditInfo) throws Exception;

    /**
     * 根据supCode查询商品信息
     * @param spuCode
     * @return
     * @throws Exception
     */
    ItemsExt queryItemsInfo(String spuCode, String skuCode, AclUserAccreditInfo aclUserAccreditInfo) throws Exception;

    /**
     * 查询商品分类属性
     * @param spuCode
     * @param categoryId
     * @return
     * @throws Exception
     */
    List<CategoryProperty> queryItemsCategoryProperty(String spuCode, Long categoryId) throws Exception;

    /**
     * 代发商品分页查询
     * @param form
     * @return
     * @throws Exception
     */
    Pagenation<ExternalItemSku> externalGoodsPage(ExternalItemSkuForm form, Pagenation<ExternalItemSku> page,AclUserAccreditInfo aclUserAccreditInfo) throws Exception;

    /**
     *查询代发商品
     * @param form
     * @return
     */
    List<ExternalItemSku> queryExternalItems(ExternalItemSkuForm form);

    /**
     * 代发商品分页查询
     * @param form
     * @return
     * @throws Exception
     */
    Pagenation<SupplyItemsExt> externalGoodsPage2(SupplyItemsForm form, Pagenation<SupplyItemsExt> page,AclUserAccreditInfo aclUserAccreditInfo) throws Exception;

    /**
     * 新增一件代发商品
     * @param supplySkus
     */
    void saveExternalItems(String supplySkus, AclUserAccreditInfo aclUserAccreditInfo);

    /**
     * 一件代发商品启用/停用
     * @param isValid
     * @throws Exception
     */
    void updateExternalItemsValid(Long id, String isValid, AclUserAccreditInfo aclUserAccreditInfo) throws Exception;

    /**
     * 更新一件代发商品
     * @param externalItemSku
     */
    void updateExternalItems(ExternalItemSku externalItemSku, AclUserAccreditInfo aclUserAccreditInfo);

    /**
     * 供应商sku更新通知
     * @param updateSupplierSkus 更新的供应商sku信息，json数组格式字符串
     */
    void supplierSkuUpdateNotice(String updateSupplierSkus);

    /**
     * 检查属性状态
     * @param propertyInfo
     */
    void checkPropetyStatus(String propertyInfo);

    /**
     * 查询供应商列表
     * @return
     * @throws Exception
     */
    List<Supplier> querySuppliers(SupplierForm SupplierForm,AclUserAccreditInfo aclUserAccreditInfo) throws Exception;

    /**
     * 检查条形码唯一性
     * @param barcode
     */
    void checkBarcodeOnly(String barcode,String skuCode,String notIn);

    void skuInfoBarCode(String skuInfo);
}
