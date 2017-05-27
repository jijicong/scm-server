package org.trc.biz.jingdong;

import org.trc.biz.impl.jingdong.util.Model.AddressDO;

/**
 * Created by hzwyz on 2017/5/19 0019.
 */
public interface IJingDongBiz {

    /**
     * 获取AccessToken
     * @return
     * @throws Exception
     */
    public String getAccessToken() throws Exception;


    public String billOrder() throws Exception;

    /**
     * 获取库存接口
     * @param sku 商品编号
     * @param area 地址 jsonStr 类似 {"province"："浙江","city":"杭州市","county":"滨江区"}
     * @return
     * @throws Exception
     */
    public String getStockById(String sku, String area) throws Exception;

    /**
     * 获取京东地址编码
     * @param province 省编码
     * @param city 市编码
     * @param county 县区编码
     * @return
     * @throws Exception
     */
    String getAddress(String province, String city, String county) throws Exception;
}
