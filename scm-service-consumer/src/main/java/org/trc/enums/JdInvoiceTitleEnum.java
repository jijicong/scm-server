package org.trc.enums;

/**京东发票类型抬头
 * Created by hzwdx on 2017/7/1.
 */
public enum JdInvoiceTitleEnum {

    PERSONAL(4,"个人"),
    COMPANY(5,"单位");

    public static JdInvoiceTitleEnum queryNameByCode(Integer code){
        for(JdInvoiceTitleEnum jdInvoiceTitleEnum: JdInvoiceTitleEnum.values()){
            if (jdInvoiceTitleEnum.getCode().equals(code)){
                return jdInvoiceTitleEnum;
            }
        }
        return null;
    }
    JdInvoiceTitleEnum(Integer code, String name) {
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
