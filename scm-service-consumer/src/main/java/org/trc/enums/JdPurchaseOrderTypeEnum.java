package org.trc.enums;

/**
 * 京东采购单订单类型
 * Created by hzwdx on 2017/7/1.
 */
public enum JdPurchaseOrderTypeEnum {

    B2B("1","B2B"),
    B2C("2","B2C");


    public static JdPurchaseOrderTypeEnum queryNameByCode(Integer code){
        for(JdPurchaseOrderTypeEnum invoiceStateEnum: JdPurchaseOrderTypeEnum.values()){
            if (invoiceStateEnum.getCode().equals(code)){
                return invoiceStateEnum;
            }
        }
        return null;
    }
    JdPurchaseOrderTypeEnum(String code, String name) {
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
