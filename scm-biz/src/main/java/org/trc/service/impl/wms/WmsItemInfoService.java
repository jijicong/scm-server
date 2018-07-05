package org.trc.service.impl.wms;

import org.springframework.stereotype.Service;
import org.trc.domain.wms.WmsItemInfo;
import org.trc.service.impl.BaseService;
import org.trc.service.wms.IWmsItemInfoService;

/**
 * Created by hzcyn on 2018/5/30.
 */
@Service("wmsItemInfoService")
public class WmsItemInfoService extends BaseService<WmsItemInfo,Long> implements IWmsItemInfoService{
}
