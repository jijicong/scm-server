package org.trc.biz.goods;

import org.trc.domain.goods.ItemSalesPropery;

/**
 * Created by hzdzf on 2017/5/27.
 */
public interface IItemSalesProperyBiz {

    ItemSalesPropery findByItemId(long itemId) throws Exception;
}
