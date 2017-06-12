package org.trc.service.impl.goods;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.trc.domain.goods.ItemNaturePropery;
import org.trc.mapper.goods.IItemNatureProperyMapper;
import org.trc.service.goods.IItemNatureProperyService;
import org.trc.service.impl.BaseService;

import java.util.List;

/**
 * Created by hzwdx on 2017/5/24.
 */
@Service("itemNatureProperyService")
public class ItemNatureProperyService extends BaseService<ItemNaturePropery, Long> implements IItemNatureProperyService{

    @Autowired
    private IItemNatureProperyMapper itemNatureProperyMapper;

    @Override
    public Integer updateItemNaturePropery(List<ItemNaturePropery> itemNatureProperyList) throws Exception {
        return itemNatureProperyMapper.updateItemNaturePropery(itemNatureProperyList);
    }

    @Override
    public Integer updateIsValidByPropertyValueId(String isValid, Long propertyValueId) throws Exception {
        return itemNatureProperyMapper.updateIsValidByPropertyValueId(isValid,propertyValueId);
    }

    @Override
    public Integer updateIsValidByPropertyId(String isValid, Long propertyId) throws Exception {
        return itemNatureProperyMapper.updateIsValidByPropertyId(isValid,propertyId);
    }
}
