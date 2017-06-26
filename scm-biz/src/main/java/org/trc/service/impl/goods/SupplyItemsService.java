package org.trc.service.impl.goods;

import org.springframework.stereotype.Service;
import org.trc.domain.goods.SupplyItems;
import org.trc.service.goods.ISupplyItemsService;
import org.trc.service.impl.BaseService;

/**
 * Created by hzwdx on 2017/6/20.
 */
@Service("supplyItemsService")
public class SupplyItemsService extends BaseService<SupplyItems, Long> implements ISupplyItemsService{
}
