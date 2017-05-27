package org.trc.biz.goods;

import org.trc.domain.goods.Skus;

/**
 * Created by hzdzf on 2017/5/27.
 */
public interface ISkuBiz {

    Skus findByItemId(long itemId) throws Exception;

}
