package org.trc.service.impl.config;

import org.springframework.stereotype.Service;
import org.trc.domain.config.RequestFlow;
import org.trc.service.config.IRequestFlowService;
import org.trc.service.impl.BaseService;

/**
 * Created by hzdzf on 2017/6/7.
 */
@Service("requestFlowService")
public class RequestFlowService extends BaseService<RequestFlow,Long> implements IRequestFlowService {
}
