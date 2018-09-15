package org.trc.biz.report;

import org.trc.domain.report.ReportInventory;
import org.trc.form.report.ReportInventoryForm;
import org.trc.util.Pagenation;

import javax.ws.rs.core.Response;
import java.util.List;

public interface IReportBiz {

    /**
     * 库存报表首页列表
     *
     * @param date 年份
     * @return
     */
    List<List<ReportInventory>> getPageList(String date);

    /**
     * 具体类型报表列表
     *
     * @param form
     * @param page
     * @param b    是否分页
     * @return
     */
    Object getReportPageList(ReportInventoryForm form, Pagenation page, boolean b);

    /**
     * 下载具体仓库全部报表
     *
     * @return
     */
    Response downloadAllForWarehouse(ReportInventoryForm form);

    /**
     * 下载具体仓库全部报表
     *
     * @return
     */
    Response downloadCurrentForWarehouse(ReportInventoryForm form);

    /**
     * 特殊查询报表列表
     *
     * @param form
     * @param page
     * @param b    是否分页
     * @return
     */
    Object getReportDetailPageList(ReportInventoryForm form, Pagenation page, boolean b);
}
