package org.trc.enums;

/**
 * 渠道订单下单结果状态
 */
public enum NoticeChannelStatusEnum {
    SUCCESS("200","成功"),
    FAILED("0","失败"),
    CANCEL("2","取消");

    NoticeChannelStatusEnum(String code, String name) {
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
