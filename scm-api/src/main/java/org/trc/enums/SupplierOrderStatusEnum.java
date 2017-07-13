package org.trc.enums;

/**
 * 供应商订单状态
 * Created by hzqph on 2017/5/16.
 */
public enum SupplierOrderStatusEnum {
    WAIT_FOR_SUBMIT("1","待发送"),SUBMIT("2","已发送"),WAIT_FOR_DELIVER("3","待发货"),DELIVER("4","已发货"),SUBMIT_FAILURE("5","下单失败");

    public static SupplierOrderStatusEnum queryNameByCode(String code){
        for(SupplierOrderStatusEnum auditStatusEnum: SupplierOrderStatusEnum.values()){
            if (auditStatusEnum.getCode().equals(code)){
                return auditStatusEnum;
            }
        }
        return null;
    }

    SupplierOrderStatusEnum(String code, String name) {
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
