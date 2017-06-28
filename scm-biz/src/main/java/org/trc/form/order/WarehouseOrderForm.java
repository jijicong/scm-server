package org.trc.form.order;

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

    //供应商订单编号
    @QueryParam("warehouseOrderCode")
    @Length(max = 32, message = "供应商订单编号长度不能超过32个")
    private String warehouseOrderCode;

    //供应商名称
    @QueryParam("supplierName")
    @Length(max = 64, message = "供应商名称长度不能超过64个")
    private String supplierName;

    //店铺订单号
    @QueryParam("shopOrderCode")
    @Length(max = 32, message = "店铺订单号长度不能超过32个")
    private String shopOrderCode;

    //状态
    @QueryParam("status")
    private String status;

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

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getShopOrderCode() {
        return shopOrderCode;
    }

    public void setShopOrderCode(String shopOrderCode) {
        this.shopOrderCode = shopOrderCode;
    }
}
