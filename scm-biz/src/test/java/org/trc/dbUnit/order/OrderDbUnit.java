package org.trc.dbUnit.order;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.apache.commons.lang3.StringUtils;
import org.dbunit.dataset.ReplacementDataSet;
import org.junit.Test;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.trc.biz.order.IScmOrderBiz;
import org.trc.constants.SupplyConstants;
import org.trc.dbUnit.order.form.*;
import org.trc.domain.order.WarehouseOrder;
import org.trc.form.JDModel.ReturnTypeDO;
import org.trc.form.liangyou.LiangYouSupplierOrder;
import org.trc.service.BaseTest;
import org.trc.service.IJDService;
import org.trc.service.order.IWarehouseOrderService;
import org.trc.service.util.IRealIpService;
import org.trc.util.AssertUtil;
import org.trc.util.ResponseAck;
import org.trc.util.SHAEncrypt;

import java.util.ArrayList;
import java.util.List;
import static org.mockito.Mockito.*;

/**
 * Created by hzwdx on 2017/8/29.
 */
public class OrderDbUnit extends BaseTest{

    @Autowired
    private IScmOrderBiz scmOrderBiz;
    @Autowired
    private IWarehouseOrderService warehouseOrderService;

    private static final String TABLE_PLATFORM_ORDER= "platform_order";
    private static final String TABLE_SHOP_ORDER= "shop_order";
    private static final String TABLE_ORDER_ITEM_ORDER= "order_item";
    private static final String TABLE_WAREHOUSE_ORDER= "warehouse_order";
    private static final String TABLE_SUPPLIER_ORDER_INFO= "supplier_order_info";


    @Test
    public void testReciveChannelOrder() throws Exception{
        //删除原数据
        execSql(conn,"delete from system_config");
        execSql(conn,"delete from sku_relation");
        execSql(conn,"delete from platform_order");
        execSql(conn,"delete from shop_order");
        execSql(conn,"delete from order_item");
        execSql(conn,"delete from warehouse_order");
        execSql(conn,"delete from supplier_order_info");
        execSql(conn,"delete from supplier_order_logistics");
        execSql(conn,"delete from external_item_sku");
        execSql(conn,"delete from log_information");
        execSql(conn,"delete from order_flow");
        //从xml文件读取数据并插入数据库中
        prepareData(conn, "order/preInsertskuRelation.xml");
        prepareData(conn, "order/preInsertSystemConfig.xml");
        prepareData(conn, "order/preInsertExternalItemSku.xml");
        //测试接收渠道订单
        ResponseAck<List<WarehouseOrder>> responseAck = scmOrderBiz.reciveChannelOrder(createOrderInfo());
        List<WarehouseOrder> lyWarehouseOrders = (List<WarehouseOrder>)responseAck.getData();
        AssertUtil.isTrue(lyWarehouseOrders.size() == 1, "测试方法testReciveChannelOrder返回粮油订单结果不等于1");
        /**
         * 校验平台订单数据
         */
        ReplacementDataSet expResult = createDataSet(Thread.currentThread().getContextClassLoader().getResourceAsStream("order/expInsertPlatformOrder.xml"));
        //空元素的字段需要一个"[null]"占位符，然后用 replacementDataSet.addReplacementObject("[null]", null) 替换成null,占位符可以自定义
        expResult.addReplacementObject("[null]", null);
        //从数据库中查出数据与期望结果作比较
        assertDataSet(TABLE_PLATFORM_ORDER,"select * from platform_order",expResult,conn);
        /**
         * 校验店铺订单数据
         */
        ReplacementDataSet expResult2 = createDataSet(Thread.currentThread().getContextClassLoader().getResourceAsStream("order/expInsertShopOrder.xml"));
        //空元素的字段需要一个"[null]"占位符，然后用 replacementDataSet.addReplacementObject("[null]", null) 替换成null,占位符可以自定义
        expResult2.addReplacementObject("[null]", null);
        //从数据库中查出数据与期望结果作比较
        assertDataSet(TABLE_SHOP_ORDER,"select * from shop_order",expResult2,conn);
        /**
         * 校验仓库订单数据
         */
        ReplacementDataSet expResult3 = createDataSet(Thread.currentThread().getContextClassLoader().getResourceAsStream("order/expInsertWarehouseOrder.xml"));
        //空元素的字段需要一个"[null]"占位符，然后用 replacementDataSet.addReplacementObject("[null]", null) 替换成null,占位符可以自定义
        expResult3.addReplacementObject("*", null);
        //从数据库中查出数据与期望结果作比较
        assertDataSet(TABLE_WAREHOUSE_ORDER,"select * from warehouse_order",expResult3,conn);
        /**
         * 校验订单商品数据
         */
        ReplacementDataSet expResult4 = createDataSet(Thread.currentThread().getContextClassLoader().getResourceAsStream("order/expInsertOrderItem.xml"));
        //空元素的字段需要一个"[null]"占位符，然后用 replacementDataSet.addReplacementObject("[null]", null) 替换成null,占位符可以自定义
        expResult4.addReplacementObject("*", null);
        //从数据库中查出数据与期望结果作比较
        assertDataSet(TABLE_ORDER_ITEM_ORDER,"select * from order_item",expResult4,conn);
    }

