package org.trc.biz.impl.goods;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.trc.biz.goods.IItemNatureProperyBiz;
import org.trc.domain.goods.ItemNaturePropery;
import org.trc.service.goods.IItemNatureProperyService;

import javax.annotation.Resource;

/**
 * Created by hzdzf on 2017/5/27.
 */
@Service("itemNatureProperyBiz")
public class ItemNatureProperyBiz implements IItemNatureProperyBiz {


    private static final Logger logger = LoggerFactory.getLogger(ItemNatureProperyBiz.class);

    @Resource
    private IItemNatureProperyService itemNatureProperyService;

    @Override
    public ItemNaturePropery findByItemId(long itemId) throws Exception {
        ItemNaturePropery itemNaturePropery = new ItemNaturePropery();
        itemNaturePropery.setItemId(itemId);
        return itemNatureProperyService.selectOne(itemNaturePropery);
    }
}
