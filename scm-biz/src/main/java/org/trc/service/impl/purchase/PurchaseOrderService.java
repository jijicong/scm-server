package org.trc.service.impl.purchase;

import org.springframework.stereotype.Service;
import org.trc.domain.purchase.PurchaseOrder;
import org.trc.domain.supplier.Supplier;
import org.trc.mapper.purchase.IPurchaseOrderMapper;
import org.trc.service.impl.BaseService;
import org.trc.service.purchase.IPurchaseOrderService;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by sone on 2017/5/25.
 */
@Service("purchaseOrderService")
public class PurchaseOrderService extends BaseService<PurchaseOrder,Long> implements IPurchaseOrderService {

    @Resource
    private IPurchaseOrderMapper purchaseOrderMapper;

    @Override
    public List<Supplier> findSuppliersByUserId(String userId) {

        return purchaseOrderMapper.findSuppliersByUserId(userId);

    }
}
