package org.trc.service.impl.supplier;

import org.springframework.stereotype.Service;
import org.trc.domain.supplier.AuditLog;
import org.trc.service.impl.BaseService;
import org.trc.service.supplier.IAuditLogService;
import org.trc.util.BaseMapper;

/**
 * Created by hzqph on 2017/5/16.
 */
@Service("auditLogService ")
public class AuditLogService extends BaseService<AuditLog,Long> implements IAuditLogService {

}