    /**
     * 粮油订单下单成功
     * @throws Exception
     */
    @Test
    public void testSubmitLiangYouOrders_success() throws Exception{
        //删除原数据
        execSql(conn,"delete from platform_order");
        execSql(conn,"delete from shop_order");
        execSql(conn,"delete from order_item");
        execSql(conn,"delete from warehouse_order");
        execSql(conn,"delete from supplier_order_info");
        execSql(conn,"delete from supplier_order_logistics");
        execSql(conn,"delete from external_item_sku");
        execSql(conn,"delete from log_information");
        execSql(conn,"delete from order_flow");
        execSql(conn,"delete from request_flow");
        //从xml文件读取数据并插入数据库中
        prepareData(conn, "order/submitOrder/preInsertPlatformOrder.xml");
        prepareData(conn, "order/submitOrder/preInsertShopOrder.xml");
        prepareData(conn, "order/submitOrder/preInsertWarehouseOrder.xml");
        prepareData(conn, "order/submitOrder/preInsertOrderItem.xml");
        prepareData(conn, "order/preInsertExternalItemSku.xml");

        //mock调用外部接口提交粮油订单
        mockSubmitLiangYouOrder(scmOrderBiz);

        String PLATFORM_ORDER_CODE = "1608201657240531";
        WarehouseOrder warehouseOrder = new WarehouseOrder();
        warehouseOrder.setPlatformOrderCode(PLATFORM_ORDER_CODE);
        warehouseOrder.setSupplierCode(SupplyConstants.Order.SUPPLIER_LY_CODE);
        List<WarehouseOrder> warehouseOrderList = warehouseOrderService.select(warehouseOrder);
        AssertUtil.notEmpty(warehouseOrderList, String.format("单元测试testSubmitLiangYouOrders_success方法中根据平台订单号%s和供应商编码%s查询仓库级订单信息为空", PLATFORM_ORDER_CODE, SupplyConstants.Order.SUPPLIER_LY_CODE));
        //scmOrderBiz.submitLiangYouOrders(warehouseOrderList);
        for(WarehouseOrder warehouseOrder2: warehouseOrderList){
            scmOrderBiz.submitLiangYouOrder(warehouseOrder2.getWarehouseOrderCode());
        }
        /**
         * 校验店铺订单数据
         */
        ReplacementDataSet expResult2 = createDataSet(Thread.currentThread().getContextClassLoader().getResourceAsStream("order/submitOrder/expInsertShopOrder_ly.xml"));
        //空元素的字段需要一个"[null]"占位符，然后用 replacementDataSet.addReplacementObject("[null]", null) 替换成null,占位符可以自定义
        //expResult2.addReplacementObject("*", null);
        //从数据库中查出数据与期望结果作比较
        assertDataSet(TABLE_SHOP_ORDER,"select * from shop_order",expResult2,conn);
        /**
         * 校验仓库订单数据
         */
        ReplacementDataSet expResult3 = createDataSet(Thread.currentThread().getContextClassLoader().getResourceAsStream("order/submitOrder/expInsertWarehouseOrder_ly.xml"));
        //空元素的字段需要一个"[null]"占位符，然后用 replacementDataSet.addReplacementObject("[null]", null) 替换成null,占位符可以自定义
        //expResult3.addReplacementObject("*", null);
        //从数据库中查出数据与期望结果作比较
        assertDataSet(TABLE_WAREHOUSE_ORDER,"select * from warehouse_order",expResult3,conn);
        /**
         * 校验订单商品数据
         */
        ReplacementDataSet expResult4 = createDataSet(Thread.currentThread().getContextClassLoader().getResourceAsStream("order/submitOrder/expInsertOrderItem_ly.xml"));
        //空元素的字段需要一个"[null]"占位符，然后用 replacementDataSet.addReplacementObject("[null]", null) 替换成null,占位符可以自定义
        //expResult4.addReplacementObject("*", null);
        //从数据库中查出数据与期望结果作比较
        assertDataSet(TABLE_ORDER_ITEM_ORDER,"select * from order_item",expResult4,conn);
        /**
         * 校验订单商品数据
         */
        ReplacementDataSet expResult5 = createDataSet(Thread.currentThread().getContextClassLoader().getResourceAsStream("order/submitOrder/expInsertSupplierOrderInfo_ly.xml"));
        //空元素的字段需要一个"[null]"占位符，然后用 replacementDataSet.addReplacementObject("[null]", null) 替换成null,占位符可以自定义
        expResult5.addReplacementObject("*", null);
        //从数据库中查出数据与期望结果作比较
        //assertDataSet(TABLE_SUPPLIER_ORDER_INFO,"select * from supplier_order_info",expResult5,conn);
    }

