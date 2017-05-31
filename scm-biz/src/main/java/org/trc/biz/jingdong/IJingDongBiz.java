package org.trc.biz.jingdong;

import com.alibaba.fastjson.JSONArray;
import org.trc.biz.impl.jingdong.util.Model.AddressDO;
import org.trc.form.JDModel.SellPriceDO;
import org.trc.form.JDModel.StockDO;

import java.util.List;

/**
 * Created by hzwyz on 2017/5/19 0019.
 */
public interface IJingDongBiz {

    /**
     * 获取AccessToken
     * @return
     * @throws Exception
     */
     String getAccessToken() throws Exception;


     String billOrder() throws Exception;


     List<SellPriceDO> getSellPrice(String sku) throws Exception;

    /**
     * 获取库存接口(商品列表页使用)
     * @param sku 商品编号 批量以逗号分隔
     * @param area 地址编码
     * @return
     * @throws Exception
     */
     List<StockDO> getStockById(String sku, AddressDO area) throws Exception;

    /**
     * 获取库存接口(订单详情页、下单使用)
     * @param skuNums 商品和数量 [{skuId: 569172,num:101}]
     * @param area 地址编码
     * @return
     * @throws Exception
     */
     List<StockDO> getNewStockById(JSONArray skuNums, AddressDO area) throws Exception;


    /**
     * 获取京东地址编码
     * @param province 省编码
     * @param city 市编码
     * @param county 县区编码
     * @return
     * @throws Exception
     */
     String getAddress(String province,String city,String county) throws Exception;

    void getSkuList() throws Exception;


}
