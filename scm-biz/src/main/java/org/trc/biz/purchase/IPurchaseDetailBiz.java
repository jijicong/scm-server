package org.trc.biz.purchase;

import org.trc.constants.SupplyConstants;
import org.trc.domain.purchase.PurchaseDetail;

import java.util.List;

/**
 * Created by sone on 2017/5/25.
 */
public interface IPurchaseDetailBiz {

    List<PurchaseDetail> purchaseDetailList(Long purchaseId) throws Exception;

}
