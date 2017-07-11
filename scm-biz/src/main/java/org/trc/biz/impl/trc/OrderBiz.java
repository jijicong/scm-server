package org.trc.biz.impl.trc;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.trc.biz.trc.IOrderBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.goods.ExternalItemSku;
import org.trc.domain.goods.SkuRelation;
import org.trc.domain.order.*;
import org.trc.enums.ExceptionEnum;
import org.trc.enums.ZeroToNineEnum;
import org.trc.exception.OrderException;
import org.trc.exception.TrcException;
import org.trc.service.goods.IExternalItemSkuService;
import org.trc.service.goods.ISkuRelationService;
import org.trc.service.order.*;
import org.trc.service.util.ISerialUtilService;
import org.trc.util.AppResult;
import org.trc.util.AssertUtil;
import org.trc.util.DateUtils;
import org.trc.util.ResultUtil;
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
    private ISkuRelationService skuRelationService;

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

    private final static String SP0 = "SP0";

    private final static String SP1 = "SP1";

    //业务类型：交易
    public final static String BIZ_TYPE_DEAL = "DEAL";




    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void splitOrder(JSONArray shopOrders, PlatformOrder platformOrder) throws Exception {
        try {
            AssertUtil.notBlank(platformOrder.getChannelCode(), "渠道编码不能为空");
            AssertUtil.notBlank(platformOrder.getPlatformCode(), "来源平台编码不能为空");
            AssertUtil.notBlank(platformOrder.getPlatformOrderCode(), "平台订单编码不能为空");
            AssertUtil.notBlank(platformOrder.getUserId(), "会员id不能为空");
            AssertUtil.notBlank(platformOrder.getUserName(), "会员名称不能为空");
            AssertUtil.notNull(platformOrder.getAdjustFee(), "卖家手工调整金额不能为空");
            AssertUtil.isTrue(platformOrder.getAdjustFee()>=0,"卖家手工调整金额应大于等于0");
            AssertUtil.notNull(platformOrder.getTotalFee(), "订单总金额不能为空");
            AssertUtil.isTrue(platformOrder.getTotalFee()>=0,"订单总金额应大于等于0");
            AssertUtil.notNull(platformOrder.getPostageFee(), "邮费不能为空");
            AssertUtil.isTrue(platformOrder.getPostageFee()>=0,"邮费应大于等于0");
            AssertUtil.notNull(platformOrder.getTotalTax(), "总税费不能为空");
            AssertUtil.isTrue(platformOrder.getTotalTax()>=0,"总税费应大于等于0");
            AssertUtil.notNull(platformOrder.getPayment(), "实付金额不能为空");
            AssertUtil.isTrue(platformOrder.getPayment()>=0,"实付金额应大于等于0");
            AssertUtil.notBlank(platformOrder.getPayType(), "支付类型不能为空");
            AssertUtil.notNull(platformOrder.getItemNum(), "买家购买的商品总数不能为空");
            Integer totalNum = 0;
            Long totalShop = 0L;
            for (Object shopOrderJson : shopOrders) {
                ShopOrder shopOrder = JSONObject.parseObject(((JSONObject) shopOrderJson).getJSONObject("shopOrder").toJSONString(), ShopOrder.class);
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
                AssertUtil.isTrue(shopOrder.getPayment()>=0,"订单实付总金额应大于等于0");
                AssertUtil.notNull(shopOrder.getItemNum(), "店铺订单商品总数不能为空");
                totalShop += shopOrder.getPayment();
                totalNum += shopOrder.getItemNum();
                JSONArray orderItems = ((JSONObject) shopOrderJson).getJSONArray("orderItems");
                Integer totalOneShopNum = 0;
                Long totalItem = 0L;
                for (Object orderItemJson : orderItems) {
                    OrderItem orderItem = JSONObject.parseObject(((JSONObject) orderItemJson).toJSONString(), OrderItem.class);
                    AssertUtil.notBlank(orderItem.getChannelCode(), "渠道编码不能为空");
                    AssertUtil.notBlank(orderItem.getPlatformCode(), "来源平台编码不能为空");
                    AssertUtil.notBlank(orderItem.getPlatformOrderCode(), "平台订单编码不能为空");
                    AssertUtil.notBlank(orderItem.getShopOrderCode(), "店铺订单编码不能为空");
                    AssertUtil.notNull(orderItem.getShopId(), "订单所属的店铺id不能为空");
                    AssertUtil.notBlank(orderItem.getShopName(), "店铺名称不能为空");
                    AssertUtil.notBlank(orderItem.getUserId(), "会员id不能为空");
                    AssertUtil.notBlank(orderItem.getItemNo(), "商品货号不能为空");
                    AssertUtil.notBlank(orderItem.getBarCode(), "条形码不能为空");
                    AssertUtil.notBlank(orderItem.getItemName(), "商品名称不能为空");
                    AssertUtil.notNull(orderItem.getPayment(), "实付金额不能为空");
                    AssertUtil.isTrue(orderItem.getPayment()>=0, "实付金额应大于等于0");
                    AssertUtil.notNull(orderItem.getPrice(), "单价不能为空");
                    AssertUtil.notNull(orderItem.getNum(), "购买数量不能为空");
                    totalItem += orderItem.getPayment();
                    totalOneShopNum += orderItem.getNum();
                }
                AssertUtil.isTrue(totalItem == shopOrder.getPayment(), "店铺订单实付金额与所有该店铺商品总实付金额不等值");
                AssertUtil.isTrue(totalOneShopNum == shopOrder.getItemNum(), "店铺订单商品总数与所有该店铺商品总数不等值");
            }
            AssertUtil.isTrue(totalShop == platformOrder.getPayment(), "平台订单实付金额与所有店铺总实付金额不等值");
            AssertUtil.isTrue(totalNum == platformOrder.getItemNum(), "平台订单商品总数与所有店铺商品总数不等值");
        } catch (Exception e) {
            logger.error("参数校验报错: " + e.getMessage());
            throw new TrcException(ExceptionEnum.TRC_ORDER_PUSH_EXCEPTION, "参数校验报错: " + e.getMessage());
        }
        try {
            //插入流水
            for (Object shopOrderJson : shopOrders) {
                OrderFlow orderFlow = new OrderFlow();
                orderFlow.setPlatformOrderCode(platformOrder.getPlatformOrderCode());
                orderFlow.setShopOrderCode(((JSONObject) shopOrderJson).getJSONObject("shopOrder").getString("shopOrderCode"));
                //TODO 建一个常量类
                orderFlow.setType("DEAL");
                orderFlowService.insert(orderFlow);
            }
        } catch (DuplicateKeyException e) {
            logger.error("重复提交订单: " + e.getMessage());
            throw new TrcException(ExceptionEnum.TRC_ORDER_PUSH_EXCEPTION, "重复提交订单：" + e.getMessage());
        }
        //向数据库中批量插入platformOrder
        platformOrderService.insert(platformOrder);
        //业务代码，以店铺为单位拆分
        for (Object shopOrderJson : shopOrders) {
            //dealShopOrder((JSONObject) shopOrderJson, platformOrder);
        }
    }


    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public AppResult<String> reciveChannelOrder(String orderInfo) {
        //数据转换
        JSONObject orderObj = null;
        try {
            orderObj = JSONObject.parseObject(orderInfo);
        }catch (ClassCastException e){
            String msg = String.format("渠道同步订单参数不是JSON格式");
            logger.error(msg, e);
            return ResultUtil.createFailAppResult(msg);
        }
        //获取平台订单信息
        PlatformOrder platformOrder = JSONObject.parseObject(orderObj.getJSONObject("platformOrder").toJSONString(), PlatformOrder.class);
        platformOrderParamCheck(platformOrder);
        JSONArray shopOrderArray = orderObj.getJSONArray("shopOrders");
        //获取店铺订单
        List<ShopOrder> shopOrderList = getShopOrderList(platformOrder, shopOrderArray);
        //保存幂等流水
        saveIdempotentFlow(shopOrderList);
        //拆分仓库订单
        List<WarehouseOrder> warehouseOrderList = new ArrayList<WarehouseOrder>();
        for (ShopOrder shopOrder : shopOrderList) {
            warehouseOrderList.addAll(dealShopOrder(shopOrder, platformOrder));
        }
        //订单商品明细
        List<OrderItem> orderItemList = new ArrayList<OrderItem>();
        for(WarehouseOrder warehouseOrder: warehouseOrderList){
            orderItemList.addAll(warehouseOrder.getOrderItemList());
        }
        orderItemService.insertList(orderItemList);
        //保存仓库订单
        warehouseOrderService.insertList(warehouseOrderList);
        //保存商铺订单
        shopOrderService.insertList(shopOrderList);
        //保存平台订单
        platformOrderService.insert(platformOrder);

        //最后传出去的封装数据
        /*Map map = new HashMap();
        map.put("platformOrder", platformOrder);
        map.put("orderItems", orderItemsToSupplier);
        map.put("warehouseOrder", warehouseOrder);
        //TODO 根据供应商信息分别调接口*/

        return ResultUtil.createSucssAppResult("接收订单成功", "");
    }

    /**
     * 保存请求流水
     * @param orderInfo
     */
    private void saveRequestFlow(String orderInfo){

    }

    /**
     * 保存幂等流水
     * @param shopOrderList
     */
    private void saveIdempotentFlow(List<ShopOrder> shopOrderList){
        try {
            for (ShopOrder shopOrder : shopOrderList) {
                OrderFlow orderFlow = new OrderFlow();
                orderFlow.setPlatformOrderCode(shopOrder.getPlatformOrderCode());
                orderFlow.setShopOrderCode(shopOrder.getShopOrderCode());
                orderFlow.setType(BIZ_TYPE_DEAL);
                int count = orderFlowService.insert(orderFlow);
                if(count == 0){
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
     * @param platformOrder
     * @param shopOrderArray
     * @return
     */
    private List<ShopOrder> getShopOrderList(PlatformOrder platformOrder, JSONArray shopOrderArray){
        List<ShopOrder> shopOrderList = new ArrayList<ShopOrder>();
        Integer totalNum = 0;
        Long totalShop = 0L;
        for(Object obj : shopOrderArray){
            ShopOrder shopOrder = ((JSONObject)obj).getJSONObject("shopOrder").toJavaObject(ShopOrder.class);
            shopOrderParamCheck(shopOrder);
            shopOrderList.add(shopOrder);
            totalShop += shopOrder.getPayment();
            totalNum += shopOrder.getItemNum();
            List<OrderItem> orderItemList = ((JSONObject)obj).getJSONArray("orderItems").toJavaList(OrderItem.class);
            shopOrder.setOrderItems(orderItemList);
            Integer totalOneShopNum = 0;
            Long totalItem = 0L;
            for(OrderItem orderItem: orderItemList){
                orderItemsParamCheck(orderItem);
                totalItem += orderItem.getPayment();
                totalOneShopNum += orderItem.getNum();
            }
            AssertUtil.isTrue(totalItem == shopOrder.getPayment(), "店铺订单实付金额与所有该店铺商品总实付金额不等值");
            AssertUtil.isTrue(totalOneShopNum == shopOrder.getItemNum(), "店铺订单商品总数与所有该店铺商品总数不等值");
            orderItemList.addAll(orderItemList);
        }
        AssertUtil.isTrue(totalShop == platformOrder.getPayment(), "平台订单实付金额与所有店铺总实付金额不等值");
        AssertUtil.isTrue(totalNum == platformOrder.getItemNum(), "平台订单商品总数与所有店铺商品总数不等值");
        return shopOrderList;
    }

    /**
     * 平台订单校验
     * @param platformOrder
     */
    private void platformOrderParamCheck(PlatformOrder platformOrder){
        AssertUtil.notBlank(platformOrder.getChannelCode(), "渠道编码不能为空");
        AssertUtil.notBlank(platformOrder.getPlatformCode(), "来源平台编码不能为空");
        AssertUtil.notBlank(platformOrder.getPlatformOrderCode(), "平台订单编码不能为空");
        AssertUtil.notBlank(platformOrder.getUserId(), "会员id不能为空");
        AssertUtil.notBlank(platformOrder.getUserName(), "会员名称不能为空");
        AssertUtil.notNull(platformOrder.getAdjustFee(), "卖家手工调整金额不能为空");
        AssertUtil.isTrue(platformOrder.getAdjustFee()>=0,"卖家手工调整金额应大于等于0");
        AssertUtil.notNull(platformOrder.getTotalFee(), "订单总金额不能为空");
        AssertUtil.isTrue(platformOrder.getTotalFee()>=0,"订单总金额应大于等于0");
        AssertUtil.notNull(platformOrder.getPostageFee(), "邮费不能为空");
        AssertUtil.isTrue(platformOrder.getPostageFee()>=0,"邮费应大于等于0");
        AssertUtil.notNull(platformOrder.getTotalTax(), "总税费不能为空");
        AssertUtil.isTrue(platformOrder.getTotalTax()>=0,"总税费应大于等于0");
        AssertUtil.notNull(platformOrder.getPayment(), "实付金额不能为空");
        AssertUtil.isTrue(platformOrder.getPayment()>=0,"实付金额应大于等于0");
        AssertUtil.notBlank(platformOrder.getPayType(), "支付类型不能为空");
        AssertUtil.notNull(platformOrder.getItemNum(), "买家购买的商品总数不能为空");
    }

    /**&
     * 店铺订单校验
     * @param shopOrder
     */
    private void shopOrderParamCheck(ShopOrder shopOrder){
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
        AssertUtil.isTrue(shopOrder.getPayment()>=0,"订单实付总金额应大于等于0");
        AssertUtil.notNull(shopOrder.getItemNum(), "店铺订单商品总数不能为空");
    }

    /**
     * 商品参数校验
     * @param orderItem
     */
    private void orderItemsParamCheck(OrderItem orderItem){
        AssertUtil.notBlank(orderItem.getChannelCode(), "渠道编码不能为空");
        AssertUtil.notBlank(orderItem.getPlatformCode(), "来源平台编码不能为空");
        AssertUtil.notBlank(orderItem.getPlatformOrderCode(), "平台订单编码不能为空");
        AssertUtil.notBlank(orderItem.getShopOrderCode(), "店铺订单编码不能为空");
        AssertUtil.notNull(orderItem.getShopId(), "订单所属的店铺id不能为空");
        AssertUtil.notBlank(orderItem.getShopName(), "店铺名称不能为空");
        AssertUtil.notBlank(orderItem.getUserId(), "会员id不能为空");
        AssertUtil.notBlank(orderItem.getItemNo(), "商品货号不能为空");
        AssertUtil.notBlank(orderItem.getBarCode(), "条形码不能为空");
        AssertUtil.notBlank(orderItem.getItemName(), "商品名称不能为空");
        AssertUtil.notNull(orderItem.getPayment(), "实付金额不能为空");
        AssertUtil.isTrue(orderItem.getPayment()>=0, "实付金额应大于等于0");
        AssertUtil.notNull(orderItem.getPrice(), "单价不能为空");
        AssertUtil.notNull(orderItem.getNum(), "购买数量不能为空");
    }

    /*public void dealShopOrder(JSONObject shopOrderJson, PlatformOrder platformOrder) throws Exception {
        JSONArray orderItems = shopOrderJson.getJSONArray("orderItems");
        List<OrderItem> orderItemList = orderItems.toJavaList(OrderItem.class);
        //分离一件代发和自采商品
        List<OrderItem> orderItemList1 = new ArrayList<>();//TODO 自采商品,二期处理
        List<OrderItem> orderItemList2 = new ArrayList<>();//一件代发
        for (OrderItem orderItem : orderItemList) {
            if (orderItem.getSkuCode().startsWith(SP0)) {
                orderItemList1.add(orderItem);
            }
            if (orderItem.getSkuCode().startsWith(SP1)) {
                orderItemList2.add(orderItem);
            }
        }
        //匹配供应商，新建仓库级订单，修改orderItem，直接发送订单信息
        ShopOrder shopOrder = JSONObject.parseObject(shopOrderJson.getJSONObject("shopOrder").toJSONString(), ShopOrder.class);
        dealSupplier(orderItemList2, shopOrder, platformOrder);
        shopOrderService.insert(shopOrder);
    }*/

    public List<WarehouseOrder> dealShopOrder(ShopOrder shopOrder, PlatformOrder platformOrder){
        List<OrderItem> orderItemList = shopOrder.getOrderItems();
        //分离一件代发和自采商品
        List<OrderItem> orderItemList1 = new ArrayList<>();//TODO 自采商品,二期处理
        List<OrderItem> orderItemList2 = new ArrayList<>();//一件代发
        for (OrderItem orderItem : orderItemList) {
            if (orderItem.getSkuCode().startsWith(SP0)) {
                orderItemList1.add(orderItem);
            }
            if (orderItem.getSkuCode().startsWith(SP1)) {
                orderItemList2.add(orderItem);
            }
        }
        return dealSupplier(orderItemList2, shopOrder, platformOrder);
    }

    //一件代发
    /*public void dealSupplier(List<OrderItem> orderItems, ShopOrder shopOrder, PlatformOrder platformOrder) {
        //新建仓库级订单
        List<String> skuCodeList = new ArrayList<>();
        for (OrderItem orderItem : orderItems) {
            skuCodeList.add(orderItem.getSkuCode());
        }
        Example example = new Example(SkuRelation.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("skuCode", skuCodeList);
        example.selectProperties("supplierCode");
        List<SkuRelation> skuRelationList = skuRelationService.selectByExample(example);
        Set<String> supplierCodes = new HashSet<>();
        for (SkuRelation skuRelation : skuRelationList) {
            supplierCodes.add(skuRelation.getSupplierCode());
        }
        for (String supplierCode : supplierCodes) {
            List<OrderItem> orderItemsToSupplier = new ArrayList<>();
            WarehouseOrder warehouseOrder = new WarehouseOrder();
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

            Boolean flag = true;
            //循环orderItem,获取供应商skucode，关联仓库级订单，并修改仓库级订单
            Iterator<OrderItem> iterator = orderItems.iterator();
            while (iterator.hasNext()) {
                OrderItem orderItem = iterator.next();
                ExternalItemSku externalItemSku = new ExternalItemSku();
                externalItemSku.setSkuCode(orderItem.getSkuCode());
                externalItemSku = externalItemSkuService.selectOne(externalItemSku);
                if (externalItemSku == null) {
                    throw new TrcException(ExceptionEnum.TRC_ORDER_PUSH_EXCEPTION, "未对应到一件代发商品" + orderItem.getSkuCode());
                }
                if (externalItemSku.getSupplierCode().equals(warehouseOrder.getSupplierCode())) {
                    if (flag) {
                        warehouseOrder.setSupplierName(externalItemSku.getSupplierName());
                        warehouseOrder.setPayment(orderItem.getPayment());
                        warehouseOrder.setItemsNum(1);
                        flag = false;
                    } else {
                        warehouseOrder.setPayment(warehouseOrder.getPayment() + orderItem.getPayment());
                        warehouseOrder.setItemsNum(warehouseOrder.getItemsNum() + 1);
                    }
                    orderItem.setWarehouseOrderCode(warehouseOrder.getWarehouseOrderCode());
                    orderItem.setSupplierSkuCode(externalItemSku.getSupplierSkuCode());
                    // 向数据库中插入OrderItem
                    orderItemService.insert(orderItem);
                    orderItemsToSupplier.add(orderItem);
                    iterator.remove();
                }
            }
            //向数据中插入仓库级订单
            warehouseOrderService.insert(warehouseOrder);
            //最后传出去的封装数据
            Map map = new HashMap();
            map.put("platformOrder", platformOrder);
            map.put("orderItems", orderItemsToSupplier);
            map.put("warehouseOrder", warehouseOrder);
            //TODO 根据供应商信息分别调接口

        }
    }*/

    public List<WarehouseOrder> dealSupplier(List<OrderItem> orderItems, ShopOrder shopOrder, PlatformOrder platformOrder) {
        //新建仓库级订单
        List<String> skuCodeList = new ArrayList<>();
        for (OrderItem orderItem : orderItems) {
            skuCodeList.add(orderItem.getSkuCode());
        }
        Example example = new Example(SkuRelation.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("skuCode", skuCodeList);
        example.selectProperties("supplierCode");
        List<SkuRelation> skuRelationList = skuRelationService.selectByExample(example);
        Set<String> supplierCodes = new HashSet<>();
        for (SkuRelation skuRelation : skuRelationList) {
            supplierCodes.add(skuRelation.getSupplierCode());
        }
        List<WarehouseOrder> warehouseOrderList = new ArrayList<WarehouseOrder>();
        for (String supplierCode : supplierCodes) {
            List<OrderItem> orderItemList2 = new ArrayList<OrderItem>();
            WarehouseOrder warehouseOrder = new WarehouseOrder();
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

            Boolean flag = true;
            //循环orderItem,获取供应商skucode，关联仓库级订单，并修改仓库级订单
            Iterator<OrderItem> iterator = orderItems.iterator();
            while (iterator.hasNext()) {
                OrderItem orderItem = iterator.next();
                ExternalItemSku externalItemSku = new ExternalItemSku();
                externalItemSku.setSkuCode(orderItem.getSkuCode());
                externalItemSku = externalItemSkuService.selectOne(externalItemSku);
                if (externalItemSku == null) {
                    throw new TrcException(ExceptionEnum.TRC_ORDER_PUSH_EXCEPTION, "未对应到一件代发商品" + orderItem.getSkuCode());
                }
                if (externalItemSku.getSupplierCode().equals(warehouseOrder.getSupplierCode())) {
                    if (flag) {
                        warehouseOrder.setSupplierName(externalItemSku.getSupplierName());
                        warehouseOrder.setPayment(orderItem.getPayment());
                        warehouseOrder.setItemsNum(1);
                        flag = false;
                    } else {
                        warehouseOrder.setPayment(warehouseOrder.getPayment() + orderItem.getPayment());
                        warehouseOrder.setItemsNum(warehouseOrder.getItemsNum() + 1);
                    }
                    orderItem.setWarehouseOrderCode(warehouseOrder.getWarehouseOrderCode());
                    orderItem.setSupplierSkuCode(externalItemSku.getSupplierSkuCode());
                    orderItemList2.add(orderItem);
                    iterator.remove();
                }
            }
            warehouseOrder.setOrderItemList(orderItemList2);
            warehouseOrderList.add(warehouseOrder);
        }
        //orderItemService.insertList(orderItemList);
        //warehouseOrderService.insertList(warehouseOrderList);
        return warehouseOrderList;
    }

    @Override
    public JSONObject sendOrderInformation() {

        return null;
    }

    public static void main(String[] args) throws Exception{
        String str = "123";
        try {
            /*JSONArray array = JSONArray.parseArray(str);
            System.out.println(array);*/
            JSONObject array = JSONObject.parseObject(str);
            System.out.println(array);
        }catch (JSONException exception){
            exception.printStackTrace();
        }catch (ClassCastException e){
            e.printStackTrace();
        }

    }

}

