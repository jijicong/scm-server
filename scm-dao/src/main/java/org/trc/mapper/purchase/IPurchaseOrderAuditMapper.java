package org.trc.mapper.purchase;

import org.trc.domain.purchase.PurchaseOrderAddAudit;
import org.trc.domain.purchase.PurchaseOrderAudit;
import org.trc.util.BaseMapper;

import java.util.List;
import java.util.Map;

/**
 * Created by sone on 2017/6/20.
 */
public interface IPurchaseOrderAuditMapper extends BaseMapper<PurchaseOrderAudit>{

    List<PurchaseOrderAddAudit> selectPurchaseOrderAuditList(Map<String, Object> map);

    Integer selectCountAuditPurchaseOrder(Map<String, Object> map);
}
