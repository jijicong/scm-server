package org.trc.service.impl.warehouseInfo;

import org.springframework.stereotype.Service;
import org.trc.domain.warehouseInfo.WarehouseInfo;
import org.trc.domain.warehouseInfo.WarehousePriority;
import org.trc.service.impl.BaseService;
import org.trc.service.warehouseInfo.IWarehouseInfoService;
import org.trc.service.warehouseInfo.IWarehousePriorityService;

/**
 * Created by wangyz on 2017/11/15.
 */
@Service("warehousePriorityService")
public class WarehousePriorityServiceImpl extends BaseService<WarehousePriority,Long> implements IWarehousePriorityService {
}
