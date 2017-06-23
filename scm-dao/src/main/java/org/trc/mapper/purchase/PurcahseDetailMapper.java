package org.trc.mapper.purchase;

import org.trc.domain.purchase.PurchaseDetail;
import org.trc.util.BaseMapper;

/**
 * Created by sone on 2017/5/25.
 */
public interface PurcahseDetailMapper extends BaseMapper<PurchaseDetail>{

    Integer deletePurchaseDetailByPurchaseOrderCode(String purchaseOrderCode);

}
