package org.trc.service.impl.tairan;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.trc.domain.goods.Items;
import org.trc.service.tairan.TItemService;
import org.trc.util.GuidUtil;


/**
 * Created by hzdzf on 2017/5/26.
 */
public class TItemServiceImpl implements TItemService {

    private static final Logger logger = LoggerFactory.getLogger(TItemServiceImpl.class);

   /* @Resource
    private ISkuBiz skuBiz;

    @Resource
    private IItemNatureProperyBiz itemNatureProperyBiz;

    @Resource
    private IItemSalesProperyBiz itemSalesProperyBiz;*/

   @Transactional
    @Override
    public String sendItemNotice(Items items, long operateTime, String action) throws Exception {
        logger.info("商品信息----" + items);
        //查询出三块信息表
       /* Skus skus = skuBiz.findByItemId(items.getId());
        ItemNaturePropery  itemNaturePropery = itemNatureProperyBiz.findByItemId(items.getId());
        ItemSalesPropery itemSalesPropery = itemSalesProperyBiz.findByItemId(items.getId());*/

        //传值处理
        String noticeNum = GuidUtil.getNextUid(action + "_");

        return null;
    }
}
