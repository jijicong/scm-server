package org.trc.enums;

/**
 * Created by wangyz on 2017/11/16.
 */
public enum NoticeSuccessEnum {
    UN_NOTIC(0,"否"),NOTIC(1,"是");

    NoticeSuccessEnum(Integer code, String name) {
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
