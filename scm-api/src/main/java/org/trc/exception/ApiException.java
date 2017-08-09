package org.trc.exception;

import org.trc.enums.ResponseAckEnum;

/**
 * aip服务异常
 */
public class ApiException extends RuntimeException {

    /**
     * 异常枚举
     */
    private ResponseAckEnum responseAckEnum;
    /**
     * 错误信息
     */
    private String message;

    public ApiException(ResponseAckEnum responseAckEnum, String message) {
        super(message);
        this.responseAckEnum = responseAckEnum;
        this.message = message;
    }

    public ResponseAckEnum getResponseAckEnum() {
        return responseAckEnum;
    }

    public void setResponseAckEnum(ResponseAckEnum responseAckEnum) {
        this.responseAckEnum = responseAckEnum;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
