package org.trc.mapper.report;


import org.apache.ibatis.annotations.Param;
import org.trc.domain.report.ReportInventory;
import org.trc.util.BaseMapper;

import java.util.List;

public interface IReportInventoryMapper extends BaseMapper<ReportInventory> {

    List<ReportInventory> selectPageList(@Param("time") String time);
}