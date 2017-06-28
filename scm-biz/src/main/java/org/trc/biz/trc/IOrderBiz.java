package org.trc.biz.trc;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.trc.domain.order.OrderItem;
import org.trc.domain.order.PlatformOrder;
import org.trc.domain.order.ShopOrder;

import java.util.List;

/**
 * 订单处理模块
 * Created by ding on 2017/6/23.
 */
public interface IOrderBiz {


    void splitOrder(JSONArray shopOrders, PlatformOrder platformOrder)throws Exception;


    JSONObject sendOrderInformation();
}
