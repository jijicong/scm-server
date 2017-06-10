package org.trc.service.goods;

import org.apache.ibatis.annotations.Param;
import org.trc.domain.goods.ItemNaturePropery;
import org.trc.service.IBaseService;

import java.util.List;

/**
 * Created by hzwdx on 2017/5/24.
 */
public interface IItemNatureProperyService extends IBaseService<ItemNaturePropery, Long> {

    Integer updateItemNaturePropery(List<ItemNaturePropery> itemNatureProperyList) throws Exception;

    Integer updateIsValidByPropertyValueId(String isValid, Long propertyValueId) throws Exception;

    Integer updateIsValidByPropertyId(String isValid, Long propertyId) throws Exception;
}
