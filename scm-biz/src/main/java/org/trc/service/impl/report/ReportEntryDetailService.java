package org.trc.service.impl.report;

import org.springframework.stereotype.Service;
import org.trc.domain.report.ReportEntryDetail;
import org.trc.service.impl.BaseService;
import org.trc.service.report.IReportEntryDetailService;

/**
 * Description〈入库详情报表〉
 *
 * @author hzliuwei
 * @create 2018/9/5
 */
@Service("reportEntryDetailService")
public class ReportEntryDetailService extends BaseService<ReportEntryDetail, Long> implements IReportEntryDetailService {
}
