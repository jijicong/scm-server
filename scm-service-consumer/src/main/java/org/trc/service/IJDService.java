package org.trc.service;


import org.trc.form.JDModel.*;
import org.trc.form.SupplyItemsExt;
import org.trc.form.liangyou.LiangYouOrder;
import org.trc.util.Pagenation;

import java.util.List;

/**
 * Created by hzwyz on 2017/5/18 0018.
 */
public interface IJDService {

    /**
     * 创建Token
     *
     * @param
     * @return 返回Access_Token, RefreshToken
     * @throws Exception
     */
    ReturnTypeDO createToken() throws Exception;

    /**
     * 使用RefreshToken刷新AccessToken
     *
     * @param refreshToken
     * @return
     */
    ReturnTypeDO freshAccessTokenByRefreshToken(String refreshToken) throws Exception;

    /**
     * 获取商品池编号
     *
     * @param token 授权时的access token
     * @return
     */
    ReturnTypeDO getPageNum(String token) throws Exception;

    /**
     * 获取商品编号
     *
     * @param token   授权时的access token
     * @param pageNum 池子编号
     * @return
     */
    ReturnTypeDO getSku(String token, String pageNum) throws Exception;


    /**
     * 获取商品的详细信息
     *
     * @param token  授权时的access token
     * @param sku    商品编号
     * @param isShow 查询商品基本信息
     * @return
     */
    ReturnTypeDO getDetail(String token, String sku, Boolean isShow) throws Exception;


    /**
     * 获取品类池商品编号
     *
     * @param token   access token
     * @param pageNum 池子编号
     * @param pageNo  页码
     * @return
     * @throws Exception
     */
    ReturnTypeDO getSkuByPage(String token, String pageNum, String pageNo) throws Exception;

    /**
     * 商品可售验证
     *
     * @param token
     * @param skuIds 商品编号
     */
    ReturnTypeDO checkSku(String token, String skuIds) throws Exception;

    /**
     * 获取商品上下架状态
     *
     * @param token 授权时的access token
     * @param sku   商品编号 支持批量（最高100个）
     * @return
     */
    ReturnTypeDO skuState(String token, String sku) throws Exception;

    /**
     * 获取商品图片信息
     *
     * @param token 授权时的access token
     * @param sku   商品编号 支持批量（最高100个）
     * @return
     */
    ReturnTypeDO skuImage(String token, String sku) throws Exception;

    /**
     * 商品区域购买限制查询
     *
     * @param token    access token
     * @param skuIds   商品编号
     * @param province 京东一级地址编号
     * @param city     京东二级地址编号
     * @param county   京东三级地址编号
     * @param town     京东四级地址编号
     * @return
     * @throws Exception
     */
    ReturnTypeDO checkAreaLimit(String token, String skuIds, String province, String city, String county, String town) throws Exception;

    /**
     * 商品搜索
     * 关键字+分页+页码，使用价格区间、品牌首字母、分类作为筛选条件
     *
     * @param searchDO
     * @return
     */
    ReturnTypeDO search(SearchDO searchDO) throws Exception;

    /**
     * 查询商品延保接口
     *
     * @param token    access token
     * @param skuIds   商品信息
     * @param province 省编号
     * @param city     市编号
     * @param county   县区编号
     * @param town
     * @return
     * @throws Exception
     */
    ReturnTypeDO getYanbaoSku(String token, String skuIds, int province, int city, int county, int town) throws Exception;

    /**
     * 获取一级地址
     *
     * @param token
     * @return
     * @throws Exception
     */
    ReturnTypeDO getProvince(String token) throws Exception;

    /**
     * 获取二级地址
     *
     * @param token
     * @param id    一级地址
     * @return
     * @throws Exception
     */
    ReturnTypeDO getCity(String token, String id) throws Exception;

    /**
     * 获取三级地址
     *
     * @param token
     * @param id    二级地址
     * @return
     * @throws Exception
     */
    ReturnTypeDO getCounty(String token, String id) throws Exception;

    /**
     * 获取四级地址
     *
     * @param token
     * @param id    三级地址
     * @return
     * @throws Exception
     */
    ReturnTypeDO getTown(String token, String id) throws Exception;

    /**
     * 验证四级地址是否正确
     *
     * @param token
     * @param provinceId 一级地址
     * @param cityId     二级地址
     * @param countyId   三级地址
     * @param townId     四级地址
     * @return
     * @throws Exception
     */
    ReturnTypeDO checkArea(String token, String provinceId, String cityId,
                           String countyId, String townId) throws Exception;

