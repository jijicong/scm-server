package org.trc.enums;

/**
 * 订单发货状态
 * Created by hzqph on 2017/5/16.
 */
public enum OrderDeliverStatusEnum {
    WAIT_FOR_DELIVER("1","待发货"),ALL_DELIVER("4","全部发货"),PARTS_DELIVER("6","部分发货"),ORDER_CANCEL("7","已取消");
    public static OrderDeliverStatusEnum queryNameByCode(String code){
        for(OrderDeliverStatusEnum auditStatusEnum: OrderDeliverStatusEnum.values()){
            if (auditStatusEnum.getCode().equals(code)){
                return auditStatusEnum;
            }
        }
        return null;
    }

    OrderDeliverStatusEnum(String code, String name) {
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
