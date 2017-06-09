package org.trc.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by hzwyz on 2017/6/3 0003.
 */
public enum JingDongEnum {

    ERROR_ORDER_BILL("0001001","统一下单异常"),

    ERROR_ORDER_CONFIRM("0001001","确认预占库存订单异常"),

    ERROR_ORDER_CANCEL("0001002","取消未确认订单异常"),

    ERROR_DO_PAY("0001003","发起支付异常"),

    ERROR_SELECT_JDORDERID_BY_THIRDORDER("0001004","订单反查异常"),

    ERROR_SELECT_JDORDER("0001005","查询京东订单信息异常"),

    ERROR_ORDER_TRACK("0001006","查询配送信息异常"),

    ERROR_GET_SELL_PRICE("0001007","查询商品价格异常"),

    ERROR_GET_NEW_STOCK_BY_ID("0001008","查询商品库存异常"),

    ERROR_GET_STOCK_BY_ID("0001009","查询商品库存异常"),

    ERROR_GET_ADDRESS("0001010","查询地址异常"),

    ERROR_GET_TOKEN("0001011","获取TOKEN失败"),

    ERROR_MESSAGE_GET("0001012","获取推送信息异常"),

    ERROR_MESSAGE_DEL("0001013","删除推送信息异常"),

    ERROR_SEARCH("0001014","商品搜索异常"),

    ERROR_GET_YANBAO_SKU("0001015","查询商品延保异常"),

    ERROR_GET_PROVINCE("0001016","获取一级地址异常"),

    ERROR_GET_CITY("0001017","获取二级地址异常"),

    ERROR_GET_COUNTY("0001018","获取三级地址异常"),

    ERROR_GET_TOWN("0001019","获取四级地址异常"),

    ERROR_CHECK_AREA("0001020","检查四级地址异常"),

    ERROR_TOKEN("0001021","创建Token出错"),
    ERROR_REFRESH_TOKEN ("0001022","刷新Access Token出错"),

    ERROR_GET_PAGE_NUM ("0001023","获取商品池异常"),

    ERROR_GET_SKU("0001024","获取商品池内商品编号异常"),

    ERROR_GET_DETAIL("0001025","获取商品池内商品编号异常"),

    ERROR_GET_SKU_BY_PAGE("0001026","获取品类商品池编号异常"),

    ERROR_CHECK_SKU("0001027","获取商品是否可用异常"),

    ERROR_SKU_STATE("0001028","获取商品上下架状态异常"),

    ERROR_SKU_IMAGE("0001029","获取商品图片信息异常"),

    ERROR_CHECK_LIMIT("0001030","获取商品区域购买限制信息异常");
    private String code;
    private String message;

    JingDongEnum(String code, String message){
        this.code = code;
        this.message = message;
    }


    /**
     *
     * @Title: getJingDongEnumByCode
     * @Description: 根据枚举编码获取枚举
     * @param @param name
     * @param @return
     * @return JingDongEnum
     * @throws
     */
    public static JingDongEnum getJingDongEnumByCode(String code){
        for(JingDongEnum jdEnum : JingDongEnum.values()){
            if(StringUtils.equals(jdEnum.getCode(), code)){
                return jdEnum;
            }
        }
        return null;
    }

    public String getMessage() {
        return message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
