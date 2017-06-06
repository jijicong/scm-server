package org.trc.enums;

/**
 * Created by hzwyz on 2017/6/1 0001.
 */
public enum JingDongEnum {
    /**
     * 订单编码按模块划分：
     * 订单管理:000开头
     * 外部调用:1000开头
     * 数据库:3000开头
     * 系统异常:4000开头
     */

    //统一下单
    /*ORDER_SUCCESS("1001","下单成功"),
    ORDER_FALSE("1002","下单失败"),
    ORDER_ERROR("1003","下单异常"),*/
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
    ERROR_GET_ADDRESS("0001010","查询地址异常");


    private String code;
    private String name;

    JingDongEnum(String code, String name){
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
