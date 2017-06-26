package org.trc.form.order;

import org.hibernate.validator.constraints.Length;
import org.trc.util.QueryModel;
import javax.ws.rs.QueryParam;

/**
 * Created by hzwdx on 2017/6/26.
 */
public class ShopOrderForm extends QueryModel{

    // 店铺订单编码
    @QueryParam("shopOrderCode")
    @Length(max = 32, message = "店铺订单编码长度不能超过32个")
    private String shopOrderCode;

    // 平台订单编码
    @QueryParam("platformOrderCode")
    @Length(max = 32, message = "平台订单编码长度不能超过32个")
    private String platformOrderCode;

    // 订单状态
    @QueryParam("status")
    @Length(max = 32, message = "订单状态长度不能超过32个")
    private String status;

    // 收货人姓名
    @QueryParam("receiverName")
    @Length(max = 128, message = "收货人姓名长度不能超过128个")
    private String receiverName;

    // 订单类型：0-普通订单 1-零元购 2-分期购 3-拼团
    @QueryParam("type")
    private Byte type;

    public String getShopOrderCode() {
        return shopOrderCode;
    }

    public void setShopOrderCode(String shopOrderCode) {
        this.shopOrderCode = shopOrderCode;
    }

    public String getPlatformOrderCode() {
        return platformOrderCode;
    }

    public void setPlatformOrderCode(String platformOrderCode) {
        this.platformOrderCode = platformOrderCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public Byte getType() {
        return type;
    }

    public void setType(Byte type) {
        this.type = type;
    }
}
