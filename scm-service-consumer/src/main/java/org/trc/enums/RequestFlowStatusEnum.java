package org.trc.enums;


/**
 * Created by ding on 2017/6/26.
 */
public enum RequestFlowStatusEnum {

    SEND_SUCCESS("SEND_SUCCESS", "发送消息成功"),
    SENDING("SENDING", "发送中"),
    SEND_FAILED("SEND_FAILED", "发送失败"),
    SEND_TIME_OUT("SEND_TIME_OUT", "请求超时"),
    RETURN_SUCESS("RETURN_SUCESS", "返回成功"),
    RETURN_FAILED("RETURN_FAILED", "返回失败"),
    RETURNING("RETURNING", "返回中");

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
