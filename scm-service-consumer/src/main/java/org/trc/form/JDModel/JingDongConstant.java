package org.trc.form.JDModel;

/**
 * Created by hzwyz on 2017/6/5 0005.
 */
public class JingDongConstant {
    //创建Token
    public static final String ACCESS_TOKEN = "/oauth2/accessToken";
    //刷新Token
    public static final String REFRESH_TOKEN = "/oauth2/refreshToken";
    //获取商品池编号
    public static final String GET_PAGE_NUM = "/api/product/getPageNum";
    //获取商品编号
    public static final String GET_SKU = "/api/product/getSku";
    //获取商品的详细信息
    public static final String GET_DETAIL = "/api/product/getDetail";
    //获取池内商品编号接口-品类商品池
    public static final String GET_SKU_BY_PAGE = "/api/product/getSkuByPage";
    //商品可售验证
    public static final String CHECK = "/api/product/check";
    //获取商品上下架状态
    public static final String SKU_STATE = "/api/product/skuState";
    //获取商品图片信息
    public static final String SKU_IMAGE = "/api/product/skuImage";
    //商品搜索
    public static final String SEARCH = "/api/search/search";
    //查询商品延保接口
    public static final String GET_YANBAO_SKU = "/api/product/getYanbaoSku";
    //获取一级地址
    public static final String GET_PROVINCE = "/api/area/getProvince";
    //获取二级地址
    public static final String GET_CITY = "/api/area/getCity";
    //获取三级地址
    public static final String GET_COUNTY = "/api/area/getCounty";
    //获取四级地址
    public static final String GET_TOWN = "/api/area/getTown";
    //验证四级地址是否正确
    public static final String CHECK_AREA = "/api/area/checkArea";
    //批量查询商品价格
    public static final String GET_SELL_PRICE = "/api/price/getSellPrice";
    //批量获取库存接口
    public static final String GET_NEW_STOCK_BY_ID = "/api/stock/getNewStockById";
    //批量获取库存接口
    public static final String GET_STOCK_BY_ID = "/api/stock/getStockById";
    //统一下单
    public static final String SUBMIT_ORDER = "/api/order/submitOrder";
    //确认预占库存订单
    public static final String CONFIRM_ORDER = "/api/order/confirmOrder";
    //取消未确认订单接口
    public static final String CANCEL = "/api/order/cancel";
    //发起支付
    public static final String DO_PAY = "/api/order/doPay";
    //订单反查接口
    public static final String SELECT_JDORDER_ID_BY_THIRD_ORDER = "/api/order/selectJdOrderIdByThirdOrder";
    //查询京东订单信息接口
    public static final String SELECT_JDORDER = "/api/order/selectJdOrder";
    //查询配送信息接口
    public static final String ORDER_TRACK = "/api/order/orderTrack";

    public static final String ERROR_TOKEN = "创建Token出错";

    public static final String ERROR_REFRESH_TOKEN = "刷新Access Token出错";

    public static final String ERROR_GET_PAGE_NUM = "获取商品池异常";

    public static final String ERROR_GET_SKU = "获取商品池内商品编号异常";

    public static final String ERROR_GET_DETAIL = "获取商品池内商品编号异常";

    public static final String ERROR_GET_SKU_BY_PAGE  = "获取品类商品池编号异常";

    public static final String ERROR_CHECK_SKU  = "获取商品是否可用异常";

    public static final String ERROR_SKU_STATE  = "获取商品上下架状态异常";

    public static final String ERROR_SKU_IMAGE  = "获取商品图片信息异常";

    public static final String ERROR_SEARCH = "商品搜索异常";

    public static final String ERROR_GET_YANBAO_SKU = "查询商品延保异常";

    public static final String ERROR_GET_PROVINCE = "获取一级地址异常";

    public static final String ERROR_GET_CITY = "获取二级地址异常";

    public static final String ERROR_GET_COUNTY = "获取三级地址异常";

    public static final String ERROR_GET_TOWN = "获取四级地址异常";

    public static final String ERROR_CHECK_AREA = "检查四级地址异常";

    public static final String ERROR_GET_SELL_PRICE = "查询商品价格异常";

    public static final String ERROR_GET_NEW_STOCK_BY_ID = "查询库存异常";

    public static final String ERROR_GET_STOCK_BY_ID = "查询库存异常";

    public static final String ERROR_SUBMIT_ORDER = "统一下单异常";

    public static final String ERROR_CONFIRM_ORDER = "确认预占库存异常";

    public static final String ERROR_CANCEL_ORDER = "取消未确认订单异常";

    public static final String ERROR_DO_PAY = "发起支付异常";

    public static final String ERROR_SELECT_JDORDERID_BY_THIRDORDER = "订单反查异常";

    public static final String ERROR_SELECT_JDORDERID = "查询京东订单信息异常";

    public static final String ERROR_ORDER_TRACK = "查询配送信息异常";
}
