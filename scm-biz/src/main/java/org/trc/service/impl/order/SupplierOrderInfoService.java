package org.trc.service.impl.order;

import org.springframework.stereotype.Service;
import org.trc.domain.order.SupplierOrderInfo;
import org.trc.service.impl.BaseService;
import org.trc.service.order.ISupplierOrderInfoService;

/**
 * Created by hzwdx on 2017/7/1.
 */
@Service("supplierOrderInfoService")
public class SupplierOrderInfoService extends BaseService<SupplierOrderInfo,Long> implements ISupplierOrderInfoService{
}
