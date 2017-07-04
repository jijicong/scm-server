package org.trc.biz.impl.trc;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import org.trc.biz.trc.IOrderBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.goods.ExternalItemSku;
import org.trc.domain.order.*;
import org.trc.enums.ExceptionEnum;
import org.trc.enums.ZeroToNineEnum;
import org.trc.exception.OrderException;
import org.trc.exception.TrcException;
import org.trc.service.ITrcService;
import org.trc.service.goods.IExternalItemSkuService;
import org.trc.service.order.*;
import org.trc.service.util.ISerialUtilService;
import org.trc.util.*;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.*;

/**
 * Created by ding on 2017/6/23.
 */
@Service("orderBiz")
public class OrderBiz implements IOrderBiz {

    private Logger logger = LoggerFactory.getLogger(OrderBiz.class);


    @Resource
    private IExternalItemSkuService externalItemSkuService;

    @Resource
    private IOrderItemService orderItemService;

    @Resource
    private IPlatformOrderService platformOrderService;

    @Resource
    private IShopOrderService shopOrderService;

    @Resource
    private ISerialUtilService serialUtilService;

    @Resource
    private IWarehouseOrderService warehouseOrderService;

    @Resource
    private IOrderFlowService orderFlowService;

    @Resource
    private ISupplierOrderInfoService supplierOrderInfoService;

    @Resource
    private ITrcService trcService;

    @Value("{trc.jd.logistic.url}")
    private String TRC_JD_LOGISTIC_URL;

    private String SP0 = "SP0";

    private String SP1 = "SP1";

    private String ONE = "1";

    private String ZERO = "0";

    //业务类型：交易
    public final static String BIZ_TYPE_DEAL = "DEAL";




    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public AppResult<String> reciveChannelOrder(String orderInfo) {
        JSONObject orderObj = getChannelOrder(orderInfo);
        //获取平台订单信息
        PlatformOrder platformOrder = getPlatformOrder(orderObj);
        platformOrderParamCheck(platformOrder);
        JSONArray shopOrderArray = getShopOrdersArray(orderObj);
        //获取店铺订单
        List<ShopOrder> shopOrderList = getShopOrderList(platformOrder, shopOrderArray);
        //保存幂等流水
        saveIdempotentFlow(shopOrderList);
        //拆分仓库订单
        List<WarehouseOrder> warehouseOrderList = new ArrayList<WarehouseOrder>();
        for (ShopOrder shopOrder : shopOrderList) {
            warehouseOrderList.addAll(dealShopOrder(shopOrder));
        }
        //订单商品明细
        List<OrderItem> orderItemList = new ArrayList<OrderItem>();
        for (WarehouseOrder warehouseOrder : warehouseOrderList) {
            orderItemList.addAll(warehouseOrder.getOrderItemList());
        }
        orderItemService.insertList(orderItemList);
        //保存仓库订单
        warehouseOrderService.insertList(warehouseOrderList);
        //保存商铺订单
        shopOrderService.insertList(shopOrderList);
        //保存平台订单
        platformOrderService.insert(platformOrder);

        //TODO 根据供应商信息分别调接口*/

        return ResultUtil.createSucssAppResult("接收订单成功", "");
    }

    @Override
    public AppResult<String> getJDLogistics(String shopOrderCode)throws Exception {
        WarehouseOrder warehouseOrder = new WarehouseOrder();
        warehouseOrder.setShopOrderCode(shopOrderCode);
        warehouseOrder.setSupplierCode(SupplyConstants.Order.SUPPLIER_JD_CODE);
        //一个店铺订单下只有一个京东仓库订单
        warehouseOrder = warehouseOrderService.selectOne(warehouseOrder);
        SupplierOrderInfo supplierOrderInfo = new SupplierOrderInfo();
        supplierOrderInfo.setWarehouseOrderCode(warehouseOrder.getWarehouseOrderCode());
        supplierOrderInfo = supplierOrderInfoService.selectOne(supplierOrderInfo);
        String result = trcService.getJDLogistic(TRC_JD_LOGISTIC_URL+supplierOrderInfo.getSupplierOrderCode());
        if (StringUtils.isEmpty(result)){
            logger.error(ExceptionEnum.TRC_LOGISTIC_EXCEPTION.getMessage());
            throw new TrcException(ExceptionEnum.TRC_LOGISTIC_EXCEPTION, ExceptionEnum.TRC_LOGISTIC_EXCEPTION.getMessage());
        }
        return ResultUtil.createSucssAppResult("查询订单信息成功",result);
    }

