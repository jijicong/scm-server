package org.trc.service;


import org.trc.form.JDModel.OrderDO;
import org.trc.form.JDModel.SearchDO;
import org.trc.form.JDModel.SellPriceDO;
import org.trc.form.JDModel.StockDO;

import java.util.List;

/**
 * Created by hzwyz on 2017/5/18 0018.
 */
public interface IJDService {

    /**
     *创建Token
     * @param
     * @return 返回Access_Token,RefreshToken
     * @throws Exception
     */
    public String createToken() throws Exception;

    /**
     * 使用RefreshToken刷新AccessToken
     * @param refreshToken
     * @return
     */
    public String freshAccessTokenByRefreshToken(String refreshToken) throws Exception;

    /**
     * 获取商品池编号
     * @param token 授权时的access token
     * @return
     */
    public String getPageNum(String token)throws Exception;

    /**
     * 获取商品编号
     * @param token 授权时的access token
     * @param pageNum 池子编号
     * @return
     */
    public String getSku(String token, String pageNum) throws Exception;


    /**
     * 获取商品的详细信息
     * @param token 授权时的access token
     * @param sku 商品编号
     * @param isShow 查询商品基本信息
     * @return
     */
    public String getDetail(String token,String sku, Boolean isShow) throws Exception;

    /**
     * 获取商品上下架状态
     * @param token 授权时的access token
     * @param sku 商品编号 支持批量（最高100个）
     * @return
     */
    public String skuState(String token,String sku) throws Exception;

    /**
     * 获取商品图片信息
     * @param token 授权时的access token
     * @param sku 商品编号 支持批量（最高100个）
     * @return
     */
    public String skuImage(String token,String sku) throws Exception;

    /**
     * 商品搜索
     * 关键字+分页+页码，使用价格区间、品牌首字母、分类作为筛选条件
     * @param searchDO
     * @return
     */
    public String search(SearchDO searchDO)throws Exception;

    /**
     * 获取一级地址
     * @param token
     * @return
     * @throws Exception
     */
    public String getProvince(String token) throws Exception;

    /**
     * 获取二级地址
     * @param token
     * @param id 一级地址
     * @return
     * @throws Exception
     */
    public String getCity(String token,String id) throws Exception;

    /**
     * 获取三级地址
     * @param token
     * @param id 二级地址
     * @return
     * @throws Exception
     */
    public String getCounty(String token,String id) throws Exception;

    /**
     * 获取四级地址
     * @param token
     * @param id 三级地址
     * @return
     * @throws Exception
     */
    public String getTown(String token,String id) throws Exception;

    /**
     * 验证四级地址是否正确
     * @param token
     * @param provinceId 一级地址
     * @param cityId 二级地址
     * @param countyId 三级地址
     * @param townId 四级地址
     * @return
     * @throws Exception
     */
    public String checkArea(String token,String provinceId,String cityId,
                            String countyId,String townId) throws Exception;

    /**
     * 批量查询商品售卖价
     * @param token 授权时的access token
     * @param sku 商品编号 支持批量（最高100个）
     * @return
     * @throws Exception
     */
    public List<SellPriceDO> getSellPrice(String token, String sku) throws Exception;

    /**
     * 批量获取库存接口（建议订单详情页、下单使用）
     * @param token 授权时的access token
     * @param skuNums 商品和数量
     * @param area 地址
     * @return
     * @throws Exception
     */
    public List<StockDO> getNewStockById(String token,String skuNums,String area) throws Exception;

    /**
     * 批量获取库存接口（建议商品列表页使用）
     * @param token 授权时的access token
     * @param sku 商品编号
     * @param area 地址
     * @return
     * @throws Exception
     */
    public List<StockDO> getStockById(String token, String sku, String area) throws Exception;

    /**
     * 统一下单
     * @param orderDO 订单类
     * @return
     * @throws Exception
     */
    public String submitOrder(OrderDO orderDO) throws Exception;

    /**
     * 确认预占库存订单
     * @param token
     * @param jdOrderId 京东的订单单号(父订单号)
     * @return
     * @throws Exception
     */
    public String confirmOrder(String token,String jdOrderId) throws Exception;

    /**
     * 取消未确认订单接口
     * @param token
     * @param jdOrderId 京东的订单单号(父订单号)
     * @return
     * @throws Exception
     */
    public String cancel(String token,String jdOrderId) throws Exception;

    /**
     * 发起支付
     * @param token
     * @param jdOrderId 京东的订单单号(父订单号)
     * @return
     * @throws Exception
     */
    public String doPay(String token,String jdOrderId) throws Exception;

    /**
     * 查询京东订单信息接口
     * @param token
     * @param jdOrderId 京东的订单单号(父订单号)
     * @return
     * @throws Exception
     */
    public String selectJdOrder(String token,String jdOrderId) throws Exception;

    /**
     * 查询配送信息接口
     * @param token
     * @param jdOrderId 京东的订单单号(父订单号)
     * @return
     * @throws Exception
     */
    public String orderTrack(String token,String jdOrderId) throws Exception;


}
