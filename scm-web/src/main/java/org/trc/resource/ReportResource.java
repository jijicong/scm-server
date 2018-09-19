package org.trc.resource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.trc.biz.report.IReportBiz;
import org.trc.domain.report.ReportInventory;
import org.trc.domain.warehouseInfo.WarehouseInfo;
import org.trc.enums.CommonExceptionEnum;
import org.trc.exception.ParamValidException;
import org.trc.form.report.ReportInventoryForm;
import org.trc.util.Pagenation;
import org.trc.util.ResultUtil;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Description〈报表统计接口〉
 *
 * @author hzliuwei
 * @create 2018/9/10
 */
@Api(value = "报表统计接口")
@Component
@Path("/report")
public class ReportResource {

    @Autowired
    private IReportBiz reportBiz;

    /**
     * 仓库列表
     */
    @GET
    @Path("/warehouseList")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "库存报表首页列表", response = WarehouseInfo.class)
    public Response getWarehouseList(){
        return ResultUtil.createSuccessResult("查询所有仓库", reportBiz.getWarehouseList());
    }

    /**
     * 库存报表首页列表
     */
    @GET
    @Path("/pageList/{date}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "库存报表首页列表", response = ReportInventory.class)
    public Response getPageList(@ApiParam(name = "date", value = "年份", required = true) @PathParam("date") String date){
        return ResultUtil.createSuccessResult("库存报表首页列表", reportBiz.getPageList(date));
    }

    /**
     * 具体类型报表列表
     */
    @GET
    @Path("/reportPageList")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "具体类型报表列表")
    public Response getReportPageList(@BeanParam ReportInventoryForm form, @BeanParam Pagenation page){
        return ResultUtil.createSuccessPageResult(reportBiz.getReportPageList(form, page, true));
    }

    /**
     * 特殊查询报表列表
     */
    @GET
    @Path("/detailPageList")
    @ApiOperation(value = "特殊查询报表列表")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getReportDetailPageList(@BeanParam ReportInventoryForm form, @BeanParam Pagenation page){
        return ResultUtil.createSuccessPageResult(reportBiz.getReportDetailPageList(form, page, true));
    }

    /**
     * 下载具体仓库全部报表
     */
    @GET
    @Path("/downloadAllForWarehouse")
    @ApiOperation(value = "下载具体仓库全部报表")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response downloadAllForWarehouse(@BeanParam ReportInventoryForm form) throws Exception{
        return reportBiz.downloadAllForWarehouse(form);
    }

    /**
     * 下载具体仓库当前报表
     */
    @GET
    @Path("/downloadCurrentForWarehouse")
    @ApiOperation(value = "下载具体仓库当前报表")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response downloadCurrentForWarehouse(@BeanParam ReportInventoryForm form) {
        return reportBiz.downloadCurrentForWarehouse(form, false);
    }

    /**
     * 下载特殊查询报表
     */
    @GET
    @Path("/downloadOtherReport")
    @ApiOperation(value = "下载特殊查询报表")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response downloadOtherReport(@BeanParam ReportInventoryForm form) {
        if (StringUtils.isBlank(form.getDate()) && (StringUtils.isBlank(form.getStartDate()) && StringUtils.isBlank(form.getEndDate()))
                && !StringUtils.isBlank(form.getDate()) && (!StringUtils.isBlank(form.getStartDate()) && !StringUtils.isBlank(form.getEndDate()))) {
            throw new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, "参数校验异常");
        }
        return reportBiz.downloadCurrentForWarehouse(form, true);
    }
}
