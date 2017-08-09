package org.trc.service.impl.system;

import org.springframework.stereotype.Service;
import org.trc.domain.System.LogisticsCompany;
import org.trc.service.System.ILogisticsCompanyService;
import org.trc.service.impl.BaseService;

/**
 * Created by hzwdx on 2017/8/8.
 */
@Service("logisticsCompanyService")
public class LogisticsCompanyService extends BaseService<LogisticsCompany,Long> implements ILogisticsCompanyService {
}
