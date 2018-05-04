package org.trc.service.impl.warehouseInfo;

import org.springframework.stereotype.Service;
import org.trc.domain.warehouseInfo.LogisticsCorporation;
import org.trc.service.impl.BaseService;
import org.trc.service.warehouseInfo.ILogisticsCorporationService;

/**
 * Created by hzcyn on 2018/5/3.
 */
@Service("logisticsCorporationService")
public class LogisticsCorporationService extends BaseService<LogisticsCorporation,Long> implements ILogisticsCorporationService {
}
