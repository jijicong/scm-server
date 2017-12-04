package org.trc.enums;

/**
 * Created by hzqph on 2017/5/2.
 * 异常订单处理状态枚举
 */
public enum ExceptionTypeEnum {
    STOCK_LESS_REFUSE(1,"缺货退回"),STOCK_LESS_WAIT(2,"缺货等待");


    private Integer code;
    private String name;
    ExceptionTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static ExceptionTypeEnum queryNameByCode(Integer code){
        for(ExceptionTypeEnum sourceEnum: ExceptionTypeEnum.values()){
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
