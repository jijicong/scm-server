package org.trc.domain.report;

import lombok.Getter;
import lombok.Setter;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

/**
 * Created by hzcyn on 2018/9/12.
 */
@Getter
@Setter
public class ReportExcelDetail {

    private HSSFWorkbook sheet;
    private String fileName;
}
