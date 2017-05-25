package org.trc.mapper.purchase;

import org.trc.domain.purchase.PurchaseOrder;
import org.trc.domain.supplier.Supplier;
import org.trc.util.BaseMapper;

import java.util.List;

/**
 * 采购订单
 * Created by sone on 2017/5/25.
 */
public interface IPurchaseOrderMapper extends BaseMapper<PurchaseOrder>{

    List<Supplier> findSuppliersByUserId(String userId);

}
