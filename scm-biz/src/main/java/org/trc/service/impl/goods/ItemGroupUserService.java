package org.trc.service.impl.goods;

import org.springframework.stereotype.Service;
import org.trc.domain.goods.ItemGroupUser;
import org.trc.service.goods.IItemGroupUserService;
import org.trc.service.impl.BaseService;

/**
 * Created by hzgjl on 2018/7/27.
 */
@Service("ItemGroupUserService")
public class ItemGroupUserService extends BaseService<ItemGroupUser,Long> implements IItemGroupUserService{
}
