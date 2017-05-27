package org.trc.service.purchase;

import org.trc.domain.purchase.PurchaseOrder;
import org.trc.domain.supplier.Supplier;
import org.trc.service.IBaseService;

import java.util.List;

/**
 * Created by sone on 2017/5/25.
 */
public interface IPurchaseOrderService extends IBaseService<PurchaseOrder,Long>{

     List<Supplier> findSuppliersByUserId(String userId);

}
