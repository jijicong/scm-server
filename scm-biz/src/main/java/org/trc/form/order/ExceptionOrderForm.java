package org.trc.form.order;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hibernate.validator.constraints.Length;
import org.trc.util.QueryModel;

import javax.ws.rs.QueryParam;

/**
 * Created by hzcyn on 2017/11/13.
 */
public class ExceptionOrderForm extends QueryModel{

    @QueryParam("exceptionOrderCode")
    @Length(max = 32, message = "拆单异常单编号长度不能超过32个")
    private String exceptionOrderCode;
    @QueryParam("shopOrderCode")
    @Length(max = 32, message = "店铺订单编码长度不能超过32个")
    private String shopOrderCode;
    @QueryParam("scmShopOrderCode")
    @Length(max = 32, message = "系统订单号长度不能超过32个")
    private String scmShopOrderCode;
    @QueryParam("platformOrderCode")
    @Length(max = 32, message = "平台订单编码长度不能超过32个")
    private String platformOrderCode;
    @QueryParam("exceptionType")
    @Length(max = 2, message = "异常类型长度不能超过2个")
    private String exceptionType;
    @QueryParam("status")
    @Length(max = 2, message = "状态长度不能超过2个")
    private String status;
    @QueryParam("receiverName")
    @Length(max = 50, message = "收货人姓名长度不能超过50个")
    private String receiverName;

    public String getExceptionOrderCode() {
        return exceptionOrderCode;
    }

    public void setExceptionOrderCode(String exceptionOrderCode) {
        this.exceptionOrderCode = exceptionOrderCode;
    }

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

    public String getExceptionType() {
        return exceptionType;
    }

    public void setExceptionType(String exceptionType) {
        this.exceptionType = exceptionType;
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

    public String getScmShopOrderCode() {
        return scmShopOrderCode;
    }

    public void setScmShopOrderCode(String scmShopOrderCode) {
        this.scmShopOrderCode = scmShopOrderCode;
    }

    @Override
    public String toString(){
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
