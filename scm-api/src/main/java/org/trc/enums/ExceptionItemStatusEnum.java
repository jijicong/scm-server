package org.trc.enums;

/**
 * Created by hzqph on 2017/5/2.
 */
public enum ExceptionItemStatusEnum {
    WAIT_FINISH(1,"待了结"),FINISHED(2,"已了结");


    public static ExceptionItemStatusEnum queryNameByCode(Integer code){
        for(ExceptionItemStatusEnum sourceEnum: ExceptionItemStatusEnum.values()){
            if (sourceEnum.getCode().equals(code)){
                return sourceEnum;
            }
        }
        return null;
    }

    private Integer code;
    private String name;

    ExceptionItemStatusEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
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
