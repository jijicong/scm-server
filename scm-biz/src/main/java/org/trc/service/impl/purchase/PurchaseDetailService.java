package org.trc.service.impl.purchase;

import org.springframework.stereotype.Service;
import org.trc.domain.purchase.PurchaseDetail;
import org.trc.mapper.purchase.PurcahseDetailMapper;
import org.trc.service.impl.BaseService;
import org.trc.service.purchase.IPurchaseDetailService;

import javax.annotation.Resource;

/**
 * Created by sone on 2017/5/25.
 */
@Service("purchaseDetailService")
public class PurchaseDetailService extends BaseService<PurchaseDetail,Long> implements IPurchaseDetailService{

    @Resource
    private PurcahseDetailMapper purcahseDetailMapper;

    @Override
    public Integer deletePurchaseDetailByPurchaseOrderCode(String purchaseOrderCode) {
        return purcahseDetailMapper.deletePurchaseDetailByPurchaseOrderCode(purchaseOrderCode);
    }
}
