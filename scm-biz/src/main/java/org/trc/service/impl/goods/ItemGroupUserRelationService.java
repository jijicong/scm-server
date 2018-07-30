package org.trc.service.impl.goods;

import org.springframework.stereotype.Service;
import org.trc.domain.goods.ItemGroupUserRelation;
import org.trc.service.goods.IItemGroupUserRelationService;
import org.trc.service.impl.BaseService;

/**
 * Created by hzgjl on 2018/7/27.
 */
@Service("ItemGroupUserRelationService")
public class ItemGroupUserRelationService extends BaseService<ItemGroupUserRelation,Long> implements IItemGroupUserRelationService{
}
