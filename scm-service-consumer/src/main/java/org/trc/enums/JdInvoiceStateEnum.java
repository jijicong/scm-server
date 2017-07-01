package org.trc.enums;

/**
 * 京东开票方式
 * Created by hzwdx on 2017/7/1.
 */
public enum JdInvoiceStateEnum {

    ORDER_PRE_BORROWING(0,"订单预借"),
    FOLLOW_GOODS(1,"随货开票"),
    FOCUS(2,"集中开票");


    public static JdInvoiceStateEnum queryNameByCode(Integer code){
        for(JdInvoiceStateEnum invoiceStateEnum: JdInvoiceStateEnum.values()){
            if (invoiceStateEnum.getCode().equals(code)){
                return invoiceStateEnum;
            }
        }
        return null;
    }
    JdInvoiceStateEnum(Integer code, String name) {
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
