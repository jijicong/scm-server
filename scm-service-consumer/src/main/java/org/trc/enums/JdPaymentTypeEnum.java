package org.trc.enums;

/**
 * 京东支付方式
 * Created by hzwdx on 2017/7/1.
 */
public enum JdPaymentTypeEnum {

    GOODS_RECEIVE(1,"货到付款"),
    POST_OFFICE(2,"邮局付款"),
    ON_LINE(4,"在线支付"),
    COMPANY_TRANSFER(5,"公司转账"),
    BANK_TRANSFER(6,"银行转账"),
    ON_LINE_BANKING_WALLET(7,"网银钱包"),
    JING_CAI_PAY(101,"金采支付");

    public static JdPaymentTypeEnum queryNameByCode(Integer code){
        for(JdPaymentTypeEnum jdPaymentTypeEnum: JdPaymentTypeEnum.values()){
            if (jdPaymentTypeEnum.getCode().equals(code)){
                return jdPaymentTypeEnum;
            }
        }
        return null;
    }
    JdPaymentTypeEnum(Integer code, String name) {
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
