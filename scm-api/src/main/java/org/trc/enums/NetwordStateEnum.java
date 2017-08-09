package org.trc.enums;

/**
 * Created by hzwyz on 2017/8/9 0009.
 */
public enum NetwordStateEnum {
    SUCCESS("SEND_SUCCESS", "发送消息成功"),
    FAILED("SEND_FAILED", "发送失败"),
    SOCKET_TIME_OUT("SOCKET_TIME_OUT", "请求超时"),
    INITIAL("INITIAL","初始化"),
    ERROR("ERROR","发送异常");

    private String code;

    private String description;


    NetwordStateEnum(String code, String description) {
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
