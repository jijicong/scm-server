package org.trc.biz.impl.goods;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.trc.biz.goods.ISkuBiz;
import org.trc.domain.goods.Skus;
import org.trc.service.goods.ISkusService;

import javax.annotation.Resource;

/**
 * Created by hzdzf on 2017/5/27.
 */
@Service("skuBiz")
public class SkuBiz implements ISkuBiz {

    private static final Logger logger = LoggerFactory.getLogger(SkuBiz.class);

    @Resource
    private ISkusService skusService;

    @Override
    public Skus findByItemId(long itemId) throws Exception {
        Skus skus = new Skus();
        skus.setItemId(itemId);
        return skusService.selectOne(skus);
    }
}
