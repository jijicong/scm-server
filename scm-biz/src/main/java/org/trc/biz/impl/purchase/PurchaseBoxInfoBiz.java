package org.trc.biz.impl.purchase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.trc.biz.purchase.IPurchaseBoxInfoBiz;
import org.trc.service.purchase.IPurchaseBoxInfoService;

/**
 * Created by hzcyn on 2018/7/25.
 */
@Service("purchaseBoxInfoBiz")
public class PurchaseBoxInfoBiz implements IPurchaseBoxInfoBiz{

    @Autowired
    private IPurchaseBoxInfoService purchaseBoxInfoService;
}
