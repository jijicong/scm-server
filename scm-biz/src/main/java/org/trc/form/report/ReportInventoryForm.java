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

    @NotBlank
    @ApiParam(value = "报表类型1.总库存 2.入库明细 3.出库明细", required = true)
    @QueryParam("reportType")
    private String reportType;

    @NotBlank
    @ApiParam(value = "查询报表日期(yyyy-MM-dd)", required = true)
    @QueryParam("date")
    private String date;

    @NotBlank
    @ApiParam(value = "库存类型1.正品 2.残品", required = true)
    @QueryParam("stockType")
    private String stockType;

    @NotBlank
    @ApiParam(value = "仓库CODE", required = true)
    @QueryParam("warehouseCode")
    private String warehouseCode;

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
}
