package org.trc.biz.goods;

import org.trc.domain.goods.Skus;
import org.trc.form.goods.SkusForm;
import org.trc.util.Pagenation;

import java.util.Map;

/**
 * Created by hzdzf on 2017/5/27.
 */
public interface ISkuBiz {

    Skus findByItemId(long itemId) throws Exception;

    Pagenation<Skus> skusPage(SkusForm form,Pagenation<Skus> page) throws Exception;

}
