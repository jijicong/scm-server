package org.trc.service.impl.supplier;

import org.springframework.stereotype.Service;
import org.trc.domain.supplier.SupplierCategory;
import org.trc.service.impl.BaseService;
import org.trc.service.supplier.ISupplierCategoryService;

/**
 * Created by hzwdx on 2017/5/6.
 */
@Service("supplierCategoryService")
public class SupplierCategoryService extends BaseService<SupplierCategory, Long> implements ISupplierCategoryService {
}
