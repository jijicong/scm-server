package org.trc.service.impl.goods;

import org.springframework.stereotype.Service;
import org.trc.domain.goods.BusiItems;
import org.trc.domain.goods.Items;
import org.trc.service.goods.IBusiItemsService;
import org.trc.service.goods.IItemsService;
import org.trc.service.impl.BaseService;

/**
 * Created by hzwdx on 2017/5/24.
 */
@Service("busiItemsService")
public class BusiItemsService extends BaseService<BusiItems, String> implements IBusiItemsService {

}

