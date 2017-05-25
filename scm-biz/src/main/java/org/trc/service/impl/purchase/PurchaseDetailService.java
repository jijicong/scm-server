package org.trc.service.impl.purchase;

import org.springframework.stereotype.Service;
import org.trc.domain.purchase.PurchaseDetail;
import org.trc.service.impl.BaseService;
import org.trc.service.purchase.IPurchaseDetailService;

/**
 * Created by sone on 2017/5/25.
 */
@Service("purchaseDetailService")
public class PurchaseDetailService extends BaseService<PurchaseDetail,Long> implements IPurchaseDetailService{

}
