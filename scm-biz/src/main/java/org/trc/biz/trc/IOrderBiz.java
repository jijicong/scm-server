package org.trc.biz.trc;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.trc.domain.order.OrderItem;
import org.trc.domain.order.PlatformOrder;
import org.trc.domain.order.ShopOrder;
import org.trc.util.AppResult;

import java.util.List;

/**
 * 订单处理模块
 * Created by ding on 2017/6/23.
 */
public interface IOrderBiz {

    /**
     * 接收渠道订单信息
     * @param orderInfo
     * @return
     */
    AppResult<String> reciveChannelOrder(String orderInfo);


    /**
     * 查询京东物流信息
     * @param shopOrderCode
     * @return
     */
    AppResult getJDLogistics(String shopOrderCode) throws  Exception;




}
