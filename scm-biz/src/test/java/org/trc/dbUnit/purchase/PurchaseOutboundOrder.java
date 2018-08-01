package org.trc.dbUnit.purchase;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.trc.biz.purchase.IPurchaseOutboundOrderBiz;
import org.trc.dbUnit.BaseTestContext;
import org.trc.domain.purchase.PurchaseOutboundDetail;
import org.trc.form.purchase.PurchaseOutboundItemForm;
import org.trc.util.Pagenation;

/**
 * Description〈〉
 *
 * @author hzliuwei
 * @create 2018/7/30
 */
public class PurchaseOutboundOrder extends BaseTestContext {

    @Autowired
    private IPurchaseOutboundOrderBiz purchaseOutboundOrderBiz;

    @Test
    public void PurchaseOutboundOrderDetailTest(){
        PurchaseOutboundItemForm form = new PurchaseOutboundItemForm();
        form.setSupplierCode("GYS000166");
        form.setReturnOrderType("2");
        form.setWarehouseInfoId("107");

        Pagenation<PurchaseOutboundDetail> detail = purchaseOutboundOrderBiz.getPurchaseOutboundOrderDetail(form, new Pagenation<PurchaseOutboundDetail>(), "");
        System.out.println(JSON.toJSONString(detail));
    }
}