    private JSONObject getChannelOrder(String orderInfo){
        JSONObject orderObj = null;
        try {
            orderObj = JSONObject.parseObject(orderInfo);
        } catch (ClassCastException e) {
            String msg = String.format("渠道同步订单参数不是JSON格式");
            logger.error(msg, e);
            throw new OrderException(ExceptionEnum.CHANNEL_ORDER_DATA_NOT_JSON_EXCEPTION, msg);
        }
        AssertUtil.notNull(orderObj, "接收渠道订单参数中平台订单信息为空");
        return orderObj;
    }

    /**
     * 获取平台订单
     * @param orderObj
     * @return
     */
    private PlatformOrder getPlatformOrder(JSONObject orderObj){
        JSONObject platformObj = null;
        try{
            platformObj = orderObj.getJSONObject("platformOrder");
        }catch (ClassCastException e){
            String msg = String.format("平台订单信息转JSON错误");
            logger.error(msg, e);
            throw new OrderException(ExceptionEnum.CHANNEL_ORDER_DATA_NOT_JSON_EXCEPTION, msg);
        }
        AssertUtil.notNull(platformObj, "接收渠道订单参数中平台订单信息为空");
        PlatformOrder platformOrder = platformObj.toJavaObject(PlatformOrder.class);
        platformOrder.setPayment(CommonUtil.getMoneyLong(platformObj.getDouble("payment")));//实付金额
        platformOrder.setPostageFee(CommonUtil.getMoneyLong(platformObj.getDouble("postageFee")));//积分抵扣金额
        platformOrder.setTotalFee(CommonUtil.getMoneyLong(platformObj.getDouble("totalFee")));//订单总金额
        platformOrder.setAdjustFee(CommonUtil.getMoneyLong(platformObj.getDouble("adjustFee")));//卖家手工调整金额
        platformOrder.setPointsFee(CommonUtil.getMoneyLong(platformObj.getDouble("pointsFee")));//邮费
        platformOrder.setTotalTax(CommonUtil.getMoneyLong(platformObj.getDouble("totalTax")));//总税费
        platformOrder.setStepPaidFee(CommonUtil.getMoneyLong(platformObj.getDouble("stepPaidFee")));//分阶段已付金额
        platformOrder.setDiscountPromotion(CommonUtil.getMoneyLong(platformObj.getDouble("discountPromotion")));//促销优惠总金额
        platformOrder.setDiscountCouponShop(CommonUtil.getMoneyLong(platformObj.getDouble("discountCouponShop")));//店铺优惠卷优惠金额
        platformOrder.setDiscountCouponPlatform(CommonUtil.getMoneyLong(platformObj.getDouble("discountCouponPlatform")));//平台优惠卷优惠金额
        platformOrder.setDiscountFee(CommonUtil.getMoneyLong(platformObj.getDouble("discountFee")));//订单优惠总金额
        return platformOrder;
    }

    /**
     * 获取店铺订单信息JSON数据
     * @param orderObj
     * @return
     */
    private JSONArray getShopOrdersArray(JSONObject orderObj){
        JSONArray shopOrderArray = null;
        try{
            shopOrderArray = orderObj.getJSONArray("shopOrders");
        }catch (ClassCastException e){
            String msg = String.format("店铺订单信息转JSON错误");
            logger.error(msg, e);
            throw new OrderException(ExceptionEnum.CHANNEL_ORDER_DATA_NOT_JSON_EXCEPTION, msg);
        }
        if(shopOrderArray.size() == 0){
            String msg = String.format("店铺订单信息为空");
            logger.error(msg);
            throw new OrderException(ExceptionEnum.CHANNEL_ORDER_DATA_NOT_JSON_EXCEPTION, msg);
        }
        return shopOrderArray;
    }

