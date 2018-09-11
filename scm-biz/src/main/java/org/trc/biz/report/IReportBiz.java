package org.trc.biz.report;

import org.trc.domain.report.ReportInventory;
import org.trc.form.report.ReportInventoryForm;
import org.trc.util.Pagenation;

import java.util.List;

public interface IReportBiz {

    /**
     * 每日统计前一天明细报表数据
     */
    void statisticalDetailReport();

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
     * @return
     */
    Object getReportPageList(ReportInventoryForm form, Pagenation<ReportInventory> page);
}
