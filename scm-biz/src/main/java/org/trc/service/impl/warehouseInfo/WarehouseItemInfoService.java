package org.trc.service.impl.warehouseInfo;

import org.springframework.stereotype.Service;
import org.trc.domain.warehouseInfo.WarehouseItemInfo;
import org.trc.service.impl.BaseService;
import org.trc.service.warehouseInfo.IWarehouseItemInfoService;

/**
 * Created by hzcyn on 2017/11/16.
 *
 * @author hzcyn
 */
@Service("warehouseItemInfoService")
public class WarehouseItemInfoService extends BaseService<WarehouseItemInfo, Long> implements IWarehouseItemInfoService {
}