    /**
     * 保存请求流水
     *
     * @param orderInfo
     */
    private void saveRequestFlow(String orderInfo) {

    }

    /**
     * 保存幂等流水
     *
     * @param shopOrderList
     */
    private void saveIdempotentFlow(List<ShopOrder> shopOrderList) {
        try {
            for (ShopOrder shopOrder : shopOrderList) {
                OrderFlow orderFlow = new OrderFlow();
                orderFlow.setPlatformOrderCode(shopOrder.getPlatformOrderCode());
                orderFlow.setShopOrderCode(shopOrder.getShopOrderCode());
                orderFlow.setType(BIZ_TYPE_DEAL);
                int count = orderFlowService.insert(orderFlow);
                if (count == 0) {
                    String msg = String.format("保存订单同步幂等流水%s失败", JSONObject.toJSON(orderFlow));
                    logger.error(msg);
                    throw new OrderException(ExceptionEnum.ORDER_IDEMPOTENT_SAVE_EXCEPTION, msg);
                }
            }
        } catch (DuplicateKeyException e) {
            logger.error("重复提交订单: " + e.getMessage());
            throw new OrderException(ExceptionEnum.TRC_ORDER_PUSH_EXCEPTION, "重复提交订单：" + e.getMessage());
        }


    }

    /**
     * 获取店铺订单
     *
     * @param platformOrder
     * @param shopOrderArray
     * @return
     */
    private List<ShopOrder> getShopOrderList(PlatformOrder platformOrder, JSONArray shopOrderArray) {
        List<ShopOrder> shopOrderList = new ArrayList<ShopOrder>();
        Integer totalNum = 0;
        Long totalShop = 0L;
        for (Object obj : shopOrderArray) {
            JSONObject tmpObj = (JSONObject) obj;
            JSONObject shopOrderObj = tmpObj.getJSONObject("shopOrder");
            AssertUtil.notNull(shopOrderObj, "接收渠道订单参数中平店铺订单信息为空");
            ShopOrder shopOrder = shopOrderObj.toJavaObject(ShopOrder.class);
            shopOrderParamCheck(shopOrder);
            //设置店铺金额
            setShopOrderFee(shopOrder, shopOrderObj);
            JSONArray orderItemArray = tmpObj.getJSONArray("orderItems");
            AssertUtil.notEmpty(orderItemArray, String.format("接收渠道订单参数中平店铺订单%s相关商品订单明细信息为空为空", shopOrderObj));
            //获取订单商品明细
            List<OrderItem> orderItemList = getOrderItem(orderItemArray);
            totalShop += shopOrder.getPayment();
            totalNum += shopOrder.getItemNum();
            shopOrder.setOrderItems(orderItemList);
            Integer totalOneShopNum = 0;
            Long totalItem = 0L;
            for (OrderItem orderItem : orderItemList) {
                orderItemsParamCheck(orderItem);
                totalItem += orderItem.getPayment();
                totalOneShopNum += orderItem.getNum();
            }
            AssertUtil.isTrue(totalItem.longValue() == shopOrder.getPayment().longValue(), "店铺订单实付金额与所有该店铺商品总实付金额不等值");
            AssertUtil.isTrue(totalOneShopNum.intValue() == shopOrder.getItemNum().intValue(), "店铺订单商品总数与所有该店铺商品总数不等值");
            shopOrderList.add(shopOrder);
        }
        AssertUtil.isTrue(totalShop.longValue() == platformOrder.getPayment().longValue(), "平台订单实付金额与所有店铺总实付金额不等值");
        AssertUtil.isTrue(totalNum.intValue() == platformOrder.getItemNum().intValue(), "平台订单商品总数与所有店铺商品总数不等值");
        return shopOrderList;
    }

