package org.trc.form.order;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.validator.constraints.Length;
import org.trc.util.QueryModel;

import javax.ws.rs.QueryParam;

/**
 * Created by hzwdx on 2017/6/28.
 */
public class WarehouseOrderForm extends QueryModel{

    //订单类型
    @QueryParam("orderType")
    @Length(max = 2, message = "订单类型长度不能超过2个")
    private String orderType;


    //平台订单编号
    @QueryParam("platformOrderCode")
    @Length(max = 32, message = "平台订单编号长度不能超过32个")
    private String platformOrderCode;

    @QueryParam("scmShopOrderCode")
    @Length(max = 32, message = "系统订单号长度不能超过32个")
    private String scmShopOrderCode;

    //销售渠道编码
    @QueryParam("sellCode")
    @Length(max = 32, message = "销售渠道编码长度不能超过32个")
    private String sellCode;

    //供应商订单编号
    @QueryParam("warehouseOrderCode")
    @Length(max = 32, message = "供应商订单编号长度不能超过32个")
    private String warehouseOrderCode;

    //供应商名称
    @QueryParam("supplierCode")
    @Length(max = 64, message = "供应商名称长度不能超过64个")
    private String supplierCode;

    //店铺订单号
    @QueryParam("shopOrderCode")
    @Length(max = 32, message = "店铺订单号长度不能超过32个")
    private String shopOrderCode;

    //状态
    @QueryParam("status")
    private String status;

    //供应商订单状态
    @QueryParam("supplierOrderStatus")
    private String supplierOrderStatus;

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getWarehouseOrderCode() {
        return warehouseOrderCode;
    }

    public void setWarehouseOrderCode(String warehouseOrderCode) {
        this.warehouseOrderCode = warehouseOrderCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSupplierCode() {
        return supplierCode;
    }

    public void setSupplierCode(String supplierCode) {
        this.supplierCode = supplierCode;
    }

    public String getShopOrderCode() {
        return shopOrderCode;
    }

    public void setShopOrderCode(String shopOrderCode) {
        this.shopOrderCode = shopOrderCode;
    }

    public String getSupplierOrderStatus() {
        return supplierOrderStatus;
    }

    public void setSupplierOrderStatus(String supplierOrderStatus) {
        this.supplierOrderStatus = supplierOrderStatus;
    }

    public String getPlatformOrderCode() {
        return platformOrderCode;
    }

    public void setPlatformOrderCode(String platformOrderCode) {
        this.platformOrderCode = platformOrderCode;
    }

    public String getScmShopOrderCode() {
        return scmShopOrderCode;
    }

    public void setScmShopOrderCode(String scmShopOrderCode) {
        this.scmShopOrderCode = scmShopOrderCode;
    }

    public String getSellCode() {
        return sellCode;
    }

    public void setSellCode(String sellCode) {
        this.sellCode = sellCode;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
