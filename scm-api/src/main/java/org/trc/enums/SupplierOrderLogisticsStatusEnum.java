package org.trc.enums;

/**
 * 供应商订单物流状态
 * Created by hzqph on 2017/5/16.
 */
public enum SupplierOrderLogisticsStatusEnum {
    CREATE("0","新建"),COMPLETE("1","妥投"),REJECT("2","拒收");

    public static SupplierOrderLogisticsStatusEnum queryNameByCode(String code){
        for(SupplierOrderLogisticsStatusEnum auditStatusEnum: SupplierOrderLogisticsStatusEnum.values()){
            if (auditStatusEnum.getCode().equals(code)){
                return auditStatusEnum;
            }
        }
        return null;
    }

    SupplierOrderLogisticsStatusEnum(String code, String name) {
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