    /**
     * 设置店铺金额
     * @param shopOrder
     * @param shopOrderObj
     */
    private void setShopOrderFee(ShopOrder shopOrder, JSONObject shopOrderObj){
        shopOrder.setPayment(CommonUtil.getMoneyLong(shopOrderObj.getDouble("payment")));//实付金额
        shopOrder.setPostageFee(CommonUtil.getMoneyLong(shopOrderObj.getDouble("postageFee")));//积分抵扣金额
        shopOrder.setTotalFee(CommonUtil.getMoneyLong(shopOrderObj.getDouble("totalFee")));//订单总金额
        shopOrder.setAdjustFee(CommonUtil.getMoneyLong(shopOrderObj.getDouble("adjustFee")));//卖家手工调整金额
        shopOrder.setTotalTax(CommonUtil.getMoneyLong(shopOrderObj.getDouble("totalTax")));//总税费
        shopOrder.setDiscountPromotion(CommonUtil.getMoneyLong(shopOrderObj.getDouble("discountPromotion")));//促销优惠总金额
        shopOrder.setDiscountCouponShop(CommonUtil.getMoneyLong(shopOrderObj.getDouble("discountCouponShop")));//店铺优惠卷优惠金额
        shopOrder.setDiscountCouponPlatform(CommonUtil.getMoneyLong(shopOrderObj.getDouble("discountCouponPlatform")));//平台优惠卷优惠金额
        shopOrder.setDiscountFee(CommonUtil.getMoneyLong(shopOrderObj.getDouble("discountFee")));//订单优惠总金额
    }

    /**
     * 获取订单商品明细
     * @param orderItemArray
     * @return
     */
    private List<OrderItem> getOrderItem(JSONArray orderItemArray){
        List<OrderItem> orderItemList = new ArrayList<OrderItem>();
        for(Object obj: orderItemArray){
            JSONObject orderItemObj = (JSONObject)obj;
            OrderItem orderItem = orderItemObj.toJavaObject(OrderItem.class);
            orderItem.setPayment(CommonUtil.getMoneyLong(orderItemObj.getDouble("payment")));//实付金额
            orderItem.setTotalFee(CommonUtil.getMoneyLong(orderItemObj.getDouble("totalFee")));//订单总金额
            orderItem.setAdjustFee(CommonUtil.getMoneyLong(orderItemObj.getDouble("adjustFee")));//卖家手工调整金额
            orderItem.setDiscountPromotion(CommonUtil.getMoneyLong(orderItemObj.getDouble("discountPromotion")));//促销优惠总金额
            orderItem.setDiscountCouponShop(CommonUtil.getMoneyLong(orderItemObj.getDouble("discountCouponShop")));//店铺优惠卷优惠金额
            orderItem.setDiscountCouponPlatform(CommonUtil.getMoneyLong(orderItemObj.getDouble("discountCouponPlatform")));//平台优惠卷优惠金额
            orderItem.setDiscountFee(CommonUtil.getMoneyLong(orderItemObj.getDouble("discountFee")));//订单优惠总金额
            orderItem.setPostDiscount(CommonUtil.getMoneyLong(orderItemObj.getDouble("postDiscount")));//运费分摊
            orderItem.setRefundFee(CommonUtil.getMoneyLong(orderItemObj.getDouble("refundFee")));//退款金额
            orderItem.setPriceTax(CommonUtil.getMoneyLong(orderItemObj.getDouble("priceTax")));//商品税费
            orderItem.setPrice(CommonUtil.getMoneyLong(orderItemObj.getDouble("price")));//商品价格
            orderItem.setMarketPrice(CommonUtil.getMoneyLong(orderItemObj.getDouble("marketPrice")));//市场价
            orderItem.setPromotionPrice(CommonUtil.getMoneyLong(orderItemObj.getDouble("promotionPrice")));//促销价
            orderItem.setCustomsPrice(CommonUtil.getMoneyLong(orderItemObj.getDouble("customsPrice")));//报关单价
            orderItem.setTransactionPrice(CommonUtil.getMoneyLong(orderItemObj.getDouble("transactionPrice")));//成交单价
            orderItem.setTotalWeight(CommonUtil.getMoneyLong(orderItemObj.getDouble("totalWeight")));//商品重量
            orderItemList.add(orderItem);
        }
        return orderItemList;
    }

