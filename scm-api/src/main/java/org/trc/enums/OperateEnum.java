package org.trc.enums;

/**
 * Created by hzwyz on 2017/10/10 0010.
 */
public enum OperateEnum {
    OK(0,"-"),HANDLE(2,"已了结"),PENDING(1,"待了结");

    OperateEnum(Integer code, String name) {
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
