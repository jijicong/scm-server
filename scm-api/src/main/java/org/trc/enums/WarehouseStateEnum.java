package org.trc.enums;

/**
 * Created by wangyz on 2017/11/16.
 */
public enum WarehouseStateEnum {
    UN_NOTIC("0","待通知"),SUCCESS("1","通知成功"),FAIL("2","通知失败");

    WarehouseStateEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    private String code;
    private String name;

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
