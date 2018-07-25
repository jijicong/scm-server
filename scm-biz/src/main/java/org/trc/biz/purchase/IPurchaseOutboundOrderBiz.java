package org.trc.biz.purchase;

import org.springframework.stereotype.Service;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.purchase.PurchaseOutboundOrder;
import org.trc.form.purchase.PurchaseOutboundOrderForm;
import org.trc.util.Pagenation;

@Service("purchaseOutboundOrderBiz")
public interface IPurchaseOutboundOrderBiz {

    /**
     * 查询采购退货单列表
     * @param form 查询条件
     * @param page 分页数据
     * @param channelCode
     * @return
     */
    Pagenation<PurchaseOutboundOrder> purchaseOutboundOrderPageList(PurchaseOutboundOrderForm form, Pagenation<PurchaseOutboundOrder> page, String channelCode);

    void savePurchaseOutboundOrder(PurchaseOutboundOrder form, String code, AclUserAccreditInfo property);

    /**
     *  采购退货单保存或
     * @param form  采购退货单数据
     * @param code  保存类型
     * @param property
     */
    //void savePurchaseOutboundOrder(PurchaseOutboundOrderDataForm form, String code, AclUserAccreditInfo property);
}
