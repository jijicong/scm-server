package org.trc.form.liangyou;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by wangyz on 2017/11/13.
 */
public class LyStatement {
    //商品sku编号
    private String skuCode;
    //粮油商品sku编号
    private String supplierSkuCode;
    //粮油商品名称
    private String itemName;
    //交易数量
    private Integer num;
    //平台订单号
    private String platformOrderCode;
    //店铺订单号
    private String shopOrderCode;
    //粮油订单号
    private String supplierOrderCode;
    //买家实付商品金额
    private BigDecimal payment;
    //系统发送粮油时间
    private Date createTime;

    public String getSupplierSkuCode() {
        return supplierSkuCode;
    }

    public void setSupplierSkuCode(String supplierSkuCode) {
        this.supplierSkuCode = supplierSkuCode;
    }

    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

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

    public String getSupplierOrderCode() {
        return supplierOrderCode;
    }

    public void setSupplierOrderCode(String supplierOrderCode) {
        this.supplierOrderCode = supplierOrderCode;
    }

    public BigDecimal getPayment() {
        return payment;
    }

    public void setPayment(BigDecimal payment) {
        this.payment = payment;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
