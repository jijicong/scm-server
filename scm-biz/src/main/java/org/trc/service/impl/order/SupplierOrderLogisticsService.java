package org.trc.service.impl.order;

import org.springframework.stereotype.Service;
import org.trc.domain.order.SupplierOrderLogistics;
import org.trc.service.impl.BaseService;
import org.trc.service.order.ISupplierOrderLogisticsService;

/**
 * Created by hzwdx on 2017/7/1.
 */
@Service("supplierOrderLogisticsService")
public class SupplierOrderLogisticsService extends BaseService<SupplierOrderLogistics, Long> implements ISupplierOrderLogisticsService {
}
