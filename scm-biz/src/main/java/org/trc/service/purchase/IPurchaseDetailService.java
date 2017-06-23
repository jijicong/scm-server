package org.trc.service.purchase;

import org.trc.domain.purchase.PurchaseDetail;
import org.trc.service.IBaseService;

/**
 * Created by sone on 2017/5/25.
 */
public interface IPurchaseDetailService extends IBaseService<PurchaseDetail,Long>{

    Integer deletePurchaseDetailByPurchaseOrderCode(String purchaseOrderCode);

}
