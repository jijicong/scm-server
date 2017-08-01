package org.trc.form.JDModel;

import org.trc.util.QueryModel;

import javax.ws.rs.QueryParam;

/**
 * Created by hzwyz on 2017/8/1 0001.
 */
public class BalanceDetailDO extends QueryModel {
    @QueryParam("orderId")
    private String orderId;

    @QueryParam("tradeType")
    private String tradeType;

    @QueryParam("tradeNo")
    private String tradeNo;

    @QueryParam("startUpdateTime")
    private String startUpdateTime;

    @QueryParam("endUpdateTime")
    private String endUpdateTime;

    @QueryParam("pageIds")
    private String pageIds;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getTradeType() {
        return tradeType;
    }

    public void setTradeType(String tradeType) {
        this.tradeType = tradeType;
    }

    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
    }

    public String getStartUpdateTime() {
        return startUpdateTime;
    }

    public void setStartUpdateTime(String startUpdateTime) {
        this.startUpdateTime = startUpdateTime;
    }

    public String getEndUpdateTime() {
        return endUpdateTime;
    }

    public void setEndUpdateTime(String endUpdateTime) {
        this.endUpdateTime = endUpdateTime;
    }

    public String getPageIds() {
        return pageIds;
    }

    public void setPageIds(String pageIds) {
        this.pageIds = pageIds;
    }
}
