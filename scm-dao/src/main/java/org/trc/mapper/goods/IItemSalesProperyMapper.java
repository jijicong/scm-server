package org.trc.mapper.goods;

import org.trc.domain.goods.ItemSalesPropery;
import org.trc.util.BaseMapper;

import java.util.List;

/**
 * Created by hzwdx on 2017/5/24.
 */
public interface IItemSalesProperyMapper extends BaseMapper<ItemSalesPropery>{

    Integer updateItemSalesPropery(List<ItemSalesPropery> itemSalesProperyList) throws Exception;

}
