package org.trc.service.impl.goods;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.trc.domain.goods.ItemSalesPropery;
import org.trc.mapper.goods.IItemSalesProperyMapper;
import org.trc.service.goods.IItemSalesProperyService;
import org.trc.service.impl.BaseService;

import java.util.List;

/**
 * Created by hzwdx on 2017/5/24.
 */
@Service("itemSalesProperyService")
public class ItemSalesProperyService extends BaseService<ItemSalesPropery, Long> implements IItemSalesProperyService{

    @Autowired
    private IItemSalesProperyMapper itemSalesProperyMapper;

    @Override
    public Integer updateItemSalesPropery(List<ItemSalesPropery> itemSalesProperyList) throws Exception {
        return itemSalesProperyMapper.updateItemSalesPropery(itemSalesProperyList);
    }
}