    @Test
    public void testFetchLogisticsInfo()throws Exception{
        //删除原数据
        execSql(conn,"delete from platform_order");
        execSql(conn,"delete from shop_order");
        execSql(conn,"delete from order_item");
        execSql(conn,"delete from warehouse_order");
        execSql(conn,"delete from supplier_order_info");
        //从xml文件读取数据并插入数据库中
        prepareData(conn, "order/logistics/preInsertPlatformOrder.xml");
        prepareData(conn, "order/logistics/preInsertShopOrder.xml");
        prepareData(conn, "order/logistics/preInsertWarehouseOrder.xml");
        prepareData(conn, "order/logistics/preInsertOrderItem.xml");
        prepareData(conn, "order/logistics/preInsertSupplierOrderInfo.xml");
        //mock调用外部接口查询物流
        mockQueryLogistics(scmOrderBiz);
        scmOrderBiz.fetchLogisticsInfo();

    }

    /**
     * mock调用外部接口提交粮油订单
     * @param scmOrderBiz
     */
    private void mockSubmitLiangYouOrder(IScmOrderBiz scmOrderBiz){
        IJDService ijdService = mock(IJDService.class);
        scmOrderBiz.setIjdService(ijdService);
        LiangYouSupplierOrder liangYouSupplierOrder = new LiangYouSupplierOrder();
        ResponseAck responseAck = new ResponseAck();
        responseAck.setCode(ResponseAck.SUCCESS_CODE);
        String submitOrderReturn = "{\n" +
                "        \"orderType\": \"1\",\n" +
                "        \"warehouseOrderCode\": \"GYS0000571201710180000530\",\n" +
                "        \"order\": [\n" +
                "            {\n" +
                "                \"supplyOrderCode\": \"33333xxxxxxxxx0000016-2\",\n" +
                "                \"skus\": [\n" +
                "                    {\n" +
                "                        \"skuName\": \"保加利亚奥伯伦（Oberon）蜂蜜 椴树蜜\",\n" +
                "                        \"num\": 2,\n" +
                "                        \"skuCode\": \"310516625460002590\"\n" +
                "                    }\n" +
                "                ],\n" +
                "                \"state\": \"200\",\n" +
                "                \"message\": \"子订单下单成功\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"supplyOrderCode\": \"33333xxxxxxxxx0000016-1\",\n" +
                "                \"skus\": [\n" +
                "                    {\n" +
                "                        \"skuName\": \"【单品包邮】日版MUHI池田模范堂 儿童液体无比滴S2a清凉冷感止痒露药水40ml\",\n" +
                "                        \"num\": 1,\n" +
                "                        \"skuCode\": \"1075510226\"\n" +
                "                    }\n" +
                "                ],\n" +
                "                \"state\": \"200\",\n" +
                "                \"message\": \"子订单下单成功\"\n" +
                "            }\n" +
                "        ]\n" +
                "    }";
        responseAck.setData(submitOrderReturn);
        when(ijdService.submitLiangYouOrder(any(liangYouSupplierOrder.getClass()))).thenReturn(responseAck);
    }

