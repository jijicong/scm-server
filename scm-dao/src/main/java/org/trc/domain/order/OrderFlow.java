package org.trc.domain.order;

import java.io.Serializable;

/**
 * Created by ding on 2017/6/22.
 */
public class OrderFlow  implements Serializable {

    // 平台订单编码
    private String platformOrderCode;

    // 店铺订单编码
    private String shopOrderCode;

    //业务类型
    private String type;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
