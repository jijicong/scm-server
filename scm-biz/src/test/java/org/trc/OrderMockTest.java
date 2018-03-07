package org.trc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.trc.biz.order.IScmOrderBiz;
import org.trc.constants.SupplyConstants;
import org.trc.dbUnit.order.form.TrcOrderForm;
import org.trc.dbUnit.order.form.TrcOrderItem;
import org.trc.dbUnit.order.form.TrcPlatformOrder;
import org.trc.dbUnit.order.form.TrcShopOrder;
import org.trc.dbUnit.order.form.TrcShopOrderForm;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.form.LogisticNoticeForm;
import org.trc.form.JDModel.JingDongSupplierOrder;
import org.trc.form.JDModel.ReturnTypeDO;
import org.trc.form.liangyou.LiangYouSupplierOrder;
import org.trc.model.ToGlyResultDO;
import org.trc.service.IJDService;
import org.trc.service.ITrcService;
import org.trc.service.util.IRealIpService;
import org.trc.util.ResponseAck;
import org.trc.util.SHAEncrypt;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

@RunWith(SpringJUnit4ClassRunner.class)  //标记测试运行的环境
@ContextConfiguration({"classpath:config/resource-context.xml"}) //配合spring测试  可以引入多个配置文件
public class OrderMockTest {
	@Autowired 
	private IScmOrderBiz scmOrderBiz;
	
	/**
	 * 接收订单，粮油提交直接通过postman请求接口完成下单
	 * @throws Exception
	 */
	@Test
	public void reciveChannelOrder () throws Exception {	
		scmOrderBiz.reciveChannelOrder(createOrderInfo());
		
	}
	
	/**
	 * 京东接收完订单之后，提交订单
	 * @throws Exception
	 */
	@Test
	public void submitJdOrder () throws Exception {
		mockSubmitJDOrder(scmOrderBiz);
		scmOrderBiz.submitJingDongOrder("GYS0000561201803050000690", "address", "sub_address", createAclUserAccreditInfo());
		
	}
	/**
	 * 接收物流信息
	 * @throws Exception
	 */
	@Test
	public void testLogisticsInfo () throws Exception {
		mockQueryLogistics(scmOrderBiz);
		scmOrderBiz.fetchLogisticsInfo();
		
	}
	
	/**
	 * 订单取消，粮油
	 */
	@Test
	public void testCancelOrder () {
		String orderInfo = "{\"orderType\":\"1\",\"order\":[{\"warehouseOrderCode\":\"GYS0000571201803050000691\",\"supplierParentOrderCode\":\"\",\"supplyOrderCode\":\"33333xxxxxxxxx0000016\",\"cancelReason\":\"人为取消\"}]}";
		scmOrderBiz.supplierCancelOrder(orderInfo);
	}
	
    private AclUserAccreditInfo createAclUserAccreditInfo () {
        AclUserAccreditInfo info = new AclUserAccreditInfo();
        info.setId(1L);
        info.setChannelId(2L);
        info.setChannelName("小泰乐活");
        info.setUserId("E2E4BDAD80354EFAB6E70120C271968C");
        info.setPhone("15757195796");
        info.setName("admin");
        info.setUserType("mixtureUser");
        info.setChannelCode("QD002");
        info.setRemark("admin");
        info.setIsValid("1");
        info.setIsDeleted("0");
        info.setCreateOperator("E2E4BDAD80354EFAB6E70120C271968C");
        return info;
    }
    
