package org.trc.service.impl.trc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trc.biz.goods.IItemNatureProperyBiz;
import org.trc.biz.goods.IItemSalesProperyBiz;
import org.trc.biz.goods.ISkuBiz;
import org.trc.domain.goods.ItemNaturePropery;
import org.trc.domain.goods.ItemSalesPropery;
import org.trc.domain.goods.Items;
import org.trc.domain.goods.Skus;
import org.trc.service.trc.TItemService;
import org.trc.util.GuidUtil;

import javax.annotation.Resource;

/**
 * Created by hzdzf on 2017/5/26.
 */
public class TItemServiceImpl implements TItemService {

    private static final Logger logger = LoggerFactory.getLogger(TItemServiceImpl.class);

    @Resource
    private ISkuBiz skuBiz;

    @Resource
    private IItemNatureProperyBiz itemNatureProperyBiz;

    @Resource
    private IItemSalesProperyBiz itemSalesProperyBiz;

    @Override
    public String sendItemNotice(Items items,long operateTime,String action) throws Exception {
        logger.info("商品信息----"+items);
        Skus skus = skuBiz.findByItemId(items.getId());
        ItemNaturePropery  itemNaturePropery = itemNatureProperyBiz.findByItemId(items.getId());
        ItemSalesPropery itemSalesPropery = itemSalesProperyBiz.findByItemId(items.getId());

        //传值处理
        String noticeNum = GuidUtil.getNextUid(action + "_");

        return null;
    }
}
