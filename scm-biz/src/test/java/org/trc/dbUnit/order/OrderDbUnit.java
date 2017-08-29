package org.trc.dbUnit.order;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.trc.biz.order.IScmOrderBiz;
import org.trc.service.BaseTest;

/**
 * Created by hzwdx on 2017/8/29.
 */
public class OrderDbUnit extends BaseTest{

    private Logger log = LoggerFactory.getLogger(OrderDbUnit.class);

    @Autowired
    private IScmOrderBiz scmOrderBiz;


    @Test
    public void testReciveChannelOrder() throws Exception{
        //删除原数据
        execSql(conn,"delete from platform_order");
        execSql(conn,"delete from shop_order");
        execSql(conn,"delete from order_item");
        execSql(conn,"delete from warehouse_order");
        execSql(conn,"delete from sku_relation");
        execSql(conn,"delete from supplier_order_info");
        execSql(conn,"delete from supplier_order_logistics");
        //从xml文件读取数据并插入数据库中
        prepareData(conn, "order/preInsertskuRelation.xml");
        //测试接收渠道订单
        scmOrderBiz.reciveChannelOrder(createOrderInfo());
    }

    /**
     * 创建订单信息
     * @return
     */
    private String createOrderInfo(){
        return "{\"noticeNum\":\"1503998005466\",\"operateTime\":1503998005466,\"platformOrder\":{\"adjustFee\":0.000,\"anony\":0,\"buyerArea\":\"610000/610100/610112\",\"cancelStatus\":\"NO_APPLY_CANCEL\",\"channelCode\":\"QD001\",\"consignTime\":1479700082,\"couponCode\":\"\",\"createTime\":1479523029,\"discountCouponPlatform\":0.000,\"discountCouponShop\":0.000,\"discountFee\":0.000,\"discountPromotion\":0.000,\"endTime\":1480563577,\"groupBuyStatus\":\"NO_APPLY\",\"invoiceMain\":\"浙江小泰科技有限公司\",\"invoiceType\":\"2\",\"ip\":\"121.43.18.24\",\"isClearing\":1,\"isDeleted\":\"0\",\"isVirtual\":0,\"itemNum\":4,\"needInvoice\":\"0\",\"obtainPointFee\":249,\"payTime\":1503997964000,\"payType\":\"online\",\"payment\":68.8,\"platformCode\":\"QD001\",\"platformOrderCode\":\"1611191037104616\",\"platformType\":\"wap\",\"pointsFee\":0.000,\"postageFee\":20,\"rateStatus\":1,\"receiveTime\":1480563577,\"receiverAddress\":\"余杭区\",\"receiverCity\":\"杭州市\",\"receiverDistrict\":\"余杭区\",\"receiverEmail\":\"471869639@qq.com\",\"receiverIdCard\":\"420281198602197693\",\"receiverMobile\":\"13872072544\",\"receiverName\":\"熊大\",\"receiverPhone\":\"13872072544\",\"receiverProvince\":\"浙江省\",\"shippingType\":\"express\",\"status\":\"TRADE_FINISHED\",\"totalFee\":47.8,\"totalTax\":1,\"type\":0,\"userId\":\"14616\",\"userName\":\"13572115122\"},\"shopOrders\":[{\"orderItems\":[{\"adjustFee\":0.0,\"barCode\":\"\",\"catServiceRate\":0,\"category\":\"232\",\"channelCode\":\"QD001\",\"complaintsStatus\":\"NOT_COMPLAINTS\",\"consignTime\":1468559766,\"createTime\":1468555312,\"customsPrice\":0.0,\"discountCouponPlatform\":0.0,\"discountCouponShop\":0.0,\"discountFee\":0.0,\"discountPromotion\":0.0,\"dlytmplId\":4,\"endTime\":1468568194,\"isOversold\":false,\"itemName\":\"【自营仓配】阿奇猫 安卓手机数据线/2A充电线 适用于三星/华为/小米等 白色\",\"itemNo\":\"yidonghuafei20\",\"marketPrice\":19.94,\"num\":2,\"objType\":\"recharge\",\"outerSkuId\":\"SP1201707250000025\",\"oversold\":false,\"params\":\"[]\",\"payTime\":1468555346,\"payment\":48.3,\"picPath\":\"\",\"platformCode\":\"QD001\",\"platformOrderCode\":\"1611191037104616\",\"postDiscount\":10,\"price\":18.9,\"priceTax\":0.5,\"promotionPrice\":0.0,\"promotionTags\":\"\",\"refundFee\":0.0,\"shopId\":4,\"shopName\":\"泰然直营（自营店铺）（自营店铺）\",\"shopOrderCode\":\"1607151201350005\",\"skuCode\":\"\",\"status\":\"TRADE_FINISHED\",\"subStock\":false,\"taxRate\":0.0,\"totalFee\":37.8,\"totalWeight\":0.0,\"transactionPrice\":19.94,\"type\":\"4\",\"userId\":\"5\"}],\"shopOrder\":{\"adjustFee\":0.0,\"channelCode\":\"QD001\",\"consignTime\":1468559766,\"createTime\":1468555312,\"discountCouponPlatform\":0.0,\"discountCouponShop\":0.0,\"discountFee\":0.0,\"discountPromotion\":0.0,\"dlytmplIds\":\"4\",\"groupBuyStatus\":\"NO_APPLY\",\"isDeleted\":\"0\",\"isPartConsign\":\"0\",\"itemNum\":2,\"payment\":48.3,\"platformCode\":\"QD001\",\"platformOrderCode\":\"1611191037104616\",\"platformType\":\"wap\",\"postageFee\":10,\"rateStatus\":\"1\",\"shopId\":4,\"shopName\":\"泰然直营（自营店铺）（自营店铺）\",\"shopOrderCode\":\"000xxxxxxxxx000041\",\"status\":\"TRADE_FINISHED\",\"title\":\"订单明细介绍\",\"totalFee\":37.8,\"totalTax\":0.5,\"totalWeight\":0.0,\"tradeMemo\":\"13588129773\",\"userId\":\"5\"}},{\"orderItems\":[{\"adjustFee\":0.0,\"barCode\":\"\",\"catServiceRate\":0,\"category\":\"64\",\"channelCode\":\"QD001\",\"complaintsStatus\":\"NOT_COMPLAINTS\",\"consignTime\":1468918407,\"createTime\":1468557443,\"customsPrice\":0.0,\"discountCouponPlatform\":0.0,\"discountCouponShop\":0.0,\"discountFee\":0.0,\"discountPromotion\":0.0,\"dlytmplId\":1,\"endTime\":1469685165,\"isOversold\":false,\"itemName\":\"浙粮定制高级葡萄酒礼品皮盒（不带酒具）\",\"itemNo\":\"310520151011501429\",\"marketPrice\":52.0,\"num\":2,\"objType\":\"item\",\"outerSkuId\":\"SP1201707250000030\",\"oversold\":false,\"params\":\"[]\",\"payTime\":1468557489,\"payment\":20.5,\"picPath\":\"https://image.trc.com/c8/f8/ed/d880b82a6d258c0e357cafa1390d32e54e9106f6.jpg\",\"platformCode\":\"QD001\",\"platformOrderCode\":\"1611191037104616\",\"postDiscount\":10,\"price\":5,\"priceTax\":0.5,\"promotionPrice\":0.0,\"promotionTags\":\"\",\"refundFee\":0.0,\"shopId\":2,\"shopName\":\"泰然直营1（自营店铺）\",\"shopOrderCode\":\"1607151237030010\",\"skuCode\":\"\",\"status\":\"TRADE_FINISHED\",\"subStock\":false,\"taxRate\":0.0,\"totalFee\":10,\"totalWeight\":0.1,\"transactionPrice\":57.0,\"type\":\"0\",\"userId\":\"10\"}],\"shopOrder\":{\"adjustFee\":0.0,\"channelCode\":\"QD001\",\"consignTime\":1468918407,\"createTime\":1468557443,\"discountCouponPlatform\":0.0,\"discountCouponShop\":0.0,\"discountFee\":0.0,\"discountPromotion\":0.0,\"dlytmplIds\":\"1\",\"groupBuyStatus\":\"NO_APPLY\",\"isDeleted\":\"0\",\"isPartConsign\":\"0\",\"itemNum\":2,\"payment\":20.5,\"platformCode\":\"QD001\",\"platformOrderCode\":\"1611191037104616\",\"platformType\":\"wap\",\"postageFee\":10,\"rateStatus\":\"1\",\"shopId\":2,\"shopName\":\"泰然直营1（自营店铺）\",\"shopOrderCode\":\"000xxxxxxxxx000042\",\"status\":\"TRADE_FINISHED\",\"title\":\"订单明细介绍\",\"totalFee\":10,\"totalTax\":0.5,\"totalWeight\":0.1,\"tradeMemo\":\"\",\"userId\":\"10\"}}],\"sign\":\"01583b887f3420f262ab282920c73394eaa79e139b3fdda98fd120f6a6d75dc6\"}";
    }


}