    /**
     * mock调用外部接口查询供应商订单物流信息
     * @param scmOrderBiz
     */
    private void mockQueryLogistics(IScmOrderBiz scmOrderBiz){
        IJDService ijdService = mock(IJDService.class);
        scmOrderBiz.setIjdService(ijdService);
        ReturnTypeDO returnTypeDO = new ReturnTypeDO();
        returnTypeDO.setSuccess(true);
        String result = "{\n" +
                "        \"logistics\": [\n" +
                "            {\n" +
                "                \"skus\": [\n" +
                "                    {\n" +
                "                        \"skuName\": \"佳能（Glad）背心袋抽取式保鲜袋大号中号组合装BCB30+BCB25\",\n" +
                "                        \"num\": \"2\",\n" +
                "                        \"skuCode\": \"1241479\"\n" +
                "                    }\n" +
                "                ],\n" +
                "                \"waybillNumber\": \"22222222-test\",\n" +
                "                \"logisticsCorporation\": \"京东快递\",\n" +
                "                \"logisticInfo\": [\n" +
                "                    {\n" +
                "                        \"msgTime\": \"2017-09-21 22:42:04\",\n" +
                "                        \"content\": \"您提交了订单，请等待系统确认\",\n" +
                "                        \"operator\": \"客户\"\n" +
                "                    },\n" +
                "                    {\n" +
                "                        \"msgTime\": \"2017-09-21 22:42:17\",\n" +
                "                        \"content\": \"您的订单预计9月22日送达您手中\",\n" +
                "                        \"operator\": \"系统\"\n" +
                "                    }\n" +
                "                ],\n" +
                "                \"supplierOrderCode\": \"22222222-test\",\n" +
                "                \"logisticsStatus\": \"1\"\n" +
                "            }\n" +
                "        ],\n" +
                "        \"type\": \"0\",\n" +
                "        \"warehouseOrderCode\": \"GYS0000561201710180000529\"\n" +
                "    }";
        returnTypeDO.setResult(result);
        when(ijdService.getLogisticsInfo(anyString(), anyString())).thenReturn(returnTypeDO);

        IRealIpService iRealIpService = mock(IRealIpService.class);
        scmOrderBiz.setiRealIpService(iRealIpService);
        when(iRealIpService.isRealTimerService()).thenReturn(true);
    }


