package org.trc.service.impl.goods;

import org.springframework.stereotype.Service;
import org.trc.domain.goods.ExternalItemSku;
import org.trc.service.goods.IExternalItemSkuService;
import org.trc.service.impl.BaseService;

/**
 * @author: Ding
 * @mail: hzdzf@tairanchina.com
 * @create: 2017-06-20 14:24
 */
@Service("externalItemSkuService")
public class ExternalItemSkuService extends BaseService<ExternalItemSku,Long> implements IExternalItemSkuService {
}
