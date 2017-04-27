package org.trc.exception;


import org.trc.enums.CommonExceptionEnum;

/**
 * 参数校验异常
 */
public class ParamValidException extends RuntimeException{

    /**
     * 异常枚举
     */
    private CommonExceptionEnum exceptionEnum;
    /**
     * 错误信息
     */
    private String message;

    public ParamValidException(CommonExceptionEnum exceptionEnum, String message){
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
