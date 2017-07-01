package org.trc.domain.order;

import org.trc.domain.util.ScmDO;

import java.util.Date;

public class SupplierOrderLogistics extends ScmDO{

    private Long id;

    private String warehouseOrderCode;

    private String supplierOrderCode;

    private String supplierSubOrderCode;

    private String supplierCode;

    private Date consignTime;

    private String logisticsCorporation;

    private String waybillNumber;

    private String logisticsInfo;

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

    public String getSupplierSubOrderCode() {
        return supplierSubOrderCode;
    }

    public void setSupplierSubOrderCode(String supplierSubOrderCode) {
        this.supplierSubOrderCode = supplierSubOrderCode == null ? null : supplierSubOrderCode.trim();
    }

    public String getSupplierCode() {
        return supplierCode;
    }

    public void setSupplierCode(String supplierCode) {
        this.supplierCode = supplierCode == null ? null : supplierCode.trim();
    }

    public Date getConsignTime() {
        return consignTime;
    }

    public void setConsignTime(Date consignTime) {
        this.consignTime = consignTime;
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
}