package org.trc.enums;

/**
 * Created by wangyz on 2017/11/15.
 */
public enum ValidStateEnum {
    DISABLE(0,"停用"),ENABLE(1,"启用");

    ValidStateEnum(Integer code, String name) {
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
