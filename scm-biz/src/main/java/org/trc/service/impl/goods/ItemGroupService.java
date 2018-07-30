package org.trc.service.impl.goods;

import org.springframework.stereotype.Service;
import org.trc.domain.goods.ItemGroup;
import org.trc.service.goods.IItemGroupService;
import org.trc.service.impl.BaseService;

/**
 * Created by hzgjl on 2018/7/26.
 */
@Service("itemGroupService")
public class ItemGroupService extends BaseService<ItemGroup,Long> implements IItemGroupService {
}
