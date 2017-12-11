package org.trc.enums;

/**
 * 供应商订单类型
 * Created by hzqph on 2017/5/16.
 */
public enum SupplierOrderTypeEnum {
    JD("0","京东"),LY("1","粮油"),ZC("2","自采");

    public static SupplierOrderTypeEnum queryNameByCode(String code){
        for(SupplierOrderTypeEnum auditStatusEnum: SupplierOrderTypeEnum.values()){
            if (auditStatusEnum.getCode().equals(code)){
                return auditStatusEnum;
            }
        }
        return null;
    }

    SupplierOrderTypeEnum(String code, String name) {
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
