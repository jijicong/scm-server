package org.trc.enums.allocateOrder;

/**
 * 调拨入库单状态枚举
 */
public enum AllocateInOrderStatusEnum {
    WAIT_OUT_FINISH(0,"待完成出库"),OUT_WMS_FINISH(1,"出库完成"),OUT_WMS_EXCEPTION(2,"出库异常"),RECIVE_WMS_RECIVE_SUCCESS(3,"入库仓接收成功"),
    RECIVE_WMS_RECIVE_FAILURE(4,"入库仓接收失败"),IN_WMS_FINISH(5,"入库完成"),IN_WMS_EXCEPTION(6,"入库异常"),CANCEL(7,"已取消");


    public static AllocateInOrderStatusEnum queryNameByCode(Integer code){
        for(AllocateInOrderStatusEnum sourceEnum: AllocateInOrderStatusEnum.values()){
            if (sourceEnum.getCode().equals(code)){
                return sourceEnum;
            }
        }
        return null;
    }

    private Integer code;
    private String name;

    AllocateInOrderStatusEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
