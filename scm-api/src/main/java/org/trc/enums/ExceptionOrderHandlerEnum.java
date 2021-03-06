package org.trc.enums;

/**
 * Created by hzqph on 2017/5/2.
 * 异常订单处理状态枚举
 */
public enum ExceptionOrderHandlerEnum {
    WAIT_HANDLER(10,"待了结"),HANDLERED(11,"已了结");


    private Integer code;
    private String name;
    ExceptionOrderHandlerEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static ExceptionOrderHandlerEnum queryNameByCode(Integer code){
        for(ExceptionOrderHandlerEnum sourceEnum: ExceptionOrderHandlerEnum.values()){
            if (sourceEnum.getCode().equals(code)){
                return sourceEnum;
            }
        }
        return null;
    }

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
