package org.trc.mapper.goods;

import org.trc.domain.goods.ItemNaturePropery;
import org.trc.util.BaseMapper;

import java.util.List;

/**
 * Created by hzwdx on 2017/5/24.
 */
public interface IItemNatureProperyMapper extends BaseMapper<ItemNaturePropery>{

    Integer updateItemNaturePropery(List<ItemNaturePropery> itemNatureProperyList) throws Exception;

}
