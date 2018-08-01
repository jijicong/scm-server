package org.trc.dbUnit.purchase;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.trc.biz.purchase.IPurchaseOutboundOrderBiz;
import org.trc.dbUnit.BaseTestContext;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.purchase.PurchaseOutboundOrder;
import org.trc.form.purchase.PurchaseOutboundOrderForm;
import org.trc.util.Pagenation;

/**
 * Description〈〉
 *
 * @author hzliuwei
 * @create 2018/7/30
 */
public class PurchaseOutboundOrderTest extends BaseTestContext {

    @Autowired
    private IPurchaseOutboundOrderBiz purchaseOutboundOrderBiz;

    /**
     * 采购退货单列表
     */

    @Test
    public void getPurchaseOutboundOrderListTest(){
        Pagenation<PurchaseOutboundOrder> purchaseOutboundOrderPagenation = purchaseOutboundOrderBiz.purchaseOutboundOrderPageList(new PurchaseOutboundOrderForm(), new Pagenation<>(), "YWX001");
        System.out.println(JSON.toJSONString(purchaseOutboundOrderPagenation));
    }

    /**
     * 暂存采购退货单
     */
    @Test
    public void savePurchaseOutboundOrderTest(){
        PurchaseOutboundOrder order = new PurchaseOutboundOrder();
        AclUserAccreditInfo info = new AclUserAccreditInfo();
        purchaseOutboundOrderBiz.savePurchaseOutboundOrder(order,"0",info);
    }
}
