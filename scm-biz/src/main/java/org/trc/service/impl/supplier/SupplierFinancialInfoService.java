package org.trc.service.impl.supplier;

import org.springframework.stereotype.Service;
import org.trc.domain.supplier.SupplierFinancialInfo;
import org.trc.service.impl.BaseService;
import org.trc.service.supplier.ISupplierFinancialInfoService;

/**
 * Created by hzwdx on 2017/5/6.
 */
@Service("supplierFinancialInfoService")
public class SupplierFinancialInfoService extends BaseService<SupplierFinancialInfo, Long> implements ISupplierFinancialInfoService {
}
