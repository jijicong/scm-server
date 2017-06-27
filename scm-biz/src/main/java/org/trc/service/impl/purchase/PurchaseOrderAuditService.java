package org.trc.service.impl.purchase;

import org.springframework.stereotype.Service;
import org.trc.domain.purchase.PurchaseDetail;
import org.trc.domain.purchase.PurchaseOrderAddAudit;
import org.trc.domain.purchase.PurchaseOrderAudit;
import org.trc.mapper.purchase.IPurchaseOrderAuditMapper;
import org.trc.service.impl.BaseService;
import org.trc.service.purchase.IPurchaseDetailService;
import org.trc.service.purchase.IPurchaseOrderAuditService;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * Created by sone on 2017/6/20.
 */
@Service("purchaseOrderAuditService")
public class PurchaseOrderAuditService extends BaseService<PurchaseOrderAudit,Long> implements IPurchaseOrderAuditService{

    @Resource
    private IPurchaseOrderAuditMapper iPurchaseOrderAuditMapper;

    @Override
    public List<PurchaseOrderAddAudit> selectPurchaseOrderAuditList(Map<String, Object> map) {
        return iPurchaseOrderAuditMapper.selectPurchaseOrderAuditList(map);
    }

    @Override
    public Integer selectCountAuditPurchaseOrder(Map<String, Object> map) {
        return iPurchaseOrderAuditMapper.selectCountAuditPurchaseOrder(map);
    }

}
