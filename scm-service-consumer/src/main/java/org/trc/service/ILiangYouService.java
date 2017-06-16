package org.trc.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.trc.form.liangyou.LiangYouOrderDO;
import org.trc.form.liangyou.LiangYouTorderDO;
import org.trc.form.liangyou.ResultType;

/**
 * Created by hzwyz on 2017/5/26 0026.
 */
public interface ILiangYouService {

    /**
     *获取Token
     * @param
     * @return 返回Access_Token
     * @throws Exception
     */
    ResultType<JSONObject> getToken() throws Exception;

    /**
     * 获取商品列表
     * @return
     * @throws Exception
     */
    ResultType<JSONObject> exportGoods(String accessToken,String page) throws Exception;

    /**
     * 库存检查接口
     * @return
     * @throws Exception
     */
    ResultType<JSONArray> checkStock(String accessToken, String sku) throws Exception;

    /**
     * 非签约订单导入
     * @return
     * @throws Exception
     */
    ResultType<JSONObject> addOutOrder(LiangYouOrderDO liangYouOrderDO) throws Exception;

    /**
     * 签约订单导入
     * @return
     * @throws Exception
     */
    ResultType<JSONObject> addToutOrder(LiangYouTorderDO orderDO) throws Exception;

    /**
     * 获取订单状态和运单号
     * @return
     * @throws Exception
     */
    ResultType<JSONObject> getOrderStatus(String orderSn) throws Exception;

    /**
     * 新增加接口
     * @return
     * @throws Exception
     */
    ResultType<JSONObject> getGoodsInfo(String accessToken,String sku) throws Exception;
}
