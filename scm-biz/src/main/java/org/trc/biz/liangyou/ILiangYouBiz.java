package org.trc.biz.liangyou;

import com.alibaba.fastjson.JSONObject;
import org.trc.domain.config.SkuListForm;
import org.trc.form.liangyou.*;

import java.util.List;

/**
 * Created by hzwyz on 2017/6/13 0013.
 */
public interface ILiangYouBiz {
    /**
     * 获取access token
     * @return
     * @throws Exception
     */
    String getAccessToken() throws Exception;


    /**
     * 导出所有商品到数据库
     * @throws Exception
     */
    public void ExportGoods() throws Exception;

    /**
     * 获取商品信息
     * @return
     * @throws Exception
     */
    SkuListForm getSkuList() throws Exception;

    /**
     * 获取库存信息
     * @return
     * @throws Exception
     */
    List<CheckStockDO> checkStock(String sku) throws Exception;

    /**
     * 非签约订单导入
     * @return
     * @throws Exception
     */
    String addOutOrder(LiangYouOrderDO liangYouOrderDO) throws Exception;

    /**
     * 签约订单导入
     * @return
     * @throws Exception
     */
    String addToutOrder(LiangYouTorderDO orderDO) throws Exception;

    /**
     * 获取订单状态和运单号
     * @return
     * @throws Exception
     */
    String getOrderStatus(String orderSn) throws Exception;

    /**
     * 获取商品详情
     * @return
     * @throws Exception
     */
    GoodsInfoDO getGoodsInfo(String sku) throws Exception;


}
