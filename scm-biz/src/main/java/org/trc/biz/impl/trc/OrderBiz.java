package org.trc.biz.impl.trc;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.txframework.util.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.trc.biz.trc.IOrderBiz;
import org.trc.domain.goods.ExternalItemSku;
import org.trc.domain.order.*;
import org.trc.enums.ExceptionEnum;
import org.trc.enums.ZeroToNineEnum;
import org.trc.exception.TrcException;
import org.trc.service.goods.IExternalItemSkuService;
import org.trc.service.goods.ISkuRelationService;
import org.trc.service.order.*;
import org.trc.util.GuidUtil;

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
    private IWarehouseOrderService warehouseOrderService;

    @Resource
    private IOrderFlowService orderFlowService;


    @Override
    @Transactional
    public void splitOrder(JSONArray shopOrders, PlatformOrder platformOrder) throws Exception {
        try {
            Assert.notNull(platformOrder.getChannelCode(), "渠道编码不能为空");
            Assert.notNull(platformOrder.getPlatformCode(), "来源平台编码不能为空");
            Assert.notNull(platformOrder.getPlatformOrderCode(), "平台订单编码不能为空");
            Assert.notNull(platformOrder.getUserId(), "会员id不能为空");
            Assert.notNull(platformOrder.getUserName(), "会员名称不能为空");
            Assert.notNull(platformOrder.getAdjustFee(), "卖家手工调整金额不能为空");
            Assert.notNull(platformOrder.getTotalFee(), "订单总金额不能为空");
            Assert.notNull(platformOrder.getPostageFee(), "邮费不能为空");
            Assert.notNull(platformOrder.getTotalTax(), "总税费不能为空");
            Assert.notNull(platformOrder.getPayment(), "实付金额不能为空");
            Assert.notNull(platformOrder.getPayType(), "支付类型不能为空");
            Assert.notNull(platformOrder.getItemNum(), "买家购买的商品总数不能为空");
            for (int i = 0; i < shopOrders.size(); i++) {
                JSONObject shopOrderJson = shopOrders.getJSONObject(i);
                ShopOrder shopOrder = JSONObject.parseObject(shopOrderJson.toJSONString(), ShopOrder.class);
                Assert.notNull(shopOrder.getChannelCode(), "渠道编码不能为空");
                Assert.notNull(shopOrder.getPlatformCode(), "来源平台编码不能为空");
                Assert.notNull(shopOrder.getPlatformOrderCode(), "平台订单编码不能为空");
                Assert.notNull(shopOrder.getShopOrderCode(), "店铺订单编码不能为空");
                Assert.notNull(shopOrder.getPlatformType(), "订单来源类型不能为空");
                Assert.notNull(shopOrder.getShopId(), "订单所属的店铺id不能为空");
                Assert.notNull(shopOrder.getShopName(), "店铺名称不能为空");
                Assert.notNull(shopOrder.getUserId(), "会员id不能为空");
                Assert.notNull(shopOrder.getStatus(), "订单状态不能为空");
                JSONArray orderItems = shopOrderJson.getJSONArray("orderItems");
                for (int j = 0; j < orderItems.size(); i++) {
                    JSONObject orderItemJson = orderItems.getJSONObject(i);
                    OrderItem orderItem = JSONObject.parseObject(orderItemJson.toJSONString(), OrderItem.class);
                    Assert.notNull(orderItem.getChannelCode(), "渠道编码不能为空");
                    Assert.notNull(orderItem.getPlatformCode(), "来源平台编码不能为空");
                    Assert.notNull(orderItem.getPlatformOrderCode(), "平台订单编码不能为空");
                    Assert.notNull(orderItem.getShopOrderCode(), "店铺订单编码不能为空");
                    Assert.notNull(orderItem.getShopId(), "订单所属的店铺id不能为空");
                    Assert.notNull(orderItem.getShopName(), "店铺名称不能为空");
                    Assert.notNull(orderItem.getUserId(), "会员id不能为空");
                    Assert.notNull(orderItem.getItemNo(), "商品货号不能为空");
                    Assert.notNull(orderItem.getBarCode(), "条形码不能为空");
                    Assert.notNull(orderItem.getItemName(), "商品名称不能为空");
                }

            }
        } catch (Exception e) {
            logger.error("参数校验报错: " + e.getMessage());
            throw new TrcException(ExceptionEnum.TRC_ORDER_PUSH_EXCEPTION, "参数校验报错: " + e.getMessage());
        }
        try {
            //插入流水
            for (int i = 0; i < shopOrders.size(); i++) {
                OrderFlow orderFlow = new OrderFlow();
                orderFlow.setPlatformOrderCode(platformOrder.getPlatformOrderCode());
                orderFlow.setShopOrderCode(shopOrders.getJSONObject(i).getString("shopOrderCode"));
                orderFlow.setType("DEAL");
                orderFlowService.insert(orderFlow);
            }
        } catch (Exception e) {
            logger.error("重复提交订单: " + e.getMessage());
            throw new TrcException(ExceptionEnum.TRC_ORDER_PUSH_EXCEPTION, "重复提交订单：" + e.getMessage());
        }
        //向数据库中批量插入platformOrder
        platformOrderService.insert(platformOrder);
        //业务代码，以店铺为单位拆分
        for (int i = 0; i < shopOrders.size(); i++) {
            dealShopOrder(shopOrders.getJSONObject(i), platformOrder);
        }
    }

    @Override
    public JSONObject sendOrderInformation() {
        
        return null;
    }

    public void dealShopOrder(JSONObject shopOrderJson, PlatformOrder platformOrder) throws Exception {
        JSONArray orderItems = shopOrderJson.getJSONArray("orderItems");
        List<OrderItem> orderItemList = orderItems.toJavaList(OrderItem.class);
        //分离一件代发和自采商品
        List<OrderItem> orderItemList1 = new ArrayList<>();//TODO 自采商品,二期处理
        List<OrderItem> orderItemList2 = new ArrayList<>();//一件代发
        for (OrderItem orderItem : orderItemList) {
            if (orderItem.getSkuCode().startsWith("SP0")) {
                orderItemList1.add(orderItem);
            } else {
                orderItemList2.add(orderItem);
            }
        }
        //匹配供应商，新建仓库级订单，修改orderItem，直接发送订单信息
        ShopOrder shopOrder = JSONObject.parseObject(shopOrderJson.toJSONString(), ShopOrder.class);
        dealSupplier(orderItemList2, shopOrder, platformOrder);
        shopOrderService.insert(shopOrder);
    }

    //一件代发
    public void dealSupplier(List<OrderItem> orderItems, ShopOrder shopOrder, PlatformOrder platformOrder) throws Exception {
        //新建仓库级订单
        //TODO 待测试
        List<String> supplierCodes = skuRelationService.selectSupplierCode(orderItems);

        for (int i = 0; i < supplierCodes.size(); i++) {


            List<OrderItem> orderItemsToSupplier = new ArrayList<>();
            WarehouseOrder warehouseOrder = new WarehouseOrder();
            warehouseOrder.setShopId(shopOrder.getShopId());
            warehouseOrder.setShopOrderCode(shopOrder.getShopOrderCode());
            warehouseOrder.setShopName(shopOrder.getShopName());
            warehouseOrder.setSupplierCode(supplierCodes.get(i));
            warehouseOrder.setPlatformCode(shopOrder.getPlatformCode());
            warehouseOrder.setChannelCode(shopOrder.getChannelCode());
            warehouseOrder.setPlatformOrderCode(shopOrder.getPlatformOrderCode());
            warehouseOrder.setPlatformType(shopOrder.getPlatformType());
            warehouseOrder.setUserId(shopOrder.getUserId());
            warehouseOrder.setStatus(ZeroToNineEnum.ONE.getCode());
            warehouseOrder.setWarehouseOrderCode(GuidUtil.getNextUid(supplierCodes.get(i) + "_"));

            Boolean flag = true;
            //循环orderItem,获取供应商skucode，关联仓库级订单，并修改仓库级订单
            Iterator<OrderItem> iterator = orderItems.iterator();
            while (iterator.hasNext()) {
                OrderItem orderItem = iterator.next();
                ExternalItemSku externalItemSku = new ExternalItemSku();
                externalItemSku.setSkuCode(orderItem.getSkuCode());
                externalItemSku = externalItemSkuService.selectOne(externalItemSku);
                if (externalItemSku.getSupplierCode().equals(warehouseOrder.getSupplierCode())) {
                    if (flag) {
                        warehouseOrder.setSupplierName(externalItemSku.getSupplierName());
                        warehouseOrder.setPayment(orderItem.getPayment());
                        flag = false;
                    } else {
                        warehouseOrder.setPayment(warehouseOrder.getPayment() + orderItem.getPayment());
                    }
                    orderItem.setWarehouseOrderCode(warehouseOrder.getWarehouseOrderCode());
                    orderItem.setSupplierSkuCode(externalItemSku.getSupplierSkuCode());
//                    orderItem.setCategoryId(externalItemSku.getCategory());
                    // 向数据库中插入OrderItem
                    orderItemService.insert(orderItem);
                    orderItemsToSupplier.add(orderItem);
                    iterator.remove();
                }
            }
            //向数据中插入仓库级订单
            warehouseOrderService.insert(warehouseOrder);

            Map map = new HashMap();//最后传出去的封装数据
            map.put("platformOrder", platformOrder);
            map.put("orderItems", orderItemsToSupplier);
            map.put("warehouseOrder", warehouseOrder);
            //TODO 根据供应商信息分别调接口

        }
    }
}

