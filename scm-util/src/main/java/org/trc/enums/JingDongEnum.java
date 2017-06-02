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
    ORDER_SUCCESS("1001","下单成功"),
    ORDER_FALSE("1002","下单失败");

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
