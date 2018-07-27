package org.trc.exception;

import org.trc.enums.ExceptionEnum;

/**
 * Created by hzgjl on 2018/7/27.
 */
public class ItemGroupException extends RuntimeException {
    /**
     * 异常枚举
     */
    private ExceptionEnum exceptionEnum;
    /**
     * 错误信息
     */
    private String message;

    public ItemGroupException(ExceptionEnum exceptionEnum,String message){
        super(message);
        this.exceptionEnum=exceptionEnum;
        this.message=message;
    }

    public ExceptionEnum getExceptionEnum() {
        return exceptionEnum;
    }

    public void setExceptionEnum(ExceptionEnum exceptionEnum) {
        this.exceptionEnum = exceptionEnum;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
