package org.trc.enums;

/**
 * 京东发票类型
 * Created by hzwdx on 2017/7/1.
 */
public enum JdInvoiceTypeEnum {

    NORMAL(1,"普通发票"),
    VALUE_ADDED_TAX(2,"增值税发票");


    public static JdInvoiceTypeEnum queryNameByCode(Integer code){
        for(JdInvoiceTypeEnum invoiceStateEnum: JdInvoiceTypeEnum.values()){
            if (invoiceStateEnum.getCode().equals(code)){
                return invoiceStateEnum;
            }
        }
        return null;
    }
    JdInvoiceTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    private Integer code;
    private String name;

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
