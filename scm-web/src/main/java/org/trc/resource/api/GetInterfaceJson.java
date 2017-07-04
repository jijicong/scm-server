package org.trc.resource.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.trc.biz.impl.trc.model.Order;
import org.trc.biz.impl.trc.model.TrcShopOrder;
import org.trc.domain.order.OrderItem;
import org.trc.domain.order.PlatformOrder;
import org.trc.domain.order.ShopOrder;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by ding on 2017/6/26.
 */
public class GetInterfaceJson {

    public static long getLong(double fee){
        return (long) (fee*100);
    }

    public static Byte getByte(Boolean boo){
        if (boo)
            return new Byte("1");
        else
            return new Byte("0");
    }

    public static Date getDate(Long str){
        return new Date(str);
    }

    public static void main(String[] args) {
        /*String channelCode = "TRC";
        PlatformOrder platformOrder = new PlatformOrder("1", "1", "1", "1", "1",
                1, "1", 12L, 12L, 12L, 12L, 12L, 12L,
                (byte) 1, "1", "1", "1", "1", "1", "1",
                "1", "1", "1", "1", "1", "1",
                "1", "1", "1", "1", "1", (byte) 1, 1, 1,
                "1", 12L, (byte) 1, "1", "1", "1", (byte) 1, "1", (byte) 1,
                1L, 1L, 1L, 1L, "1", "1",
                (byte) 1, "1", "1", "1", Calendar.getInstance().getTime(), Calendar.getInstance().getTime(),
                Calendar.getInstance().getTime(), Calendar.getInstance().getTime(), Calendar.getInstance().getTime(), Calendar.getInstance().getTime(),
                Calendar.getInstance().getTime(), "1");
        TrcShopOrder t = new TrcShopOrder();
        ShopOrder shop1 = new ShopOrder(t.getOrderId(), "1", "1",
                "1", "1", 1L, "1", "1", "1", "1",
                "1", 1L, 1L, 1L, 1L, 1L,
                1L, 1L, "1", "1", 1L, 1, 1L,
                false, false, "1", 1L, new Date(), new Date(), new Date(), "1", "1");



        ShopOrder shop2 = new ShopOrder("2", "1", "1",
                "1", "1", 1L, "1", "1", "1", "1",
                "1", 1L, 1L, 1L, 1L, 1L,
                1L, 1L, "1", "1", 1L, 1, 1L,
                false, false, "1", 1L, new Date(), new Date(), new Date(), "1", "1");

        OrderItem orderItem = new OrderItem("1", "1", "1",
                "1", "1", 1L, "1", "1", 1L, "1",
                "1", "1", "SP0_sku1", "1", 1L, "1", "1", "1",
                1L, 1L, 1L, 1L, 1L, 1, 1, "1",
                "1", false, "1", "1", "1", "1", 1L,
                1L, 1L, 1L, 1L, 1L, 1L, 1L,
                1L, "1", "1", "1", 1L, 1, "1", "1", "1",
                true, 1, "1", 1L, "1", "1", "1", 0.13, "1",
                new Date(), new Date(), new Date(), new Date(), new Date(), new Date(), "1");
        OrderItem orderItem2 = new OrderItem("1", "1", "1",
                "1", "1", 1L, "1", "1", 1L, "1",
                "1", "1", "SP1_sku1", "1", 1L, "1", "1", "1",
                1L, 1L, 1L, 1L, 1L, 1, 1, "1",
                "1", false, "1", "1", "1", "1", 1L,
                1L, 1L, 1L, 1L, 1L, 1L, 1L,
                1L, "1", "1", "1", 1L, 1, "1", "1", "1",
                true, 1, "1", 1L, "1", "1", "1", 0.13, "1",
                new Date(), new Date(), new Date(), new Date(), new Date(), new Date(), "1");
        OrderItem orderItem3 = new OrderItem("1", "1", "1",
                "1", "1", 1L, "1", "1", 1L, "1",
                "1", "1", "SP1_sku2", "1", 1L, "1", "1", "1",
                1L, 1L, 1L, 1L, 1L, 1, 1, "1",
                "1", false, "1", "1", "1", "1", 1L,
                1L, 1L, 1L, 1L, 1L, 1L, 1L,
                1L, "1", "1", "1", 1L, 1, "1", "1", "1",
                true, 1, "1", 1L, "1", "1", "1", 0.13, "1",
                new Date(), new Date(), new Date(), new Date(), new Date(), new Date(), "1");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("platformOrder", platformOrder);
        JSONArray shopOrders = new JSONArray();
        JSONArray orderItems1 = new JSONArray();
        JSONArray orderItems2 = new JSONArray();
        orderItems1.add(orderItem);
        orderItems1.add(orderItem2);
        JSONObject shopOrder1 = new JSONObject();
        shopOrder1.put("shopOrder", shop1);
        shopOrder1.put("orderItems", orderItems1);
        JSONObject shopOrder2 = new JSONObject();
        shopOrder2.put("shopOrder", shop2);
        orderItems2.add(orderItem3);
        shopOrder2.put("orderItems", orderItems2);
        shopOrders.add(shopOrder1);
        shopOrders.add(shopOrder2);
        jsonObject.put("shopOrders", shopOrders);
        System.out.println(jsonObject.toJSONString());*/
    }
}