    /**
     * 平台订单校验
     *
     * @param platformOrder
     */
    private void platformOrderParamCheck(PlatformOrder platformOrder) {
        AssertUtil.notBlank(platformOrder.getChannelCode(), "渠道编码不能为空");
        AssertUtil.notBlank(platformOrder.getPlatformCode(), "来源平台编码不能为空");
        AssertUtil.notBlank(platformOrder.getPlatformOrderCode(), "平台订单编码不能为空");
        AssertUtil.notBlank(platformOrder.getUserId(), "会员id不能为空");
        AssertUtil.notBlank(platformOrder.getUserName(), "会员名称不能为空");
        AssertUtil.notNull(platformOrder.getAdjustFee(), "卖家手工调整金额不能为空");
        AssertUtil.isTrue(platformOrder.getAdjustFee() >= 0, "卖家手工调整金额应大于等于0");
        AssertUtil.notNull(platformOrder.getTotalFee(), "订单总金额不能为空");
        AssertUtil.isTrue(platformOrder.getTotalFee() >= 0, "订单总金额应大于等于0");
        AssertUtil.notNull(platformOrder.getPostageFee(), "邮费不能为空");
        AssertUtil.isTrue(platformOrder.getPostageFee() >= 0, "邮费应大于等于0");
        AssertUtil.notNull(platformOrder.getTotalTax(), "总税费不能为空");
        AssertUtil.isTrue(platformOrder.getTotalTax() >= 0, "总税费应大于等于0");
        AssertUtil.notNull(platformOrder.getPayment(), "实付金额不能为空");
        AssertUtil.isTrue(platformOrder.getPayment() >= 0, "实付金额应大于等于0");
        AssertUtil.notBlank(platformOrder.getPayType(), "支付类型不能为空");
        AssertUtil.notNull(platformOrder.getItemNum(), "买家购买的商品总数不能为空");
    }

    /**
     * &
     * 店铺订单校验
     *
     * @param shopOrder
     */
    private void shopOrderParamCheck(ShopOrder shopOrder) {
        AssertUtil.notBlank(shopOrder.getChannelCode(), "渠道编码不能为空");
        AssertUtil.notBlank(shopOrder.getPlatformCode(), "来源平台编码不能为空");
        AssertUtil.notBlank(shopOrder.getPlatformOrderCode(), "平台订单编码不能为空");
        AssertUtil.notBlank(shopOrder.getShopOrderCode(), "店铺订单编码不能为空");
        AssertUtil.notBlank(shopOrder.getPlatformType(), "订单来源类型不能为空");
        AssertUtil.notNull(shopOrder.getShopId(), "订单所属的店铺id不能为空");
        AssertUtil.notBlank(shopOrder.getShopName(), "店铺名称不能为空");
        AssertUtil.notBlank(shopOrder.getUserId(), "会员id不能为空");
        AssertUtil.notBlank(shopOrder.getStatus(), "订单状态不能为空");
        AssertUtil.notNull(shopOrder.getPayment(), "订单实付总金额不能为空");
        AssertUtil.isTrue(shopOrder.getPayment() >= 0, "订单实付总金额应大于等于0");
        AssertUtil.notNull(shopOrder.getItemNum(), "店铺订单商品总数不能为空");
    }

