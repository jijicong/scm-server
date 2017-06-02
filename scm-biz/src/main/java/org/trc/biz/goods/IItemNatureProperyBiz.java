package org.trc.biz.goods;

import org.trc.domain.goods.ItemNaturePropery;

/**
 * Created by hzdzf on 2017/5/27.
 */
public interface IItemNatureProperyBiz {

    ItemNaturePropery findByItemId(long itemId) throws Exception;
}
