package org.trc.domain.order;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.trc.custom.CustomDateSerializer;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by hzcyn on 2017/11/14.
 */
public class ExceptionOrderItem implements Serializable {

    // 主键
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //拆单异常单编号
    private String exceptionOrderCode;
    //店铺订单编码
    private String shopOrderCode;
    //平台订单编码
    private String platformOrderCode;
    //仓库订单编号
    private String warehouseOrderCode;
    //sku编码
    private  String skuCode;
    //商品名称
    private  String itemName;
    //状态:1-等待中,2-已了结
    private Integer status;
    //商品类型: 1-自采,2-代发
    private Integer itemType;
    //供应商编号
    private String supplierCode;
    //供应商名称
    private String supplierName;
    //异常原因
    private String exceptionReason;
    //应发货数量
    private Integer itemNum;
    //异常数量
    private Integer exceptionNum;
    //创建时间,格式yyyy-mm-dd hh:mi:ss
    @JsonSerialize(using = CustomDateSerializer.class)
    private Date createTime;
    // 更新时间
    private Date updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getWarehouseOrderCode() {
        return warehouseOrderCode;
    }

    public void setWarehouseOrderCode(String warehouseOrderCode) {
        this.warehouseOrderCode = warehouseOrderCode;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getItemType() {
        return itemType;
    }

    public void setItemType(Integer itemType) {
        this.itemType = itemType;
    }

    public String getSupplierCode() {
        return supplierCode;
    }

    public void setSupplierCode(String supplierCode) {
        this.supplierCode = supplierCode;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getExceptionReason() {
        return exceptionReason;
    }

    public void setExceptionReason(String exceptionReason) {
        this.exceptionReason = exceptionReason;
    }

    public Integer getItemNum() {
        return itemNum;
    }

    public void setItemNum(Integer itemNum) {
        this.itemNum = itemNum;
    }

    public Integer getExceptionNum() {
        return exceptionNum;
    }

    public void setExceptionNum(Integer exceptionNum) {
        this.exceptionNum = exceptionNum;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "ExceptionOrderItem{" +
                "id=" + id +
                ", exceptionOrderCode='" + exceptionOrderCode + '\'' +
                ", shopOrderCode='" + shopOrderCode + '\'' +
                ", platformOrderCode='" + platformOrderCode + '\'' +
                ", warehouseOrderCode='" + warehouseOrderCode + '\'' +
                ", skuCode='" + skuCode + '\'' +
                ", itemName='" + itemName + '\'' +
                ", status=" + status +
                ", itemType=" + itemType +
                ", supplierCode='" + supplierCode + '\'' +
                ", supplierName='" + supplierName + '\'' +
                ", exceptionReason='" + exceptionReason + '\'' +
                ", itemNum=" + itemNum +
                ", exceptionNum=" + exceptionNum +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
