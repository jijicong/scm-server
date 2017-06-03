package org.trc.service.goods;

import org.trc.domain.goods.ItemSalesPropery;
import org.trc.service.IBaseService;

import java.util.List;

/**
 * Created by hzwdx on 2017/5/24.
 */
public interface IItemSalesProperyService extends IBaseService<ItemSalesPropery, Long>{

    Integer updateItemSalesPropery(List<ItemSalesPropery> itemSalesProperyList) throws Exception;

}
