package org.trc.biz.goods;

import org.trc.domain.goods.Items;
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
     * @return
     */
    void saveItems(Items items) throws Exception;

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

}
