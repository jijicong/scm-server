package org.trc.enums;

/**
 * 订单商品发货状态状态
 * Created by hzqph on 2017/5/16.
 */
public enum OrderItemDeliverStatusEnum {
    //WAIT_FOR_SUBMIT("1","待发送"),SUBMIT("2","已发送"),WAIT_FOR_DELIVER("3","待发货"),DELIVER("4","已发货"),SUBMIT_FAILURE("5","下单失败");
    ORDER_SPLITING("0","正在拆单"),WAIT_FOR_SUBMIT("1","待发送供应商"),ORDER_EXCEPTION("2","供应商下单异常"),WAIT_FOR_DELIVER("3","等待供应商发货"),ALL_DELIVER("4","全部发货"),
    ORDER_FAILURE("5","供应商下单失败"),PARTS_DELIVER("6","部分发货"),ORDER_CANCEL("7","已取消"),WAREHOUSE_RECIVE_FAILURE("8","仓库接收失败"),WAIT_WAREHOUSE_DELIVER("9","等待仓库发货"),
    WAIT_HANDLER("10","待了结"),HANDLERED("11","已了结"),WAREHOUSE_MIDDEL_STATUS("12","仓库告知的过程中状态");
    public static OrderItemDeliverStatusEnum queryNameByCode(String code){
        for(OrderItemDeliverStatusEnum auditStatusEnum: OrderItemDeliverStatusEnum.values()){
            if (auditStatusEnum.getCode().equals(code)){
                return auditStatusEnum;
            }
        }
        return null;
    }

    OrderItemDeliverStatusEnum(String code, String name) {
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
