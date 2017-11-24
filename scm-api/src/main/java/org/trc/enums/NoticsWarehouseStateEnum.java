package org.trc.enums;

/**
 * Created by wangyz on 2017/11/17.
 */
public enum NoticsWarehouseStateEnum {
    UN_NOTICS(0,"待通知"),FAIL(1,"通知失败"),CANCEL(2,"取消通知"),NOTICING(3,"通知中"),SUCCESS(4,"通知成功");

    NoticsWarehouseStateEnum(Integer code, String name) {
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
