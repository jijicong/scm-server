package org.trc.service.impl.goods;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.trc.domain.goods.Items;
import org.trc.mapper.goods.IItemsMapper;
import org.trc.service.goods.IItemsService;
import org.trc.service.impl.BaseService;

/**
 * Created by hzwdx on 2017/5/24.
 */
@Service("itemsService")
public class ItemsService extends BaseService<Items, Long> implements IItemsService{

    @Autowired
    private IItemsMapper iItemsMapper;

    @Override
    public Items selectOneBySpuCode(String spuCode) {
        Items items = new Items();
        items.setSpuCode(spuCode);
        return iItemsMapper.selectOne(items);
    }
}

