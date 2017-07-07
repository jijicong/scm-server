package org.trc.enums;

/**
 * 供应商物流
 * Created by hzqph on 2017/5/16.
 */
public enum SupplierLogisticsEnum {
    JD("0","京东"),LY("1","粮油");

    public static SupplierLogisticsEnum queryNameByCode(String code){
        for(SupplierLogisticsEnum auditStatusEnum: SupplierLogisticsEnum.values()){
            if (auditStatusEnum.getCode().equals(code)){
                return auditStatusEnum;
            }
        }
        return null;
    }

    SupplierLogisticsEnum(String code, String name) {
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
