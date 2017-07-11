package org.trc.enums;

/**
 * Created by hzqph on 2017/5/16.
 */
public enum OrderSubmitStatusEnum {
    SUBMIT("0","订单提交"),FAILURE("1","下单失败"),SUCCESS("2","下单成功");

    public static OrderSubmitStatusEnum queryNameByCode(String code){
        for(OrderSubmitStatusEnum auditStatusEnum: OrderSubmitStatusEnum.values()){
            if (auditStatusEnum.getCode().equals(code)){
                return auditStatusEnum;
            }
        }
        return null;
    }

    OrderSubmitStatusEnum(String code, String name) {
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
