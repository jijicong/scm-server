package org.trc.domain.report;

import lombok.Getter;
import lombok.Setter;

import java.io.ByteArrayOutputStream;


/**
 * Created by hzcyn on 2018/9/12.
 */
@Getter
@Setter
public class ReportExcelDetail {

    private ByteArrayOutputStream stream;
    private String fileName;
}
