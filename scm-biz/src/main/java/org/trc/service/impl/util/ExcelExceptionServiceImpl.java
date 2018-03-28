package org.trc.service.impl.util;

import org.springframework.stereotype.Service;
import org.trc.domain.util.ExcelException;
import org.trc.service.impl.BaseService;
import org.trc.service.util.IExcelExceptionService;

/**
 * Created by hzcyn on 2018/3/28.
 */
@Service("excelExceptionService")
public class ExcelExceptionServiceImpl extends BaseService<ExcelException,Long> implements IExcelExceptionService {
}
