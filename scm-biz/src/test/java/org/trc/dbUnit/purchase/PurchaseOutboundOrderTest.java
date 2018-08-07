package org.trc.dbUnit.purchase;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.trc.biz.purchase.IPurchaseOutboundOrderBiz;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.purchase.PurchaseOutboundDetail;
import org.trc.domain.purchase.PurchaseOutboundOrder;
import org.trc.domain.supplier.SupplierBrandExt;
import org.trc.domain.warehouseInfo.WarehouseInfo;
import org.trc.domain.warehouseNotice.WarehouseNoticeDetails;
import org.trc.form.purchase.AuditPurchaseOrderForm;
import org.trc.form.purchase.PurchaseOutboundItemForm;
import org.trc.form.purchase.PurchaseOutboundOrderForm;
import org.trc.util.Pagenation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Description〈〉
 *
 * @author hzliuwei
 * @create 2018/7/30
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:config/resource-context.xml"})
public class PurchaseOutboundOrderTest {

    @Autowired
    private IPurchaseOutboundOrderBiz purchaseOutboundOrderBiz;

    /**
     * 采购退货单列表
     */

    @Test
    public void getPurchaseOutboundOrderListTest(){
        PurchaseOutboundOrderForm form = new PurchaseOutboundOrderForm();
        //退货类型1-正品，2-残品
        form.setReturnOrderType("1");
        Pagenation<PurchaseOutboundOrder> purchaseOutboundOrderPagenation = purchaseOutboundOrderBiz.purchaseOutboundOrderPageList(form, new Pagenation<>(), "YWX001");
        System.out.println(JSON.toJSONString(purchaseOutboundOrderPagenation));
    }

    /**
     * 暂存采购退货单
     */
    @Test
    public void savePurchaseOutboundOrderTest(){
        PurchaseOutboundOrder order = new PurchaseOutboundOrder();
        order.setSupplierCode("GYS000166");
        order.setWarehouseInfoId(58L);
        //退货类型1-正品，2-残品
        order.setReturnOrderType("2");
        //提货方式1-到仓自提，2-京东配送，3-其他物流
        order.setPickType("3");

        order.setReceiver("zhang");
        order.setReceiverNumber("17826821234");
        order.setReceiverProvince("330000");
        order.setReceiverCity("330100");
        order.setReceiverArea("330108");
        order.setReceiverAddress("奥斯卡刘德华");
        order.setReturnPolicy("存在残次品");

        List<PurchaseOutboundDetail> list = new ArrayList<>();

        PurchaseOutboundDetail detail = new PurchaseOutboundDetail();
        detail.setCanBackQuantity(100L);
        detail.setBrandName("品牌test");
        detail.setReturnOrderType("2");
        detail.setPrice(new BigDecimal(100));
        detail.setBarCode("99,88,77,66");
        detail.setBrandId("1893");
        detail.setCategoryId("165");
        detail.setItemNo("huohao");
        detail.setSkuCode("SP0201805190000768");
        detail.setSkuName("采购1");
        detail.setSpecNatureInfo("属性:采购1");
        detail.setTaxRate(new BigDecimal(16));
        detail.setOutboundQuantity(5L);
        list.add(detail);

        order.setPurchaseOutboundDetailList(list);


        AclUserAccreditInfo info = new AclUserAccreditInfo();
        info.setChannelCode("YWX001");
        info.setUserId("B571346F625E44DB8FCBA8116E72593D");
        //code [0]暂存，[1]提交审核
        purchaseOutboundOrderBiz.savePurchaseOutboundOrder(order, info);
    }

    /**
     * 更新采购退货单
     */
    @Test
    public void updatePurchaseOutboundOrderTest(){
        PurchaseOutboundOrder order = new PurchaseOutboundOrder();
        order.setId(17L);
        order.setAuditStatus("1");
        order.setSupplierCode("GYS000166");
        order.setPurchaseOutboundOrderCode("CGTH2018080200160");
        order.setWarehouseInfoId(58L);
        //退货类型1-正品，2-残品
        order.setReturnOrderType("2");
        //提货方式1-到仓自提，2-京东配送，3-其他物流
        order.setPickType("1");

        order.setReceiver("liu");
        order.setReceiverNumber("17826821234");
        order.setReceiverProvince("330000");
        order.setReceiverCity("330100");
        order.setReceiverArea("330108");
        order.setReceiverAddress("奥斯卡刘德华");
        order.setReturnPolicy("存在残次品");


        List<PurchaseOutboundDetail> list = new ArrayList<>();

        PurchaseOutboundDetail detail = new PurchaseOutboundDetail();
        detail.setCanBackQuantity(100L);
        detail.setBrandName("品牌test");
        detail.setReturnOrderType("2");
        detail.setPrice(new BigDecimal(100));
        detail.setBarCode("99,88,77,66");
        detail.setBrandId("1893");
        detail.setCategoryId("165");
        detail.setItemNo("huohao");
        detail.setSkuCode("SP0201805190000768");
        detail.setSkuName("采购1");
        detail.setSpecNatureInfo("属性:采购1");
        detail.setTaxRate(new BigDecimal(16));
        detail.setOutboundQuantity(5L);
        list.add(detail);

        order.setPurchaseOutboundDetailList(list);

        AclUserAccreditInfo info = new AclUserAccreditInfo();
        info.setChannelCode("YWX001");
        info.setUserId("B571346F625E44DB8FCBA8116E72593D");
        purchaseOutboundOrderBiz.updatePurchaseOutboundOrder(order, info);
    }

