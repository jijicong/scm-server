package org.trc.exception;

/**
 * Created by hzwdx on 2017/4/22.
 */
import org.trc.enums.ExceptionEnum;

/**
 * 数据处理异常
 */
public class DataHandlerException extends RuntimeException{
    /**
     * 异常枚举
     */
    private ExceptionEnum exceptionEnum;
    /**
     * 错误信息
     */
    private String message;

    public DataHandlerException(ExceptionEnum exceptionEnum, String message){
        super(message);
        this.exceptionEnum = exceptionEnum;
        this.message = message;
    }

    public ExceptionEnum getExceptionEnum() {
        return exceptionEnum;
    }

    public void setExceptionEnum(ExceptionEnum exceptionEnum) {
        this.exceptionEnum = exceptionEnum;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
