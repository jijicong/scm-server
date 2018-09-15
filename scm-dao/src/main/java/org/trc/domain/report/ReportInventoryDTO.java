package org.trc.domain.report;

import java.io.Serializable;

/**
 * Description〈〉
 *
 * @author hzliuwei
 * @create 2018/9/11
 */
public class ReportInventoryDTO implements Serializable {

    private static final long serialVersionUID = 4376634840756965104L;

    private String reportType;

    private String date;


    private String stockType;


    private String warehouseCode;


    private String startDate;


    private String endDate;


    private String skuCode;


    private String barCode;


    private String warehousePurchaseOrderCode;

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStockType() {
        return stockType;
    }

    public void setStockType(String stockType) {
        this.stockType = stockType;
    }

    public String getWarehouseCode() {
        return warehouseCode;
    }

    public void setWarehouseCode(String warehouseCode) {
        this.warehouseCode = warehouseCode;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public String getWarehousePurchaseOrderCode() {
        return warehousePurchaseOrderCode;
    }

    public void setWarehousePurchaseOrderCode(String warehousePurchaseOrderCode) {
        this.warehousePurchaseOrderCode = warehousePurchaseOrderCode;
    }
}
