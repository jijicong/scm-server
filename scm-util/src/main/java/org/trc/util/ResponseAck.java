package org.trc.util;

import org.trc.enums.ResponseAckEnum;

/**
 * Created by george on 2017/2/28.
 */
public class ResponseAck<T> {

    private String code;

    private String message;

    private T data;

    public ResponseAck(){

    }

    public ResponseAck(String code, String message, T data){
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public ResponseAck(ResponseAckEnum responseAckEnum, T data){
        this.code = responseAckEnum.getCode();
        this.message = responseAckEnum.getMessage();
        this.data = data;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
