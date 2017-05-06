package org.trc.service.impl.supplier;

import org.springframework.stereotype.Service;
import org.trc.domain.supplier.SupplierBrand;
import org.trc.service.impl.BaseService;
import org.trc.service.supplier.ISupplierBrandService;

/**
 * Created by hzwdx on 2017/5/6.
 */
@Service("supplierBrandService")
public class SupplierBrandService extends BaseService<SupplierBrand, Long> implements ISupplierBrandService {
}
