package org.trc.biz.impl.goods;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.trc.biz.goods.IItemSalesProperyBiz;
import org.trc.domain.goods.ItemSalesPropery;
import org.trc.service.goods.IItemSalesProperyService;

import javax.annotation.Resource;

/**
 * Created by hzdzf on 2017/5/27.
 */
@Service("itemSalesProperyBiz")
public class ItemSalesProperyBiz implements IItemSalesProperyBiz {

    private static final Logger logger = LoggerFactory.getLogger(ItemSalesProperyBiz.class);

    @Resource
    private IItemSalesProperyService itemSalesProperyService;

    @Override
    public ItemSalesPropery findByItemId(long itemId) throws Exception {
        ItemSalesPropery itemSalesPropery = new ItemSalesPropery();
        itemSalesPropery.setItemId(itemId);
        return itemSalesProperyService.selectOne(itemSalesPropery);
    }
}