    /**
     * 商品参数校验
     *
     * @param orderItem
     */
    private void orderItemsParamCheck(OrderItem orderItem) {
        AssertUtil.notBlank(orderItem.getChannelCode(), "渠道编码不能为空");
        AssertUtil.notBlank(orderItem.getPlatformCode(), "来源平台编码不能为空");
        AssertUtil.notBlank(orderItem.getPlatformOrderCode(), "平台订单编码不能为空");
        AssertUtil.notBlank(orderItem.getShopOrderCode(), "店铺订单编码不能为空");
        AssertUtil.notNull(orderItem.getShopId(), "订单所属的店铺id不能为空");
        AssertUtil.notBlank(orderItem.getShopName(), "店铺名称不能为空");
        AssertUtil.notBlank(orderItem.getUserId(), "会员id不能为空");
        AssertUtil.notBlank(orderItem.getItemNo(), "商品货号不能为空");
        //AssertUtil.notBlank(orderItem.getBarCode(), "条形码不能为空");
        AssertUtil.notBlank(orderItem.getItemName(), "商品名称不能为空");
        AssertUtil.notNull(orderItem.getPayment(), "实付金额不能为空");
        AssertUtil.isTrue(orderItem.getPayment() >= 0, "实付金额应大于等于0");
        AssertUtil.notNull(orderItem.getPrice(), "单价不能为空");
        AssertUtil.notNull(orderItem.getNum(), "购买数量不能为空");
    }


    /**
     * 拆分店铺级订单
     * @param shopOrder
     * @return
     */
    public List<WarehouseOrder> dealShopOrder(ShopOrder shopOrder) {
        List<OrderItem> orderItemList = shopOrder.getOrderItems();
        //分离一件代发和自采商品
        List<OrderItem> orderItemList1 = new ArrayList<>();//自采商品
        List<OrderItem> orderItemList2 = new ArrayList<>();//一件代发
        for (OrderItem orderItem : orderItemList) {
            if (orderItem.getSkuCode().startsWith(SP0)) {
                orderItemList1.add(orderItem);
            }
            if (orderItem.getSkuCode().startsWith(SP1)) {
                orderItemList2.add(orderItem);
            }
        }
        List<WarehouseOrder> warehouseOrderList = new ArrayList<WarehouseOrder>();
        if(orderItemList1.size() > 0){
            // TODO 自采的拆单暂时不做
        }
        if(orderItemList2.size() > 0){
            warehouseOrderList = dealSupplier(orderItemList2, shopOrder);
        }
        return warehouseOrderList;
    }