    /**
     * mock调用外部接口提交京东订单
     * @param scmOrderBiz
     */
    private void mockSubmitJDOrder(IScmOrderBiz scmOrderBiz){
        IJDService ijdService = mock(IJDService.class);
        scmOrderBiz.setIjdService(ijdService);
        JingDongSupplierOrder jdSupplierOrder = new JingDongSupplierOrder();
        ResponseAck responseAck = new ResponseAck();
        responseAck.setCode(ResponseAck.SUCCESS_CODE);
        String submitOrderReturn = "{\n" +
                "        \"orderType\": \"0\",\n" +
                "        \"warehouseOrderCode\": \"GYS0000561201803050000690\",\n" +
                "        \"order\": [\n" +
                "            {\n" +
                "                \"supplyOrderCode\": \"33333xxxxxxxxx0000016-2\",\n" +
                "                \"skus\": [\n" +
                "                    {\n" +
                "                        \"skuName\": \"佳能（Glad）背心袋抽取式保鲜袋大号中号组合装BCB30+BCB25\",\n" +
                "                        \"num\": 2,\n" +
                "                        \"skuCode\": \"1241479\"\n" +
                "                    }\n" +
                "                ],\n" +
                "                \"state\": \"200\",\n" +
                "                \"message\": \"子订单下单成功\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"supplyOrderCode\": \"33333xxxxxxxxx0000016-1\",\n" +
                "                \"skus\": [\n" +
                "                    {\n" +
                "                        \"skuName\": \"【小米（MI）7号电池 彩虹电池碱性 7号（10粒装）\",\n" +
                "                        \"num\": 1,\n" +
                "                        \"skuCode\": \"2614024\"\n" +
                "                    }\n" +
                "                ],\n" +
                "                \"state\": \"200\",\n" +
                "                \"message\": \"子订单下单成功\"\n" +
                "            }\n" +
                "        ]\n" +
                "    }";
        responseAck.setData(submitOrderReturn);
        when(ijdService.submitJingDongOrder(any(jdSupplierOrder.getClass()))).thenReturn(responseAck);
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

        ReturnTypeDO returnTypeDO2 = new ReturnTypeDO();
        returnTypeDO2.setSuccess(true);
        String result2 = "{\n" +
                "        \"logistics\": [\n" +
                "            {\n" +
                "                \"skus\": [\n" +
                "                    {\n" +
                "                        \"skuName\": \"保加利亚奥伯伦（Oberon）蜂蜜 椴树蜜\",\n" +
                "                        \"num\": \"2\",\n" +
                "                        \"skuCode\": \"310516625460002590\"\n" +
                "                    }\n" +
                "                ],\n" +
                "                \"waybillNumber\": \"1608201657240531\",\n" +
                "                \"logisticsCorporation\": \"圆通速递\",\n" +
                "                \"logisticInfo\": [],\n" +
                "                \"supplierOrderCode\": \"33333xxxxxxxxx0000016-2\",\n" +
                "                \"logisticsStatus\": \"1\"\n" +
                "            },\n" +
                "\t\t\t{\n" +
                "                \"skus\": [\n" +
                "                    {\n" +
                "                        \"skuName\": \"【单品包邮】日版MUHI池田模范堂 儿童液体无比滴S2a清凉冷感止痒露药水40ml\",\n" +
                "                        \"num\": \"1\",\n" +
                "                        \"skuCode\": \"1075510226\"\n" +
                "                    }\n" +
                "                ],\n" +
                "                \"waybillNumber\": \"1608201657240531\",\n" +
                "                \"logisticsCorporation\": \"圆通速递\",\n" +
                "                \"logisticInfo\": [],\n" +
                "                \"supplierOrderCode\": \"33333xxxxxxxxx0000016-1\",\n" +
                "                \"logisticsStatus\": \"1\"\n" +
                "            }\n" +
                "        ],\n" +
                "        \"type\": \"1\",\n" +
                "        \"warehouseOrderCode\": \"GYS0000571201710180000530\"\n" +
                "    }";
        returnTypeDO2.setResult(result2);
        when(ijdService.getLogisticsInfo(anyString(), eq("0"))).thenReturn(returnTypeDO);
        when(ijdService.getLogisticsInfo(anyString(), eq("1"))).thenReturn(returnTypeDO2);

        IRealIpService iRealIpService = mock(IRealIpService.class);
        scmOrderBiz.setiRealIpService(iRealIpService);
        when(iRealIpService.isRealTimerService()).thenReturn(true);

        ITrcService trcService = mock(ITrcService.class);
        scmOrderBiz.setTrcService(trcService);
        ToGlyResultDO resultDO = new ToGlyResultDO();
        resultDO.setStatus("1");
        resultDO.setMsg("发送物流信息给泰然城成功");
        when(trcService.sendLogisticInfoNotice(any(LogisticNoticeForm.class))).thenReturn(resultDO);
    }
	
