package org.trc.form.external;

import org.trc.util.QueryModel;

import javax.ws.rs.QueryParam;

/**
 * Created by hzwyz on 2017/9/21 0021.
 */
public class OrderDetailForm extends QueryModel {

    @QueryParam("channelPlatformOrder")
    private String channelPlatformOrder;

    @QueryParam("orderCode")
    private String orderCode;

    @QueryParam("errMsg")
    private String errMsg;

    @QueryParam("itemSkuCode")
    private String itemSkuCode;

    @QueryParam("itemSkuName")
    private String itemSkuName;

    @QueryParam("startUpdateTime")
    private String startUpdateTime;

    @QueryParam("endUpdateTime")
    private String endUpdateTime;

    @QueryParam("pageIds")
    private String pageIds;

    public String getChannelPlatformOrder() {
        return channelPlatformOrder;
    }

    public void setChannelPlatformOrder(String channelPlatformOrder) {
        this.channelPlatformOrder = channelPlatformOrder;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public String getItemSkuCode() {
        return itemSkuCode;
    }

    public void setItemSkuCode(String itemSkuCode) {
        this.itemSkuCode = itemSkuCode;
    }

    public String getItemSkuName() {
        return itemSkuName;
    }

    public void setItemSkuName(String itemSkuName) {
        this.itemSkuName = itemSkuName;
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
