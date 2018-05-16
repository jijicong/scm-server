package org.trc.service.impl.order;

import org.springframework.stereotype.Service;
import org.trc.domain.order.ImportOrderInfo;
import org.trc.domain.order.OrderItem;
import org.trc.service.impl.BaseService;
import org.trc.service.order.IImportOrderInfoService;
import org.trc.service.order.IOrderItemService;

/**
 * Created by ding on 2017/6/22.
 */
@Service("importOrderInfoService")
public class ImportOrderInfoService extends BaseService<ImportOrderInfo,Long> implements IImportOrderInfoService {
}
