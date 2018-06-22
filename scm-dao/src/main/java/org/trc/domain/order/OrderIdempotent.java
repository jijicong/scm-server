package org.trc.domain.order;

import javax.persistence.Table;
import java.io.Serializable;

@Table(name = "order_idempotent")
public class OrderIdempotent implements Serializable {

    private String channelCode;

    private String sellCode;

    private String ShopOrderCode;

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
}
