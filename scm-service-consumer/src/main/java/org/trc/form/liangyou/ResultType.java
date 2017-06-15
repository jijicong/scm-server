package org.trc.form.liangyou;

/**
 * Created by hzwyz on 2017/6/13 0013.
 */
public class ResultType<T> {
    private int code;

    private String message;

    private T data;

    public ResultType(){}

    public ResultType(int code,String message,T data){
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
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
