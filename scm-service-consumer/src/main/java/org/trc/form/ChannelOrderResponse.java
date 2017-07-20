package org.trc.form;

import java.util.List;

/**
 * Created by hzwdx on 2017/7/7.
 */
public class ChannelOrderResponse extends TrcParam{

    //平台订单编号
    private String platformOrderCode;
    //店铺订单编号
    private String shopOrderCode;
    //订单类型:0-京东订单,1-其他订单
    private String orderType;
    //订单信息
    private List<SupplierOrderReturn> order;

    public String getPlatformOrderCode() {
        return platformOrderCode;
    }

    public void setPlatformOrderCode(String platformOrderCode) {
        this.platformOrderCode = platformOrderCode;
    }

    public String getShopOrderCode() {
        return shopOrderCode;
    }

    public void setShopOrderCode(String shopOrderCode) {
        this.shopOrderCode = shopOrderCode;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public List<SupplierOrderReturn> getOrder() {
        return order;
    }

    public void setOrder(List<SupplierOrderReturn> order) {
        this.order = order;
    }
}
