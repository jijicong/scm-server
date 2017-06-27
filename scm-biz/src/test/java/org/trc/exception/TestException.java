package org.trc.exception;

/**
 * Created by hzqph on 2017/6/22.
 */
public class TestException extends RuntimeException{
    /**
     * 错误信息
     */
    private String message;

    public TestException(String message) {
        super(message);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