    /**
     * 获取商品
     */
    @Test
    public void getPurchaseOutboundOrderDetailTest(){
        PurchaseOutboundItemForm form = new PurchaseOutboundItemForm();
        form.setSupplierCode("GYS000015");
        form.setWarehouseInfoId("107");
        //退货类型1-正品，2-残品
        form.setReturnOrderType("2");

        Pagenation<PurchaseOutboundDetail> detail = purchaseOutboundOrderBiz.getPurchaseOutboundOrderDetail(form, new Pagenation<PurchaseOutboundDetail>(), "");
        System.out.println(JSON.toJSONString(detail));
    }

    /**
     * 获取审核列表
     */
    @Test
    public void getAuditPagelist(){

        PurchaseOutboundOrderForm form = new PurchaseOutboundOrderForm();
        //审核状态：1-未审核,3-审核通过
        form.setAuditStatus("1");

        Pagenation<PurchaseOutboundOrder> pagelist = purchaseOutboundOrderBiz.getAuditPagelist(form, new Pagenation<PurchaseOutboundOrder>(), "YWX001");
        System.out.println(JSON.toJSONString(pagelist));
    }

    /**
     * 采购退货单获取采购历史详情
     */
    @Test
    public void getPurchaseHistoryTest(){
        PurchaseOutboundItemForm form = new PurchaseOutboundItemForm();
        form.setStartDate("2018-02-02");
        form.setEndDate("2018-08-02");
        form.setSupplierCode("GYS000166");
        form.setWarehouseInfoId("107");
        //退货类型1-正品，2-残品
        form.setReturnOrderType("2");
        form.setSkuCode("SP0201805190000768");

        Pagenation<WarehouseNoticeDetails> pagenation = new Pagenation<>();
        Pagenation<WarehouseNoticeDetails> purchaseHistory = purchaseOutboundOrderBiz.getPurchaseHistory(form, pagenation);
        System.out.println(JSON.toJSONString(purchaseHistory));
    }

    /**
     *  根据采购退货单Id查询采购退货单
     */
    @Test
    public void getPurchaseOutboundOrderTest(){
        PurchaseOutboundOrder order = purchaseOutboundOrderBiz.getPurchaseOutboundOrderById(12L);
        System.out.println(JSON.toJSONString(order));
    }

    /**
     * 采购退货单审核操作，获取详情
     */
    @Test
    public void getPurchaseOutboundAuditOrderTest() {
        PurchaseOutboundOrder order = purchaseOutboundOrderBiz.getPurchaseOutboundAuditOrder(12L);
        System.out.println(JSON.toJSONString(order));
    }

    /**
     * 采购退货单审核
     */
    @Test
    public void auditPurchaseOrderTest(){
        AuditPurchaseOrderForm order = new AuditPurchaseOrderForm();
        order.setId(27L);
        //2-审核驳回,3-审核通过
        order.setAuditStatus("3");
        order.setAuditOpinion("asdsd");

        AclUserAccreditInfo info = new AclUserAccreditInfo();
        info.setChannelCode("YWX001");
        info.setUserId("B571346F625E44DB8FCBA8116E72593D");
        purchaseOutboundOrderBiz.auditPurchaseOrder(order, info);

    }

    /**
     * 采购退货单出库通知
     */
    @Test
    public void saveWarahouseAdviceTest(){

        AclUserAccreditInfo info = new AclUserAccreditInfo();
        info.setChannelCode("YWX001");
        info.setUserId("B571346F625E44DB8FCBA8116E72593D");
        purchaseOutboundOrderBiz.warehouseAdvice(27L, info);
    }

    /**
     * 更新采购退货单状态或出库通知作废操作
     */
    @Test
    public void updatePurchaseStateTest(){

        AclUserAccreditInfo info = new AclUserAccreditInfo();
        info.setChannelCode("YWX001");
        info.setUserId("B571346F625E44DB8FCBA8116E72593D");

        //出库通知作废
        //String s = purchaseOutboundOrderBiz.cancelWarahouseAdviceAndupdate(15L, info);
        //System.out.println(s);
        List<WarehouseInfo> allWarehouses = purchaseOutboundOrderBiz.getAllWarehouses();
        System.out.println(JSON.toJSONString(allWarehouses));


    }

    @Test
    public void findSupplierBrand(){
        List<SupplierBrandExt> supplierBrand = purchaseOutboundOrderBiz.findSupplierBrand("GYS000009");
        System.out.println(JSON.toJSONString(supplierBrand));
    }

    @Test
    public void getsupplierTest(){
        System.out.println(JSON.toJSONString(purchaseOutboundOrderBiz.getSuppliersByChannelCode("YWX001")));

    }
}
