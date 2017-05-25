package org.trc.service.impl.goods;

import org.springframework.stereotype.Service;
import org.trc.domain.goods.SkuStock;
import org.trc.service.goods.ISkuStockService;
import org.trc.service.impl.BaseService;

/**
 * Created by hzwdx on 2017/5/24.
 */
@Service("skuStockService")
public class SkuStockService extends BaseService<SkuStock, Long> implements ISkuStockService{

}