    private String createOrderInfo(){
        TrcOrderForm orderForm  = new TrcOrderForm();
        //创建平台订单信息
        TrcPlatformOrder platformOrder = JSON.parseObject("{\"adjustFee\": 0,\"anony\": 0,\"buyerArea\": \"110100/110101\",\"cancelReason\": \"现在不想购买\",\"cancelStatus\": \"SUCCESS\",\"channelCode\": \"QD001\",\"sellCode\": \"QD001\",\"couponCode\": \"\",\"createTime\": 1471683422,\"discountCouponPlatform\": 0,\"discountCouponShop\": 0,\"discountFee\": 0,\"discountPromotion\": 0,\"endTime\": 1471919905,\"groupBuyStatus\": \"NO_APPLY\",\"invoiceMain\": \"浙江小泰科技有限公司\",\"invoiceType\": \"2\",\"ip\": \"118.178.15.71\",\"isClearing\": 0,\"isDeleted\": \"0\",\"isVirtual\": 0,\"itemNum\": 6,\"needInvoice\": \"0\",\"obtainPointFee\": 180,\"payTime\": 1508206933000,\"payType\": \"online\",\"payment\": 225,\"platformCode\": \"QD001\",\"platformOrderCode\": \"1608201657240531\",\"platformType\": \"wap\",\"pointsFee\": 0,\"postageFee\": 39,\"rateStatus\": 0,\"receiveTime\": 1471919905,\"receiverAddress\": \"余杭区\",\"receiverCity\": \"杭州市\",\"receiverDistrict\": \"余杭区\",\"receiverEmail\": \"471869639@qq.com\",\"receiverIdCard\": \"420281198602197693\",\"receiverMobile\": \"15068839416\",\"receiverName\": \"熊9测试\",\"receiverPhone\": \"15068839416\",\"receiverProvince\": \"浙江省\",\"receiverZip\": \"232656\",\"shippingType\": \"express\",\"status\": \"TRADE_CLOSED_BY_CANCEL\",\"totalFee\": 184,\"totalTax\": 2,\"type\": 0,\"userId\": \"531\",\"userName\": \"15229896960\"}").toJavaObject(TrcPlatformOrder.class);
        orderForm.setPlatformOrder(platformOrder);
        //创建店铺订单信息
        List<TrcShopOrderForm> shopOrders = new ArrayList<>();
        TrcShopOrder shopOrder = JSON.parseObject("{\"adjustFee\": 0,\"channelCode\": \"QD001\",\"sellCode\": \"QD001\",\"consignTime\": 1468559766,\"createTime\": 1468555312,\"discountCouponPlatform\": 0,\"discountCouponShop\": 0,\"discountFee\": 0,\"discountPromotion\": 0,\"dlytmplIds\": \"4\",\"groupBuyStatus\": \"NO_APPLY\",\"isDeleted\": \"0\",\"isPartConsign\": \"0\",\"itemNum\": 3,\"payment\": 55,\"platformCode\": \"QD001\",\"platformOrderCode\": \"1608201657240531\",\"platformType\": \"wap\",\"postageFee\": 19,\"rateStatus\": \"1\",\"shopId\": 4,\"shopName\": \"泰然直营（自营店铺）（自营店铺）\",\"shopOrderCode\": \"33333xxxxxxxxx0000015\",\"status\": \"TRADE_FINISHED\",\"title\": \"订单明细介绍\",\"totalFee\": 35,\"totalTax\": 1,\"totalWeight\": 0,\"tradeMemo\": \"13588129773\",\"userId\": \"5\"}").toJavaObject(TrcShopOrder.class);
        List<TrcOrderItem> orderItems = JSONArray.parseArray("[{\"id\": \"3333\",\"adjustFee\": 0,\"barCode\": \"\",\"catServiceRate\": 0,\"category\": \"232\",\"channelCode\": \"QD001\",\"sellCode\": \"QD001\",\"complaintsStatus\": \"NOT_COMPLAINTS\",\"consignTime\": 1468559766,\"createTime\": 1468555312,\"customsPrice\": 0,\"discountCouponPlatform\": 0,\"discountCouponShop\": 0,\"discountFee\": 0,\"discountPromotion\": 0,\"dlytmplId\": 4,\"endTime\": 1468568194,\"isOversold\": false,\"itemName\": \"佳能（Glad）背心袋抽取式保鲜袋大号中号组合装BCB30+BCB25\",\"itemNo\": \"yidonghuafei20\",\"marketPrice\": 19.94,\"num\": 2,\"objType\": \"recharge\",\"outerSkuId\": \"SP1201710120000627\",\"oversold\": false,\"params\": \"[]\",\"payTime\": 1468555346,\"payment\": 36,\"picPath\": \"\",\"platformCode\": \"QD001\",\"platformOrderCode\": \"1608201657240531\",\"postDiscount\": 10,\"price\": 13,\"priceTax\": 0,\"promotionPrice\": 0,\"promotionTags\": \"\",\"refundFee\": 0,\"shopId\": 4,\"shopName\": \"泰然直营（自营店铺）（自营店铺）\",\"shopOrderCode\": \"33333xxxxxxxxx0000015\",\"skuCode\": \"\",\"status\": \"TRADE_FINISHED\",\"subStock\": false,\"taxRate\": 0,\"totalFee\": 26,\"totalWeight\": 0,\"transactionPrice\": 19.94,\"type\": \"4\",\"userId\": \"5\"},{\"id\": \"4444\",\"adjustFee\": 0,\"barCode\": \"\",\"catServiceRate\": 0,\"category\": \"64\",\"channelCode\": \"QD001\",\"sellCode\": \"QD001\",\"complaintsStatus\": \"NOT_COMPLAINTS\",\"consignTime\": 1468918407,\"createTime\": 1468557443,\"customsPrice\": 0,\"discountCouponPlatform\": 0,\"discountCouponShop\": 0,\"discountFee\": 0,\"discountPromotion\": 0,\"dlytmplId\": 1,\"endTime\": 1469685165,\"isOversold\": false,\"itemName\": \"小米（MI）7号电池 彩虹电池碱性 7号（10粒装）\",\"itemNo\": \"310520151011501429\",\"marketPrice\": 52,\"num\": 1,\"objType\": \"item\",\"outerSkuId\": \"SP1201710120000628\",\"oversold\": false,\"params\": \"[]\",\"payTime\": 1468557489,\"payment\": 19,\"picPath\": \"https://image.trc.com/c8/f8/ed/d880b82a6d258c0e357cafa1390d32e54e9106f6.jpg\",\"platformCode\": \"QD001\",\"platformOrderCode\": \"1608201657240531\",\"postDiscount\": 9,\"price\": 9,\"priceTax\": 1,\"promotionPrice\": 0,\"promotionTags\": \"\",\"refundFee\": 0,\"shopId\": 2,\"shopName\": \"泰然直营1（自营店铺）\",\"shopOrderCode\": \"33333xxxxxxxxx0000015\",\"skuCode\": \"\",\"status\": \"TRADE_FINISHED\",\"subStock\": false,\"taxRate\": 0,\"totalFee\": 9,\"totalWeight\": 0.1,\"transactionPrice\": 57,\"type\": \"0\",\"userId\": \"10\"}]", TrcOrderItem.class);
        TrcShopOrderForm trcShopOrderForm = new TrcShopOrderForm();
        trcShopOrderForm.setShopOrder(shopOrder);
        trcShopOrderForm.setOrderItems(orderItems);
        shopOrders.add(trcShopOrderForm);
        TrcShopOrder shopOrder2 = JSON.parseObject("{\"adjustFee\": 0,\"channelCode\": \"QD001\",\"sellCode\": \"QD001\",\"consignTime\": 1468918407,\"createTime\": 1468557443,\"discountCouponPlatform\": 0,\"discountCouponShop\": 0,\"discountFee\": 0,\"discountPromotion\": 0,\"dlytmplIds\": \"1\",\"groupBuyStatus\": \"NO_APPLY\",\"isDeleted\": \"0\",\"isPartConsign\": \"0\",\"itemNum\": 3,\"payment\": 170,\"platformCode\": \"QD001\",\"platformOrderCode\": \"1608201657240531\",\"platformType\": \"wap\",\"postageFee\": 20,\"rateStatus\": \"1\",\"shopId\": 2,\"shopName\": \"泰然直营1（自营店铺）\",\"shopOrderCode\": \"33333xxxxxxxxx0000016\",\"status\": \"TRADE_FINISHED\",\"title\": \"订单明细介绍\",\"totalFee\": 149,\"totalTax\": 1,\"totalWeight\": 0.1,\"tradeMemo\": \"\",\"userId\": \"10\"}").toJavaObject(TrcShopOrder.class);
        List<TrcOrderItem> orderItems2 = JSONArray.parseArray("[{\"id\": \"1111\",\"adjustFee\": 0,\"barCode\": \"\",\"catServiceRate\": 0,\"category\": \"37\",\"channelCode\": \"QD001\",\"sellCode\": \"QD001\",\"complaintsStatus\": \"NOT_COMPLAINTS\",\"createTime\": 1468558351,\"customsPrice\": 0,\"discountCouponPlatform\": 0,\"discountCouponShop\": 0,\"discountFee\": 0,\"discountPromotion\": 0,\"dlytmplId\": 1,\"endTime\": 1468568307,\"isOversold\": false,\"itemName\": \"保加利亚奥伯伦（Oberon）蜂蜜 椴树蜜\",\"itemNo\": \"310520141011500548\",\"marketPrice\": 458,\"num\": 2,\"objType\": \"item\",\"outerSkuId\": \"SP1201709070000580\",\"oversold\": false,\"params\": \"[]\",\"payment\": 85,\"picPath\": \"https://image.trc.com/0b/29/a6/2caf0f806173ee02d9d7c99c3d7286deaca2032a.jpg\",\"platformCode\": \"QD001\",\"platformOrderCode\": \"1608201657240531\",\"postDiscount\": 10,\"price\": 37,\"priceTax\": 1,\"promotionPrice\": 0,\"promotionTags\": \"\",\"refundFee\": 0,\"shopId\": 2,\"shopName\": \"泰然直营1（自营店铺）\",\"shopOrderCode\": \"33333xxxxxxxxx0000016\",\"skuCode\": \"\",\"status\": \"TRADE_CLOSED_BY_CANCEL\",\"subStock\": false,\"taxRate\": 0,\"totalFee\": 74,\"totalWeight\": 5,\"transactionPrice\": 467,\"type\": \"0\",\"userId\": \"5\"},{\"id\": \"2222\",\"adjustFee\": 0,\"barCode\": \"\",\"catServiceRate\": 0,\"category\": \"102\",\"channelCode\": \"QD001\",\"sellCode\": \"QD001\",\"complaintsStatus\": \"NOT_COMPLAINTS\",\"createTime\": 1468558437,\"customsPrice\": 0,\"discountCouponPlatform\": 0,\"discountCouponShop\": 0,\"discountFee\": 0,\"discountPromotion\": 0,\"dlytmplId\": 2,\"endTime\": 1468818061,\"isOversold\": false,\"itemName\": \"【单品包邮】日版MUHI池田模范堂 儿童液体无比滴S2a清凉冷感止痒露药水40ml\",\"itemNo\": \"310520151011501513\",\"marketPrice\": 127,\"num\": 1,\"objType\": \"item\",\"outerSkuId\": \"SP1201710170000630\",\"oversold\": false,\"params\": \"[]\",\"payment\": 85,\"picPath\": \"https://image.trc.com/86/5a/17/c1f0a338d700ffb951c6b8f7cf257978383ed482.jpg\",\"platformCode\": \"QD001\",\"platformOrderCode\": \"1608201657240531\",\"postDiscount\": 10,\"price\": 75,\"priceTax\": 0,\"promotionPrice\": 0,\"promotionTags\": \"\",\"refundFee\": 0,\"shopId\": 2,\"shopName\": \"泰然直营1（自营店铺）\",\"shopOrderCode\": \"33333xxxxxxxxx0000016\",\"skuCode\": \"\",\"status\": \"TRADE_CLOSED_BY_CANCEL\",\"subStock\": false,\"taxRate\": 0,\"totalFee\": 75,\"totalWeight\": 0.4,\"transactionPrice\": 131.5,\"type\": \"0\",\"userId\": \"4\"}]", TrcOrderItem.class);
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
//        System.out.println(JSON.toJSONString(orderForm));
        return JSON.toJSONString(orderForm);
    }
    
    @Test
    public void testUpdateWarehouseOrder () {
    	//scmOrderBiz.updateWarehouseOrderSupplierOrderStatus("GYS0000571201710200000541");
    }
	
	
}
