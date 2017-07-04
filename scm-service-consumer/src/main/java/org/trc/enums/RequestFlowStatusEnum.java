package org.trc.enums;


/**
 * Created by ding on 2017/6/26.
 */
public enum RequestFlowStatusEnum {

    SUCCESS("SEND_SUCCESS", "发送消息成功"),
    FAILED("SEND_FAILED", "发送失败"),
    SEND_TIME_OUT("SEND_TIME_OUT", "请求超时");


    private String code;

    private String description;


    RequestFlowStatusEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
