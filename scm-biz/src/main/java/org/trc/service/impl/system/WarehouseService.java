package org.trc.service.impl.system;

import org.springframework.stereotype.Service;
import org.trc.domain.System.Warehouse;
import org.trc.service.System.IWarehouseService;
import org.trc.service.impl.BaseService;

/**
 * Created by sone on 2017/5/4.
 */
@Service("warehouseService")
public class WarehouseService extends BaseService<Warehouse,Long> implements IWarehouseService{

}
