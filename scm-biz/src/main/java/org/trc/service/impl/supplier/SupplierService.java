package org.trc.service.impl.supplier;

import org.springframework.stereotype.Service;
import org.trc.domain.supplier.Supplier;
import org.trc.service.impl.BaseService;
import org.trc.service.supplier.ISupplierService;

/**
 * Created by hzwdx on 2017/5/6.
 */
@Service("supplierService")
public class SupplierService extends BaseService<Supplier, Long> implements ISupplierService {
}
