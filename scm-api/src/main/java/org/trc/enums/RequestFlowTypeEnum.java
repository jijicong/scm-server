package org.trc.enums;

/**
 * Created by hzdzf on 2017/6/6.
 */
public enum RequestFlowTypeEnum {

    /**
     * 渠道部分
     */
    BRAND_UPDATE_NOTICE("BRAND_UPDATE_NOTICE", "品牌变更通知"),
    PROPERTY_UPDATE_NOTICE("PROPERTY_UPDATE_NOTICE", "属性变更通知"),
    CATEFORY_UPDATE_NOTICE("CATEFORY_UPDATE_NOTICE", "分类变更通知"),
    CATEFORY_BRAND_UPDATE_NOTICE("BRAND_UPDATE_NOTICE", "分类品牌变更通知"),
    CATEFORY_PROPERTY_UPDATE_NOTICE("BRAND_UPDATE_NOTICE", "分类属性变更通知"),
    ITEM_UPDATE_NOTICE("ITEM_UPDATE_NOTICE", "自营商品变更通知"),
    EXTERNAL_ITEM_UPDATE_NOTICE("EXTERNAL_ITEM_UPDATE_NOTICE", "代发商品变更通知"),
    RECEIVE_CHANNEL_ORDER("RECEIVE_CHANNEL_ORDER", "接收渠道订单"),
    CHANNEL_RECEIVE_ORDER_SUBMIT_RESULT("CHANNEL_RECEIVE_ORDER_SUBMIT_RESULT", "渠道接收订单提交结果"),
    SEND_LOGISTICS_INFO_TO_CHANNEL("SEND_LOGISTICS_INFO_TO_CHANNEL", "发送物流信息给渠道"),
    CHANNEL_QUERY_LOGISTICS_INFO("CHANNEL_QUERY_LOGISTICS_INFO", "渠道查询物流信息"),

    /**
    京东部分
     */
    JD_SUBMIT_ORDER("SUBMIT_JD_ORDER", "提交京东订单"),
    JD_SKU_PRICE_UPDATE_SUBMIT_ORDER("JD_SKU_PRICE_UPDATE_SUBMIT_ORDER", "京东sku价格更新重新提交订单"),
    JD_SKU_PRICE_QUERY("JD_SKU_PRICE_QUERY", "京东查询sku价格"),
    JD_SKU_PRICE_UPDATE_NOTICE("JD_SKU_PRICE_UPDATE_NOTICE", "京东sku价格更新通知"),
    JD_LOGISTIC_INFO_QUERY("JD_LOGISTIC_INFO_QUERY", "京东物流信息查询"),
    NOTICE_UPDATE_SKU_USE_STATUS("NOTICE_UPDATE_SKU_USE_STATUS", "通知更新SKU使用状态"),
    /**
     * 粮油部分
     */
    //粮油订单提交
    LY_SUBMIT_ORDER("SUBMIT_LY_ORDER", "提交粮油订单"),
    LY_LOGISTIC_INFO_QUERY("LY_LOGISTIC_INFO_QUERY", "粮油物流信息查询")
    ;

    private String code;

    private String description;

    RequestFlowTypeEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
