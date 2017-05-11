package org.trc.service.impl.supplier;

import org.springframework.stereotype.Service;
import org.trc.domain.supplier.SupplierAfterSaleInfo;
import org.trc.service.impl.BaseService;
import org.trc.service.supplier.ISupplierAfterSaleInfoService;

/**
 * Created by hzwdx on 2017/5/6.
 */
@Service("supplierAfterSaleInfoService")
public class SupplierAfterSaleInfoService extends BaseService<SupplierAfterSaleInfo, Long> implements ISupplierAfterSaleInfoService {
}
