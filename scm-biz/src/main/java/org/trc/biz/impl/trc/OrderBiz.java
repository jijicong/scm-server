package org.trc.biz.impl.trc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.trc.biz.trc.IOrderBiz;
import org.trc.domain.goods.ExternalItemSku;
import org.trc.domain.order.*;
import org.trc.enums.ExceptionEnum;
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
    public void splitOrder(List<OrderItem> orderItems, List<ShopOrder> shopOrders, PlatformOrder platformOrder) throws Exception {
        //插入流水
        try {
            for (ShopOrder shopOrder : shopOrders) {
                OrderFlow orderFlow = new OrderFlow();
                orderFlow.setPlatformOrderCode(platformOrder.getPlatformOrderCode());
                orderFlow.setShopOrderCode(shopOrder.getShopOrderCode());
                orderFlow.setType("DEAL");
                orderFlowService.insert(orderFlow);
            }
        } catch (Exception e) {
            logger.error("重复提交订单: " + e.getMessage());
            throw new TrcException(ExceptionEnum.TRC_ORDER_PUSH_EXCEPTION, "重复提交订单：" + e.getMessage());
        }

        //分离一件代发和自采商品
        List<OrderItem> orderItems1 = new ArrayList<>();//TODO 自采商品,二期处理
        List<OrderItem> orderItems2 = new ArrayList<>();//一件代发
        for (OrderItem orderItem : orderItems) {
            if (orderItem.getSkuCode().startsWith("SP0")) {
                orderItems1.add(orderItem);
            } else {
                orderItems2.add(orderItem);
            }
        }

        //向数据库中批量插入platformOrder
        platformOrderService.insert(platformOrder);

        //以店铺为单位拆分
        for (ShopOrder shopOrder : shopOrders) {
            //匹配供应商，新建仓库级订单，修改orderItem，直接发送订单信息
            dealSupplier(orderItems2, shopOrder, platformOrder);
            shopOrderService.insert(shopOrder);
        }
    }

    public void dealSupplier(List<OrderItem> orderItems, ShopOrder shopOrder, PlatformOrder platformOrder) throws Exception {
        //新建仓库级订单
        //TODO 待测试
        List<String> supplierNames = skuRelationService.selectSupplierSkuCode(orderItems);

        for (int i = 0; i < supplierNames.size(); i++) {

            Map map = new HashMap();//最后传出去的封装数据
            map.put("platformOrder", platformOrder);
            List<OrderItem> orderItemsToSupplier = new ArrayList<>();
            WarehouseOrder warehouseOrder = new WarehouseOrder();
            warehouseOrder.setShopId(shopOrder.getShopId());
            warehouseOrder.setShopOrderCode(shopOrder.getShopOrderCode());
            warehouseOrder.setShopName(shopOrder.getShopName());
            warehouseOrder.setSupplierName(supplierNames.get(i));
            warehouseOrder.setPlatformCode(shopOrder.getPlatformCode());
            warehouseOrder.setChannelCode(shopOrder.getChannelCode());
            warehouseOrder.setPlatformOrderCode(shopOrder.getPlatformOrderCode());
            warehouseOrder.setPlatformType(shopOrder.getPlatformType());
            warehouseOrder.setUserId(shopOrder.getUserId());
            warehouseOrder.setStatus(new Byte("1"));
            warehouseOrder.setWarehouseOrderCode(GuidUtil.getNextUid(supplierNames.get(i) + "_"));

            Boolean flag = true;
            //循环orderItem,获取供应商skucode，关联仓库级订单，并修改仓库级订单
            Iterator<OrderItem> iterator = orderItems.iterator();
            while (iterator.hasNext()) {
                OrderItem orderItem = iterator.next();
                ExternalItemSku externalItemSku = new ExternalItemSku();
                externalItemSku.setSkuCode(orderItem.getSkuCode());
                externalItemSku = externalItemSkuService.selectOne(externalItemSku);
                if (externalItemSku.getSupplierName().equals(warehouseOrder.getSupplierName())) {
                    if (flag) {
                        warehouseOrder.setSupplierCode(externalItemSku.getSupplierCode());
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

            map.put("orderItems", orderItemsToSupplier);
            map.put("warehouseOrder", warehouseOrder);
            //TODO 根据供应商信息分别调接口

        }
    }
}