    private String createOrderInfo(){
        TrcOrderForm orderForm  = new TrcOrderForm();
        //创建平台订单信息
        TrcPlatformOrder platformOrder = JSON.parseObject("{\"adjustFee\": 0,\"anony\": 0,\"buyerArea\": \"110100/110101\",\"cancelReason\": \"现在不想购买\",\"cancelStatus\": \"SUCCESS\",\"channelCode\": \"QD001\",\"couponCode\": \"\",\"createTime\": 1471683422,\"discountCouponPlatform\": 0,\"discountCouponShop\": 0,\"discountFee\": 0,\"discountPromotion\": 0,\"endTime\": 1471919905,\"groupBuyStatus\": \"NO_APPLY\",\"invoiceMain\": \"浙江小泰科技有限公司\",\"invoiceType\": \"2\",\"ip\": \"118.178.15.71\",\"isClearing\": 0,\"isDeleted\": \"0\",\"isVirtual\": 0,\"itemNum\": 6,\"needInvoice\": \"0\",\"obtainPointFee\": 180,\"payTime\": 1508206933000,\"payType\": \"online\",\"payment\": 225,\"platformCode\": \"QD001\",\"platformOrderCode\": \"1608201657240531\",\"platformType\": \"wap\",\"pointsFee\": 0,\"postageFee\": 39,\"rateStatus\": 0,\"receiveTime\": 1471919905,\"receiverAddress\": \"余杭区\",\"receiverCity\": \"杭州市\",\"receiverDistrict\": \"余杭区\",\"receiverEmail\": \"471869639@qq.com\",\"receiverIdCard\": \"420281198602197693\",\"receiverMobile\": \"15068839416\",\"receiverName\": \"熊9测试\",\"receiverPhone\": \"15068839416\",\"receiverProvince\": \"浙江省\",\"receiverZip\": \"232656\",\"shippingType\": \"express\",\"status\": \"TRADE_CLOSED_BY_CANCEL\",\"totalFee\": 184,\"totalTax\": 2,\"type\": 0,\"userId\": \"531\",\"userName\": \"15229896960\"}").toJavaObject(TrcPlatformOrder.class);
        orderForm.setPlatformOrder(platformOrder);
        //创建店铺订单信息
        List<TrcShopOrderForm> shopOrders = new ArrayList<>();
        TrcShopOrder shopOrder = JSON.parseObject("{\"adjustFee\": 0,\"channelCode\": \"QD001\",\"consignTime\": 1468559766,\"createTime\": 1468555312,\"discountCouponPlatform\": 0,\"discountCouponShop\": 0,\"discountFee\": 0,\"discountPromotion\": 0,\"dlytmplIds\": \"4\",\"groupBuyStatus\": \"NO_APPLY\",\"isDeleted\": \"0\",\"isPartConsign\": \"0\",\"itemNum\": 3,\"payment\": 55,\"platformCode\": \"QD001\",\"platformOrderCode\": \"1608201657240531\",\"platformType\": \"wap\",\"postageFee\": 19,\"rateStatus\": \"1\",\"shopId\": 4,\"shopName\": \"泰然直营（自营店铺）（自营店铺）\",\"shopOrderCode\": \"33333xxxxxxxxx0000015\",\"status\": \"TRADE_FINISHED\",\"title\": \"订单明细介绍\",\"totalFee\": 35,\"totalTax\": 1,\"totalWeight\": 0,\"tradeMemo\": \"13588129773\",\"userId\": \"5\"}").toJavaObject(TrcShopOrder.class);
        List<TrcOrderItem> orderItems = JSONArray.parseArray("[{\"adjustFee\": 0,\"barCode\": \"\",\"catServiceRate\": 0,\"category\": \"232\",\"channelCode\": \"QD001\",\"complaintsStatus\": \"NOT_COMPLAINTS\",\"consignTime\": 1468559766,\"createTime\": 1468555312,\"customsPrice\": 0,\"discountCouponPlatform\": 0,\"discountCouponShop\": 0,\"discountFee\": 0,\"discountPromotion\": 0,\"dlytmplId\": 4,\"endTime\": 1468568194,\"isOversold\": false,\"itemName\": \"佳能（Glad）背心袋抽取式保鲜袋大号中号组合装BCB30+BCB25\",\"itemNo\": \"yidonghuafei20\",\"marketPrice\": 19.94,\"num\": 2,\"objType\": \"recharge\",\"outerSkuId\": \"SP1201710120000627\",\"oversold\": false,\"params\": \"[]\",\"payTime\": 1468555346,\"payment\": 36,\"picPath\": \"\",\"platformCode\": \"QD001\",\"platformOrderCode\": \"1608201657240531\",\"postDiscount\": 10,\"price\": 13,\"priceTax\": 0,\"promotionPrice\": 0,\"promotionTags\": \"\",\"refundFee\": 0,\"shopId\": 4,\"shopName\": \"泰然直营（自营店铺）（自营店铺）\",\"shopOrderCode\": \"33333xxxxxxxxx0000015\",\"skuCode\": \"\",\"status\": \"TRADE_FINISHED\",\"subStock\": false,\"taxRate\": 0,\"totalFee\": 26,\"totalWeight\": 0,\"transactionPrice\": 19.94,\"type\": \"4\",\"userId\": \"5\"},{\"adjustFee\": 0,\"barCode\": \"\",\"catServiceRate\": 0,\"category\": \"64\",\"channelCode\": \"QD001\",\"complaintsStatus\": \"NOT_COMPLAINTS\",\"consignTime\": 1468918407,\"createTime\": 1468557443,\"customsPrice\": 0,\"discountCouponPlatform\": 0,\"discountCouponShop\": 0,\"discountFee\": 0,\"discountPromotion\": 0,\"dlytmplId\": 1,\"endTime\": 1469685165,\"isOversold\": false,\"itemName\": \"小米（MI）7号电池 彩虹电池碱性 7号（10粒装）\",\"itemNo\": \"310520151011501429\",\"marketPrice\": 52,\"num\": 1,\"objType\": \"item\",\"outerSkuId\": \"SP1201710120000628\",\"oversold\": false,\"params\": \"[]\",\"payTime\": 1468557489,\"payment\": 19,\"picPath\": \"https://image.trc.com/c8/f8/ed/d880b82a6d258c0e357cafa1390d32e54e9106f6.jpg\",\"platformCode\": \"QD001\",\"platformOrderCode\": \"1608201657240531\",\"postDiscount\": 9,\"price\": 9,\"priceTax\": 1,\"promotionPrice\": 0,\"promotionTags\": \"\",\"refundFee\": 0,\"shopId\": 2,\"shopName\": \"泰然直营1（自营店铺）\",\"shopOrderCode\": \"33333xxxxxxxxx0000015\",\"skuCode\": \"\",\"status\": \"TRADE_FINISHED\",\"subStock\": false,\"taxRate\": 0,\"totalFee\": 9,\"totalWeight\": 0.1,\"transactionPrice\": 57,\"type\": \"0\",\"userId\": \"10\"}]", TrcOrderItem.class);
        TrcShopOrderForm trcShopOrderForm = new TrcShopOrderForm();
        trcShopOrderForm.setShopOrder(shopOrder);
        trcShopOrderForm.setOrderItems(orderItems);
        shopOrders.add(trcShopOrderForm);
        TrcShopOrder shopOrder2 = JSON.parseObject("{\"adjustFee\": 0,\"channelCode\": \"QD001\",\"consignTime\": 1468918407,\"createTime\": 1468557443,\"discountCouponPlatform\": 0,\"discountCouponShop\": 0,\"discountFee\": 0,\"discountPromotion\": 0,\"dlytmplIds\": \"1\",\"groupBuyStatus\": \"NO_APPLY\",\"isDeleted\": \"0\",\"isPartConsign\": \"0\",\"itemNum\": 3,\"payment\": 170,\"platformCode\": \"QD001\",\"platformOrderCode\": \"1608201657240531\",\"platformType\": \"wap\",\"postageFee\": 20,\"rateStatus\": \"1\",\"shopId\": 2,\"shopName\": \"泰然直营1（自营店铺）\",\"shopOrderCode\": \"33333xxxxxxxxx0000016\",\"status\": \"TRADE_FINISHED\",\"title\": \"订单明细介绍\",\"totalFee\": 149,\"totalTax\": 1,\"totalWeight\": 0.1,\"tradeMemo\": \"\",\"userId\": \"10\"}").toJavaObject(TrcShopOrder.class);
        List<TrcOrderItem> orderItems2 = JSONArray.parseArray("[{\"adjustFee\": 0,\"barCode\": \"\",\"catServiceRate\": 0,\"category\": \"37\",\"channelCode\": \"QD001\",\"complaintsStatus\": \"NOT_COMPLAINTS\",\"createTime\": 1468558351,\"customsPrice\": 0,\"discountCouponPlatform\": 0,\"discountCouponShop\": 0,\"discountFee\": 0,\"discountPromotion\": 0,\"dlytmplId\": 1,\"endTime\": 1468568307,\"isOversold\": false,\"itemName\": \"保加利亚奥伯伦（Oberon）蜂蜜 椴树蜜\",\"itemNo\": \"310520141011500548\",\"marketPrice\": 458,\"num\": 2,\"objType\": \"item\",\"outerSkuId\": \"SP1201709070000580\",\"oversold\": false,\"params\": \"[]\",\"payment\": 85,\"picPath\": \"https://image.trc.com/0b/29/a6/2caf0f806173ee02d9d7c99c3d7286deaca2032a.jpg\",\"platformCode\": \"QD001\",\"platformOrderCode\": \"1608201657240531\",\"postDiscount\": 10,\"price\": 37,\"priceTax\": 1,\"promotionPrice\": 0,\"promotionTags\": \"\",\"refundFee\": 0,\"shopId\": 2,\"shopName\": \"泰然直营1（自营店铺）\",\"shopOrderCode\": \"33333xxxxxxxxx0000016\",\"skuCode\": \"\",\"status\": \"TRADE_CLOSED_BY_CANCEL\",\"subStock\": false,\"taxRate\": 0,\"totalFee\": 74,\"totalWeight\": 5,\"transactionPrice\": 467,\"type\": \"0\",\"userId\": \"5\"},{\"adjustFee\": 0,\"barCode\": \"\",\"catServiceRate\": 0,\"category\": \"102\",\"channelCode\": \"QD001\",\"complaintsStatus\": \"NOT_COMPLAINTS\",\"createTime\": 1468558437,\"customsPrice\": 0,\"discountCouponPlatform\": 0,\"discountCouponShop\": 0,\"discountFee\": 0,\"discountPromotion\": 0,\"dlytmplId\": 2,\"endTime\": 1468818061,\"isOversold\": false,\"itemName\": \"【单品包邮】日版MUHI池田模范堂 儿童液体无比滴S2a清凉冷感止痒露药水40ml\",\"itemNo\": \"310520151011501513\",\"marketPrice\": 127,\"num\": 1,\"objType\": \"item\",\"outerSkuId\": \"SP1201710170000630\",\"oversold\": false,\"params\": \"[]\",\"payment\": 85,\"picPath\": \"https://image.trc.com/86/5a/17/c1f0a338d700ffb951c6b8f7cf257978383ed482.jpg\",\"platformCode\": \"QD001\",\"platformOrderCode\": \"1608201657240531\",\"postDiscount\": 10,\"price\": 75,\"priceTax\": 0,\"promotionPrice\": 0,\"promotionTags\": \"\",\"refundFee\": 0,\"shopId\": 2,\"shopName\": \"泰然直营1（自营店铺）\",\"shopOrderCode\": \"33333xxxxxxxxx0000016\",\"skuCode\": \"\",\"status\": \"TRADE_CLOSED_BY_CANCEL\",\"subStock\": false,\"taxRate\": 0,\"totalFee\": 75,\"totalWeight\": 0.4,\"transactionPrice\": 131.5,\"type\": \"0\",\"userId\": \"4\"}]", TrcOrderItem.class);
        TrcShopOrderForm trcShopOrderForm2 = new TrcShopOrderForm();
        trcShopOrderForm2.setShopOrder(shopOrder2);
        trcShopOrderForm2.setOrderItems(orderItems2);
        shopOrders.add(trcShopOrderForm2);
        orderForm.setShopOrders(shopOrders);
        //设置请求参数
        orderForm.setNoticeNum(String.valueOf(System.currentTimeMillis()));
        orderForm.setOperateTime(System.currentTimeMillis());
        StringBuilder sb = new StringBuilder();
        sb.append(orderForm.getNoticeNum()).append("|");
        sb.append(orderForm.getOperateTime()).append("|");
        sb.append(orderForm.getPlatformOrder().getChannelCode()).append("|");
        sb.append(orderForm.getPlatformOrder().getPlatformOrderCode()).append("|");
        for(TrcShopOrderForm shopOrderForm : orderForm.getShopOrders()){
            sb.append(shopOrderForm.getShopOrder().getShopOrderCode()).append("|");
        }

        sb.append(StringUtils.isNotBlank(orderForm.getPlatformOrder().getUserId())? orderForm.getPlatformOrder().getUserId():StringUtils.EMPTY).append(SupplyConstants.Symbol.FULL_PATH_SPLIT);//用户id
        sb.append(StringUtils.isNotBlank(orderForm.getPlatformOrder().getUserName())? orderForm.getPlatformOrder().getUserName():StringUtils.EMPTY).append(SupplyConstants.Symbol.FULL_PATH_SPLIT);//会员名称
        sb.append(null != orderForm.getPlatformOrder().getItemNum()? orderForm.getPlatformOrder().getItemNum():StringUtils.EMPTY).append(SupplyConstants.Symbol.FULL_PATH_SPLIT);//商品总数
        sb.append(null != orderForm.getPlatformOrder().getPayment()? orderForm.getPlatformOrder().getPayment():StringUtils.EMPTY).append(SupplyConstants.Symbol.FULL_PATH_SPLIT);//实付金额
        sb.append(null != orderForm.getPlatformOrder().getTotalFee()? orderForm.getPlatformOrder().getTotalFee():StringUtils.EMPTY).append(SupplyConstants.Symbol.FULL_PATH_SPLIT);//订单总金额
        sb.append(null != orderForm.getPlatformOrder().getPostageFee()? orderForm.getPlatformOrder().getPostageFee():StringUtils.EMPTY).append(SupplyConstants.Symbol.FULL_PATH_SPLIT);//邮费
        sb.append(null != orderForm.getPlatformOrder().getTotalTax()? orderForm.getPlatformOrder().getTotalTax():StringUtils.EMPTY).append(SupplyConstants.Symbol.FULL_PATH_SPLIT);//总税费
        sb.append(StringUtils.isNotBlank(orderForm.getPlatformOrder().getStatus())? orderForm.getPlatformOrder().getStatus():StringUtils.EMPTY).append(SupplyConstants.Symbol.FULL_PATH_SPLIT);//订单状态
        sb.append(StringUtils.isNotBlank(orderForm.getPlatformOrder().getReceiverIdCard())? orderForm.getPlatformOrder().getReceiverIdCard():StringUtils.EMPTY).append(SupplyConstants.Symbol.FULL_PATH_SPLIT);//收货人身份证
        sb.append(StringUtils.isNotBlank(orderForm.getPlatformOrder().getReceiverMobile())? orderForm.getPlatformOrder().getReceiverMobile():StringUtils.EMPTY).append(SupplyConstants.Symbol.FULL_PATH_SPLIT);//收货人手机号码
        String encryptStr = sb.toString();
        if(encryptStr.endsWith("|")){
            encryptStr = encryptStr.substring(0, encryptStr.length()-1);
        }
        String sign = SHAEncrypt.SHA256(encryptStr);
        orderForm.setSign(sign);
        return JSON.toJSONString(orderForm);
    }






}
