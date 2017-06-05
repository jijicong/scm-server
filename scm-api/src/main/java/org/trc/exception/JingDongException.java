package org.trc.exception;

/**
 * Created by hzwyz on 2017/6/3 0003.
 */

import org.trc.enums.JingDongEnum;

/**
 * 京东异常类
 */
public class JingDongException extends RuntimeException {
    /**
     * 异常枚举
     */
    private JingDongEnum jingDongEnum;
    /**
     * 错误信息
     */
    private String message;

    public JingDongException(JingDongEnum jingDongEnum, String message) {
        super(message);
        this.jingDongEnum = jingDongEnum;
        this.message = message;
    }

    public JingDongEnum getJingDongEnum() {
        return jingDongEnum;
    }

    public void setJingDongEnum(JingDongEnum jingDongEnum) {
        this.jingDongEnum = jingDongEnum;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
    
}
