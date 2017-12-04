package org.trc.enums;

/**
 * Created by wangyz on 2017/11/16.
 * 商品通知仓库状态枚举
 */
public enum ItemNoticeStateEnum {
    WAIT_NOTICE("0","待通知"),NOTICE_FAILURE("1","通知失败"),NOTICE_CANCEL("2","取消通知"),IN_NOTICE("3","通知中"),NOTICE_SUCCESS("4","通知成功");

    ItemNoticeStateEnum(String code, String name) {
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
