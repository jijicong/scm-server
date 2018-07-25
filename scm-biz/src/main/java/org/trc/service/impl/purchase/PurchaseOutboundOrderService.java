package org.trc.service.impl.purchase;

import org.springframework.stereotype.Service;
import org.trc.domain.purchase.PurchaseOutboundOrder;
import org.trc.service.impl.BaseService;
import org.trc.service.purchase.IPurchaseOutboundOrderService;

/**
 * Description〈〉
 *
 * @author hzliuwei
 * @create 2018/7/25
 */
@Service("purchaseOutboundOrderService")
public class PurchaseOutboundOrderService extends BaseService<PurchaseOutboundOrder,Long> implements IPurchaseOutboundOrderService {
}