    public List<WarehouseOrder> dealSupplier(List<OrderItem> orderItems, ShopOrder shopOrder) {
        //新建仓库级订单
        List<String> skuCodeList = new ArrayList<>();
        for (OrderItem orderItem : orderItems) {
            skuCodeList.add(orderItem.getSkuCode());
        }
        Example example = new Example(ExternalItemSku.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("skuCode", skuCodeList);
        List<ExternalItemSku> externalItemSkuList = externalItemSkuService.selectByExample(example);
        AssertUtil.notEmpty(externalItemSkuList, String.format("根据sku编码列表[%s]查询一件代发商品为空", CommonUtil.converCollectionToString(skuCodeList)));
        Set<String> supplierCodes = new HashSet<>();
        for (ExternalItemSku externalItemSku : externalItemSkuList) {
            supplierCodes.add(externalItemSku.getSupplierCode());
        }
        List<WarehouseOrder> warehouseOrderList = new ArrayList<WarehouseOrder>();
        for (String supplierCode : supplierCodes) {
            List<OrderItem> orderItemList2 = new ArrayList<OrderItem>();
            WarehouseOrder warehouseOrder = new WarehouseOrder();
            warehouseOrder.setSupplierCode(supplierCode);
            warehouseOrder.setShopId(shopOrder.getShopId());
            warehouseOrder.setShopOrderCode(shopOrder.getShopOrderCode());
            warehouseOrder.setShopName(shopOrder.getShopName());
            warehouseOrder.setSupplierCode(supplierCode);
            warehouseOrder.setPlatformCode(shopOrder.getPlatformCode());
            warehouseOrder.setChannelCode(shopOrder.getChannelCode());
            warehouseOrder.setPlatformOrderCode(shopOrder.getPlatformOrderCode());
            warehouseOrder.setPlatformType(shopOrder.getPlatformType());
            warehouseOrder.setUserId(shopOrder.getUserId());
            warehouseOrder.setStatus(ZeroToNineEnum.ONE.getCode());
            //流水号
            String code = serialUtilService.generateRandomCode(Integer.parseInt(ZeroToNineEnum.SEVEN.getCode()), SupplyConstants.Serial.WAREHOUSE_ORDER, supplierCode, ZeroToNineEnum.ONE.getCode(), DateUtils.dateToCompactString(Calendar.getInstance().getTime()));
            warehouseOrder.setWarehouseOrderCode(code);
            warehouseOrder.setOrderType(ZeroToNineEnum.ONE.getCode());
            Boolean flag = false;
            for (ExternalItemSku externalItemSku : externalItemSkuList) {
                if(StringUtils.equals(supplierCode, externalItemSku.getSupplierCode())){
                    OrderItem orderItem = getWarehouseOrderItems(warehouseOrder,externalItemSku, orderItems);
                    if(!flag){
                        warehouseOrder.setSupplierName(orderItem.getSupplierName());
                        flag = true;
                    }
                    orderItemList2.add(orderItem);
                }
            }
            setWarehouseOrderFee(warehouseOrder, orderItemList2);
            warehouseOrder.setOrderItemList(orderItemList2);
            warehouseOrderList.add(warehouseOrder);
        }
        return warehouseOrderList;
    }

    /**
     * 设置仓库订单金额
     * @param warehouseOrder
     * @param orderItemList
     */
    private void setWarehouseOrderFee(WarehouseOrder warehouseOrder, List<OrderItem> orderItemList){
        Integer itemsNum = 0;//商品总数量
        Long totalFee = 0L;//总金额
        Long payment = 0L;//实付金额
        Long adjustFee = 0L;//卖家手工调整金额
        Long postageFee = 0L;//邮费分摊金额
        Long discountPromotion = 0L;//促销优惠总金额
        Long discountCouponShop = 0L;//店铺优惠卷分摊总金额
        Long discountCouponPlatform = 0L;//平台优惠卷分摊总金额
        Long discountFee = 0L;//促销优惠金额
        for(OrderItem orderItem: orderItemList){
            itemsNum += 1;
            totalFee += orderItem.getTotalFee();
            payment += orderItem.getPayment();
            adjustFee += orderItem.getAdjustFee();
            postageFee += orderItem.getPostDiscount();
            discountPromotion += orderItem.getDiscountPromotion();
            discountCouponShop += orderItem.getDiscountCouponShop();
            discountCouponPlatform += orderItem.getDiscountCouponPlatform();
            discountFee += orderItem.getDiscountFee();
        }
        warehouseOrder.setItemsNum(itemsNum);
        warehouseOrder.setTotalFee(totalFee);
        warehouseOrder.setPayment(payment);
        warehouseOrder.setAdjustFee(adjustFee);
        warehouseOrder.setPostageFee(postageFee);
        warehouseOrder.setDiscountPromotion(discountPromotion);
        warehouseOrder.setDiscountCouponShop(discountCouponShop);
        warehouseOrder.setDiscountCouponPlatform(discountCouponPlatform);
        warehouseOrder.setDiscountFee(discountFee);
    }


    /**
     * 获取仓库商品明细
     * @param warehouseOrder
     * @param externalItemSku
     * @param orderItemList
     * @return
     */
    private OrderItem getWarehouseOrderItems(WarehouseOrder warehouseOrder, ExternalItemSku externalItemSku, List<OrderItem> orderItemList){
        for(OrderItem orderItem: orderItemList){
            if(StringUtils.equals(externalItemSku.getSkuCode(),orderItem.getSkuCode())){
                orderItem.setPlatformOrderCode(warehouseOrder.getPlatformOrderCode());
                orderItem.setShopOrderCode(warehouseOrder.getShopOrderCode());
                orderItem.setWarehouseOrderCode(warehouseOrder.getWarehouseOrderCode());
                orderItem.setSupplierSkuCode(externalItemSku.getSupplierSkuCode());
                orderItem.setSupplierName(externalItemSku.getSupplierName());
                return orderItem;
            }
        }
        return null;
    }

    @Override
    public JSONObject sendOrderInformation() {

        return null;
    }

}

