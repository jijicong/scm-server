package org.trc.dbUnit.order.form;

import java.util.List;

/**
 * Created by hzwdx on 2017/7/3.
 */
public class TrcOrderForm {

    /**
     * 通知流水号
     */
    private String noticeNum;

    /**
     * 接口调用时间戳
     */
    private Long operateTime;
    /**
     * SHA256签名
     */
    private String sign;

    private TrcPlatformOrder platformOrder;

    private List<TrcShopOrderForm> shopOrders;

    public String getNoticeNum() {
        return noticeNum;
    }

    public void setNoticeNum(String noticeNum) {
        this.noticeNum = noticeNum;
    }

    public Long getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Long operateTime) {
        this.operateTime = operateTime;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public TrcPlatformOrder getPlatformOrder() {
        return platformOrder;
    }

    public void setPlatformOrder(TrcPlatformOrder platformOrder) {
        this.platformOrder = platformOrder;
    }

    public List<TrcShopOrderForm> getShopOrders() {
        return shopOrders;
    }

    public void setShopOrders(List<TrcShopOrderForm> shopOrders) {
        this.shopOrders = shopOrders;
    }
}
