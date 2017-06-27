package org.trc.enums;

/**
 * Created by hzqph on 2017/6/26.
 */
public enum remarkEnum {
    VALID_ON("状态更新为启用"),VALID_OFF("状态更新为停用");
    private String message;

    remarkEnum(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
