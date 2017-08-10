package org.trc.service.impl.config;

import org.springframework.stereotype.Service;
import org.trc.domain.config.RetryConfig;
import org.trc.service.config.IRetryConfigService;
import org.trc.service.impl.BaseService;

/**
 * Created by hzdzf on 2017/6/7.
 */
@Service("retryConfigService")
public class RetryConfigService extends BaseService<RetryConfig,Long> implements IRetryConfigService {
}
