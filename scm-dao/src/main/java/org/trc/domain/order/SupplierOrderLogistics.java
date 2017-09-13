package org.trc.domain.order;

import org.trc.domain.util.ScmDO;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

public class SupplierOrderLogistics extends ScmDO{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;

    private String warehouseOrderCode;

    private String supplierOrderCode;

    private String supplierParentOrderCode;

    private String supplierCode;
    /**
     *物流公司,当字段type=0-物流信息时不为空
     */
    private String logisticsCorporation;
    /**
     *运单号,当字段type=0-物流信息时不为空
     */
    private String waybillNumber;
    /**
     * 供应商订单sku信息,JSONArray字符串
     */
    private String skus;
    /**
     *物流信息,当字段type=1-配送信息时不为空
     */
    private String logisticsInfo;
    /**
     * 信息类型:0-物流单号,1-配送信息
     */
    private String type;

    private String logisticsStatus;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWarehouseOrderCode() {
        return warehouseOrderCode;
    }

    public void setWarehouseOrderCode(String warehouseOrderCode) {
        this.warehouseOrderCode = warehouseOrderCode == null ? null : warehouseOrderCode.trim();
    }

    public String getSupplierOrderCode() {
        return supplierOrderCode;
    }

    public void setSupplierOrderCode(String supplierOrderCode) {
        this.supplierOrderCode = supplierOrderCode == null ? null : supplierOrderCode.trim();
    }

    public String getSupplierParentOrderCode() {
        return supplierParentOrderCode;
    }

    public void setSupplierParentOrderCode(String supplierParentOrderCode) {
        this.supplierParentOrderCode = supplierParentOrderCode;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSupplierCode() {
        return supplierCode;
    }

    public void setSupplierCode(String supplierCode) {
        this.supplierCode = supplierCode == null ? null : supplierCode.trim();
    }

    public String getLogisticsCorporation() {
        return logisticsCorporation;
    }

    public void setLogisticsCorporation(String logisticsCorporation) {
        this.logisticsCorporation = logisticsCorporation == null ? null : logisticsCorporation.trim();
    }

    public String getWaybillNumber() {
        return waybillNumber;
    }

    public void setWaybillNumber(String waybillNumber) {
        this.waybillNumber = waybillNumber == null ? null : waybillNumber.trim();
    }

    public String getLogisticsInfo() {
        return logisticsInfo;
    }

    public void setLogisticsInfo(String logisticsInfo) {
        this.logisticsInfo = logisticsInfo == null ? null : logisticsInfo.trim();
    }

    public String getLogisticsStatus() {
        return logisticsStatus;
    }

    public void setLogisticsStatus(String logisticsStatus) {
        this.logisticsStatus = logisticsStatus;
    }

    public String getSkus() {
        return skus;
    }

    public void setSkus(String skus) {
        this.skus = skus;
    }
}