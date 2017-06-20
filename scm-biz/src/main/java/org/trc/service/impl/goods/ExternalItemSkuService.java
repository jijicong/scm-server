package org.trc.service.impl.goods;

import org.springframework.stereotype.Service;
import org.trc.domain.goods.ExternalItemSku;
import org.trc.service.goods.IExternalItemSkuService;
import org.trc.service.impl.BaseService;

/**
 * Created by hzwdx on 2017/6/20.
 */
@Service("externalItemSkuService")
public class ExternalItemSkuService  extends BaseService<ExternalItemSku, Long> implements IExternalItemSkuService {
}
