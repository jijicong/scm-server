package org.trc.exception;

/**
 * Created by hzwdx on 2017/4/22.
 */

import org.trc.enums.CommonExceptionEnum;

/**
 * 数据处理异常
 */
public class DataHandlerException extends RuntimeException{
    /**
     * 异常枚举
     */
    private CommonExceptionEnum exceptionEnum;
    /**
     */
    private String message;

    public DataHandlerException(CommonExceptionEnum exceptionEnum, String message){
        super(message);
        this.exceptionEnum = exceptionEnum;
        this.message = message;
    }

    public CommonExceptionEnum getExceptionEnum() {
        return exceptionEnum;
    }

    public void setExceptionEnum(CommonExceptionEnum exceptionEnum) {
        this.exceptionEnum = exceptionEnum;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
