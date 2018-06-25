package org.trc.domain.order;

import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Table(name = "order_idempotent")
public class OrderIdempotent implements Serializable {

    private String channelCode;

    private String sellCode;

    private String ShopOrderCode;

    private Date createTime;

    public String getChannelCode() {
        return channelCode;
    }

    public void setChannelCode(String channelCode) {
        this.channelCode = channelCode;
    }

    public String getSellCode() {
        return sellCode;
    }

    public void setSellCode(String sellCode) {
        this.sellCode = sellCode;
    }

    public String getShopOrderCode() {
        return ShopOrderCode;
    }

    public void setShopOrderCode(String shopOrderCode) {
        ShopOrderCode = shopOrderCode;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
