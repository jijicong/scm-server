package org.trc.service.purchase;

import org.trc.domain.purchase.PurchaseDetail;
import org.trc.domain.purchase.PurchaseOrderAddAudit;
import org.trc.domain.purchase.PurchaseOrderAudit;
import org.trc.service.IBaseService;

import java.util.List;
import java.util.Map;

/**
 * Created by sone on 2017/6/20.
 */
public interface IPurchaseOrderAuditService extends IBaseService<PurchaseOrderAudit,Long>{

    List<PurchaseOrderAddAudit> selectPurchaseOrderAuditList(Map<String ,Object> map);

    Integer selectCountAuditPurchaseOrder(Map<String ,Object> map);

    Integer updatePurchaseOrderByPurchase(Map<String ,Object> map);

}
