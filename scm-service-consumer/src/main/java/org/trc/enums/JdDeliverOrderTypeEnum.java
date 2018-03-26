package org.trc.enums;

/**
 * 京东发货单订单类型
 * Created by hzwdx on 2017/7/1.
 */
public enum JdDeliverOrderTypeEnum {

    B2C("1","B2C订单"),
    B2B("2","B2B订单");


    public static JdDeliverOrderTypeEnum queryNameByCode(Integer code){
        for(JdDeliverOrderTypeEnum invoiceStateEnum: JdDeliverOrderTypeEnum.values()){
            if (invoiceStateEnum.getCode().equals(code)){
                return invoiceStateEnum;
            }
        }
        return null;
    }
    JdDeliverOrderTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    private String code;
    private String name;

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
