package org.trc.exception;

import org.trc.enums.ExceptionEnum;

/**
 * 采购单异常
 */
public class PurchaseOrderException extends RuntimeException {

    /**
     * 异常枚举
     */
    private ExceptionEnum exceptionEnum;
    /**
     * 错误信息
     */
    private String message;

    public PurchaseOrderException(ExceptionEnum exceptionEnum, String message) {
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
