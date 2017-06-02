package org.trc.biz.goods;

import org.trc.domain.goods.ItemNaturePropery;
import org.trc.domain.goods.ItemSalesPropery;
import org.trc.domain.goods.Items;
import org.trc.domain.goods.Skus;
import org.trc.form.goods.ItemsExt;
import org.trc.form.goods.ItemsForm;
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
    Pagenation<Items> ItemsPage(ItemsForm form, Pagenation<Items> page) throws Exception;

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
     * @param
     * @return
     * @throws Exception
     */
    void updateItems(Items items) throws Exception;

    /**
     * 启用/停用
     * @param isValid
     * @throws Exception
     */
    void updateValid(Long id, String isValid) throws Exception;

    /**
     * 根据supCode查询商品信息
     * @param spuCode
     * @return
     * @throws Exception
     */
    ItemsExt queryItemsInfo(String spuCode) throws Exception;

}
