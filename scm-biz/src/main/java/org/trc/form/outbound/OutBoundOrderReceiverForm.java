package org.trc.form.outbound;

import org.hibernate.validator.constraints.Length;

import javax.ws.rs.FormParam;
import javax.ws.rs.PathParam;

public class OutBoundOrderReceiverForm {

    @PathParam("outboundOrderCode")
    @Length(max = 32, message = "发货通知单编号长度不能超过32个")
    private String outboundOrderCode;

    @FormParam("receiverName")
    @Length(max = 128, message = "收货人姓名长度不能超过128个")
    private String receiverName;

    @FormParam("receiverPhone")
    @Length(max = 16, message = "收货人电话号码长度不能超过16个")
    private String receiverPhone;

    @FormParam("receiverProvince")
    @Length(max = 16, message = "收货人所在省长度不能超过16个")
    private String receiverProvince;

    @FormParam("receiverCity")
    @Length(max = 16, message = "收货人所在城市长度不能超过16个")
    private String receiverCity;

    @FormParam("receiverDistrict")
    @Length(max = 16, message = "收货人所在地区长度不能超过16个")
    private String receiverDistrict;

    @FormParam("receiverAddress")
    @Length(max = 256, message = "收货人详细地址长度不能超过256个")
    private String receiverAddress;

    public String getOutboundOrderCode() {
        return outboundOrderCode;
    }

    public void setOutboundOrderCode(String outboundOrderCode) {
        this.outboundOrderCode = outboundOrderCode;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }

    public String getReceiverProvince() {
        return receiverProvince;
    }

    public void setReceiverProvince(String receiverProvince) {
        this.receiverProvince = receiverProvince;
    }

    public String getReceiverCity() {
        return receiverCity;
    }

    public void setReceiverCity(String receiverCity) {
        this.receiverCity = receiverCity;
    }

    public String getReceiverDistrict() {
        return receiverDistrict;
    }

    public void setReceiverDistrict(String receiverDistrict) {
        this.receiverDistrict = receiverDistrict;
    }

    public String getReceiverAddress() {
        return receiverAddress;
    }

    public void setReceiverAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress;
    }
}
