package org.trc.service.goods;

import org.trc.domain.goods.ItemNaturePropery;
import org.trc.service.IBaseService;

import java.util.List;

/**
 * Created by hzwdx on 2017/5/24.
 */
public interface IItemNatureProperyService extends IBaseService<ItemNaturePropery, Long>{

    Integer updateItemNaturePropery(List<ItemNaturePropery> itemNatureProperyList) throws Exception;

}
