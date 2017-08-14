package org.trc.form.outbound;

import org.hibernate.validator.constraints.Length;
import org.trc.util.QueryModel;

import javax.ws.rs.QueryParam;

public class OutBoundOrderForm extends QueryModel {

    //发货通知单编号
    @QueryParam("outboundOrderCode")
    @Length(max = 32, message = "发货通知单编号长度不能超过2个")
    private String outboundOrderCode;

    //发货通知单编号
    @QueryParam("shopOrderCode")
    @Length(max = 32, message = "发货通知单编号长度不能超过2个")
    private String shopOrderCode;

    //发货仓库code
    @QueryParam("warehouseId")
    private String warehouseId;

    //状态
    @QueryParam("status")
    @Length(max = 4, message = "状态长度不能超过4")
    private String status;

    //收货人
    @QueryParam("receiverName")
    @Length(max = 128, message = "收货人姓名长度不能超过128")
    private String receiverName;

    public String getOutboundOrderCode() {
        return outboundOrderCode;
    }

    public void setOutboundOrderCode(String outboundOrderCode) {
        this.outboundOrderCode = outboundOrderCode;
    }

    public String getShopOrderCode() {
        return shopOrderCode;
    }

    public void setShopOrderCode(String shopOrderCode) {
        this.shopOrderCode = shopOrderCode;
    }

    public String getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(String warehouseId) {
        this.warehouseId = warehouseId;
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
}