    /**
     * 批量查询商品售卖价
     *
     * @param token 授权时的access token
     * @param sku   商品编号 支持批量（最高100个）
     * @return
     * @throws Exception
     */
    ReturnTypeDO getSellPrice(String token, String sku) throws Exception;

    /**
     * 批量获取库存接口（建议订单详情页、下单使用）
     *
     * @param token   授权时的access token
     * @param skuNums 商品和数量
     * @param area    地址
     * @return
     * @throws Exception
     */
    ReturnTypeDO getNewStockById(String token, String skuNums, String area) throws Exception;

    /**
     * 批量获取库存接口（建议商品列表页使用）
     *
     * @param token 授权时的access token
     * @param sku   商品编号
     * @param area  地址
     * @return
     * @throws Exception
     */
    ReturnTypeDO getStockById(String token, String sku, String area) throws Exception;

    /**
     * 统一下单
     *
     * @param orderDO 订单类
     * @return
     * @throws Exception
     */
    ReturnTypeDO submitOrder(String token, OrderDO orderDO) throws Exception;

    /**
     * 确认预占库存订单
     *
     * @param token
     * @param jdOrderId 京东的订单单号(父订单号)
     * @return
     * @throws Exception
     */
    ReturnTypeDO confirmOrder(String token, String jdOrderId) throws Exception;

    /**
     * 取消未确认订单接口
     *
     * @param token
     * @param jdOrderId 京东的订单单号(父订单号)
     * @return
     * @throws Exception
     */
    ReturnTypeDO cancel(String token, String jdOrderId) throws Exception;

    /**
     * 发起支付
     *
     * @param token
     * @param jdOrderId 京东的订单单号(父订单号)
     * @return
     * @throws Exception
     */
    ReturnTypeDO doPay(String token, String jdOrderId) throws Exception;

    /**
     * 订单反查接口
     *
     * @param token      access token
     * @param thirdOrder 客户系统订单号
     * @return
     * @throws Exception
     */
    ReturnTypeDO selectJdOrderIdByThirdOrder(String token, String thirdOrder) throws Exception;

    /**
     * 查询京东订单信息接口
     *
     * @param token
     * @param jdOrderId 京东的订单单号(父订单号)
     * @return
     * @throws Exception
     */
    ReturnTypeDO selectJdOrder(String token, String jdOrderId) throws Exception;

    /**
     * 查询配送信息接口
     *
     * @param token
     * @param jdOrderId 京东的订单单号(父订单号)
     * @return
     * @throws Exception
     */
    ReturnTypeDO orderTrack(String token, String jdOrderId) throws Exception;

    /**
     * 信息推送接口
     *
     * @param token
     * @param type  推送类型（非必须）
     * @return
     * @throws Exception
     */
    ReturnTypeDO get(String token, String type) throws Exception;

    /**
     * 信息推送接口
     *
     * @param token
     * @param id    推送id
     * @return
     * @throws Exception
     */
    ReturnTypeDO del(String token, String id) throws Exception;

    /**
     *
     * @param form
     * @param page
     * @return
     * @throws Exception
     */
    ReturnTypeDO skuPage(SupplyItemsExt form, Pagenation<SupplyItemsExt> page) throws Exception;

    /**
     * 通知更新Sku使用状态
     * @param skuDOList
     * @return
     * @throws Exception
     */
    ReturnTypeDO noticeUpdateSkuUsedStatus(List<SkuDO> skuDOList);

    /**
     * 提交京东订单
     * @param jingDongOrder
     * @return
     * @throws Exception
     */
    ReturnTypeDO submitJingDongOrder(JingDongOrder jingDongOrder);

    /**
     * 提交粮油订单
     * @param liangYouOrder
     * @return
     * @throws Exception
     */
    ReturnTypeDO submitLiangYouOrder(LiangYouOrder liangYouOrder);


    /**
     * 查询代发供应商物流信息
     * @param warehouseOrderCode
     * @param flag 0-京东,1-粮油
     * @return
     */
    ReturnTypeDO getLogisticsInfo(String warehouseOrderCode, String flag);

    /**
     * 京东sku价格查询,多个sku用逗号分隔
     * @param skus
     * @return
     */
    ReturnTypeDO getSellPrice(String skus);

    /**
     * 通知更新京东sku价格,多个sku用逗号分隔
     * @param skus
     * @return
     */
    ReturnTypeDO updateSellPriceNotice(String skus);

}
