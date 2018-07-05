package org.trc.form.order;

import java.io.Serializable;

/**
 * Created by hzcyn on 2017/11/20.
 */
public class ImportOrderResult implements Serializable {

    /**
     * 订单号
     */
    private String orderCode;
    /**
     * 导入成功数
     */
    private Integer successCount;
    /**
     * 导入失败数
     */
    private Integer failCount;

    public ImportOrderResult(){
    }

    public ImportOrderResult(String orderCode, Integer successCount, Integer failCount) {
        this.orderCode = orderCode;
        this.successCount = successCount;
        this.failCount = failCount;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public Integer getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(Integer successCount) {
        this.successCount = successCount;
    }

    public Integer getFailCount() {
        return failCount;
    }

    public void setFailCount(Integer failCount) {
        this.failCount = failCount;
    }
}
