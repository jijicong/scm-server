package org.trc.service.impl.purchase;

import org.springframework.stereotype.Service;
import org.trc.domain.purchase.WarehouseNotice;
import org.trc.service.IBaseService;
import org.trc.service.impl.BaseService;
import org.trc.service.purchase.IWarehouseNoticeService;

/**
 * Created by sone on 2017/7/10.
 */
@Service("warehouseNoticeService")
public class WarehouseNoticeService extends BaseService<WarehouseNotice,Long> implements IWarehouseNoticeService{

}
