package org.trc.util;

import org.trc.enums.ExceptionEnum;

/**
 * Created by george on 2017/2/28.
 */
public class ResponseAck<T> {

    //成功状态码
    public final static String SUCCESS_CODE = "200";

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

    /**
     * 构造异常返回结果
     * @param exceptionEnum
     * @param data
     */
    public ResponseAck(ExceptionEnum exceptionEnum, T data){
        this.code = exceptionEnum.getCode();
        this.message = exceptionEnum.getMessage();
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
