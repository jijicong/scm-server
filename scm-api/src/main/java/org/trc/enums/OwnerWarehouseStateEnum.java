package org.trc.enums;

/**
 * Created by wangyz on 2017/11/16.
 * 货主仓库状态枚举
 */
public enum OwnerWarehouseStateEnum {
    WAIT_NOTICE("0","待通知"),NOTICE_SUCCESS("1","通知成功"),NOTICE_FAILURE("2","通知失败"),IN_NOTICE("3","通知中");

    OwnerWarehouseStateEnum(String code, String name) {
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
