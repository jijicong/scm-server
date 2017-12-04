package org.trc.enums;

/**
 * Created by hzqph on 2017/5/2.
 */
public enum OrderTypeEnum {
    SELF_PURCHARSE("0","自采"),SUPPLIER("1","代发");


    private String code;
    private String name;
    OrderTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static OrderTypeEnum queryNameByCode(Integer code){
        for(OrderTypeEnum sourceEnum: OrderTypeEnum.values()){
            if (sourceEnum.getCode().equals(code)){
                return sourceEnum;
            }
        }
        return null;
    }

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
