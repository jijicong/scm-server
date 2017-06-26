package org.trc.enums;

/**
 * Created by hzqph on 2017/6/26.
 */
public enum LogOperationEnum {
    ADD("新增"),UPDATE("修改"),DELETE("删除");

    private String message;

    LogOperationEnum(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
