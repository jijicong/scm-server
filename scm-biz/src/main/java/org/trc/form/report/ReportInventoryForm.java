package org.trc.form.report;

import io.swagger.annotations.ApiParam;
import org.hibernate.validator.constraints.NotBlank;

import javax.ws.rs.QueryParam;
import java.io.Serializable;

/**
 * Description〈〉
 *
 * @author hzliuwei
 * @create 2018/9/11
 */
public class ReportInventoryForm implements Serializable {

    private static final long serialVersionUID = 4376634840756965104L;

    @ApiParam(value = "报表类型1.总库存 2.入库明细 3.出库明细", required = true)
    @QueryParam("reportType")
    private String reportType;

    @ApiParam(value = "查询报表日期")
    @QueryParam("date")
    private String date;

    @ApiParam(value = "库存类型1.正品 2.残品", required = true)
    @QueryParam("stockType")
    private String stockType;

    @NotBlank
    @ApiParam(value = "仓库CODE", required = true)
    @QueryParam("warehouseCode")
    private String warehouseCode;

    @QueryParam("startDate")
    @ApiParam(value = "仓库CODE")
    private String startDate;

    @QueryParam("endDate")
    @ApiParam(value = "结束日期")
    private String endDate;

    @QueryParam("skuCode")
    @ApiParam(value = "SKU编号")
    private String skuCode;

    @QueryParam("barCode")
    @ApiParam(value = "条形码")
    private String barCode;

    @QueryParam("skuName")
    @ApiParam(value = "SKU名称")
    private String skuName;

    @QueryParam("outboundOrderCode")
    @ApiParam(value = "出库单编号")
    private String outboundOrderCode;

    @QueryParam("warehouseOutboundOrderCode")
    @ApiParam(value = "仓库反馈出库单编号")
    private String warehouseOutboundOrderCode;

    @QueryParam("sellChannelCode")
    @ApiParam(value = "销售渠道订单号")
    private String sellChannelCode;

    @QueryParam("purchaseOrderCode")
    @ApiParam(value = "入库单编号")
    private String purchaseOrderCode;

    @QueryParam("warehousePurchaseOrderCode")
    @ApiParam(value = "仓库反馈入库单编号")
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

    public String getSkuName() {
        return skuName;
    }

    public void setSkuName(String skuName) {
        this.skuName = skuName;
    }

    public String getOutboundOrderCode() {
        return outboundOrderCode;
    }

    public void setOutboundOrderCode(String outboundOrderCode) {
        this.outboundOrderCode = outboundOrderCode;
    }

    public String getWarehouseOutboundOrderCode() {
        return warehouseOutboundOrderCode;
    }

    public void setWarehouseOutboundOrderCode(String warehouseOutboundOrderCode) {
        this.warehouseOutboundOrderCode = warehouseOutboundOrderCode;
    }

    public String getSellChannelCode() {
        return sellChannelCode;
    }

    public void setSellChannelCode(String sellChannelCode) {
        this.sellChannelCode = sellChannelCode;
    }

    public String getPurchaseOrderCode() {
        return purchaseOrderCode;
    }

    public void setPurchaseOrderCode(String purchaseOrderCode) {
        this.purchaseOrderCode = purchaseOrderCode;
    }

    public String getWarehousePurchaseOrderCode() {
        return warehousePurchaseOrderCode;
    }

    public void setWarehousePurchaseOrderCode(String warehousePurchaseOrderCode) {
        this.warehousePurchaseOrderCode = warehousePurchaseOrderCode;
    }
}
