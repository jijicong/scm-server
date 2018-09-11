package org.trc.resource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.trc.biz.report.IReportBiz;
import org.trc.domain.report.ReportInventory;
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
     * 库存报表首页列表
     */
    @GET
    @Path("/pageList/{date}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPageList(@ApiParam(name = "date", value = "年份", required = true) @PathParam("date") String date){
        return ResultUtil.createSuccessResult("库存报表首页列表", reportBiz.getPageList(date));
    }

    /**
     * 具体类型报表列表
     */
    @GET
    @Path("/reportPageList")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getReportPageList(@BeanParam ReportInventoryForm form, @BeanParam Pagenation<ReportInventory> page){
        return ResultUtil.createSuccessPageResult(reportBiz.getReportPageList(form, page));
    }

    /**
     * 特殊查询报表列表
     */
    @GET
    @Path("/detailedPageList")
    @Produces(MediaType.APPLICATION_JSON)
    public void getReportDetailedPageList(){

    }
}
