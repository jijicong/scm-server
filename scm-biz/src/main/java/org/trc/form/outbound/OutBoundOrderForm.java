package org.trc.form.outbound;

import org.hibernate.validator.constraints.Length;
import org.trc.util.QueryModel;

import javax.ws.rs.QueryParam;
import java.io.Serializable;

public class OutBoundOrderForm extends QueryModel {

    //发货通知单编号
    @QueryParam("outboundOrderCode")
    @Length(max = 32, message = "发货通知单编号长度不能超过32个")
    private String outboundOrderCode;

    //开放平台出库单号
    @QueryParam("wmsOrderCode")
    @Length(max = 32, message = "开放平台出库单号长度不能超过32个")
    private String wmsOrderCode;

    //店铺订单编号
    @QueryParam("shopOrderCode")
    @Length(max = 32, message = "店铺订单编号长度不能超过32个")
    private String shopOrderCode;

    //发货仓库id
    @QueryParam("warehouseId")
    private String warehouseId;

    //状态
    @QueryParam("status")
    @Length(max = 2, message = "状态长度不能超过2")
    private String status;

    //收货人
    @QueryParam("receiverName")
    @Length(max = 128, message = "收货人姓名长度不能超过128")
    private String receiverName;

    //付款起始日期
    @QueryParam("startPayDate")
    @Length(max = 10, message = "付款起始日期长度不能超过10个")
    protected String startPayDate;//开始日期，格式：yyyy-mm-dd

    //付款截止日期
    @QueryParam("endPayDate")
    @Length(max = 10, message = "付款截止日期长度不能超过10个")
    protected String endPayDate;//截止日期，格式：yyyy-mm-dd

    //平台订单编号
    @QueryParam("platformOrderCode")
    @Length(max = 32, message = "平台订单编号长度不能超过32个")
    private String platformOrderCode;

    //发货单创建起始日期
    @QueryParam("startCreateDate")
    @Length(max = 10, message = "发货单创建起始日期长度不能超过10个")
    protected String startCreateDate;//开始日期，格式：yyyy-mm-dd

    //发货单创建截止日期
    @QueryParam("endCreateDate")
    @Length(max = 10, message = "发货单创建截止日期长度不能超过10个")
    protected String endCreateDate;//截止日期，格式：yyyy-mm-dd

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

    public String getStartPayDate() {
        return startPayDate;
    }

    public void setStartPayDate(String startPayDate) {
        this.startPayDate = startPayDate;
    }

    public String getEndPayDate() {
        return endPayDate;
    }

    public void setEndPayDate(String endPayDate) {
        this.endPayDate = endPayDate;
    }

    public String getPlatformOrderCode() {
        return platformOrderCode;
    }

    public void setPlatformOrderCode(String platformOrderCode) {
        this.platformOrderCode = platformOrderCode;
    }

    public String getStartCreateDate() {
        return startCreateDate;
    }

    public void setStartCreateDate(String startCreateDate) {
        this.startCreateDate = startCreateDate;
    }

    public String getEndCreateDate() {
        return endCreateDate;
    }

    public void setEndCreateDate(String endCreateDate) {
        this.endCreateDate = endCreateDate;
    }

    public String getWmsOrderCode() {
        return wmsOrderCode;
    }

    public void setWmsOrderCode(String wmsOrderCode) {
        this.wmsOrderCode = wmsOrderCode;
    }
}
