package org.trc.enums;

/**
 * 仓库订单物流状态
 * Created by hzqph on 2017/5/16.
 */
public enum WarehouseOrderLogisticsStatusEnum {
    UN_COMPLETE("0","未完成"),COMPLETE("1","已完成");

    public static WarehouseOrderLogisticsStatusEnum queryNameByCode(String code){
        for(WarehouseOrderLogisticsStatusEnum auditStatusEnum: WarehouseOrderLogisticsStatusEnum.values()){
            if (auditStatusEnum.getCode().equals(code)){
                return auditStatusEnum;
            }
        }
        return null;
    }

    WarehouseOrderLogisticsStatusEnum(String code, String name) {
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
