package org.trc.biz.jingdong;

import com.alibaba.fastjson.JSONArray;
import org.trc.form.jingdong.AddressDO;
import org.trc.form.jingdong.NewStockDO;
import org.trc.form.JDModel.OrderDO;
import org.trc.form.JDModel.SellPriceDO;
import org.trc.form.JDModel.StockDO;

import java.util.List;

/**
 * Created by hzwyz on 2017/5/19 0019.
 */
public interface IJingDongBiz {

    /**
     * 获取AccessToken
     *
     * @return
     * @throws Exception
     */
    String getAccessToken() throws Exception;


    /**
     * 统一下单接口
     *
     * @param orderDO 订单
     * @return
     * @throws Exception
     */
    String billOrder(OrderDO orderDO) throws Exception;

    /**
     * 确认预占库存订单
     *
     * @param jdOrderId 京东的订单单号
     * @return
     * @throws Exception
     */
    String confirmOrder(String jdOrderId) throws Exception;

    /**
     * 取消未确认订单接口
     *
     * @param jdOrderId
     * @return
     * @throws Exception
     */
    String cancel(String jdOrderId) throws Exception;

    /**
     * 发起支付接口
     *
     * @param jdOrderId
     * @return
     * @throws Exception
     */
    String doPay(String jdOrderId) throws Exception;

    /**
     * 订单反查接口
     *
     * @param jdOrderId
     * @return
     * @throws Exception
     */
    String selectJdOrderIdByThirdOrder(String jdOrderId) throws Exception;

    /**
     * 查询京东订单信息接口
     *
     * @param jdOrderId
     * @return
     * @throws Exception
     */
    String selectJdOrder(String jdOrderId) throws Exception;

    /**
     * 查询配送信息接口
     *
     * @param jdOrderId
     * @return
     * @throws Exception
     */
    String orderTrack(String jdOrderId) throws Exception;

    /**
     * 查询商品价格
     *
     * @param sku 商品编号 批量以逗号分隔
     * @return
     * @throws Exception
     */
    List<SellPriceDO> getSellPrice(String sku) throws Exception;

    /**
     * 获取库存接口(商品列表页使用)
     *
     * @param sku  商品编号 批量以逗号分隔
     * @param area 地址编码
     * @return
     * @throws Exception
     */
    List<StockDO> getStockById(String sku, AddressDO area) throws Exception;

    /**
     * 获取库存接口(订单详情页、下单使用)
     *
     * @param skuNums 商品和数量 [{skuId: 569172,num:101}]
     * @param area    地址编码
     * @return
     * @throws Exception
     */
    List<NewStockDO> getNewStockById(JSONArray skuNums, AddressDO area) throws Exception;


    /**
     * 获取京东地址编码
     *
     * @param province 省编码
     * @param city     市编码
     * @param county   县区编码
     * @return
     * @throws Exception
     */
    String getAddress(String province, String city, String county) throws Exception;

    void getSkuList() throws Exception;


}
