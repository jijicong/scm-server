package org.trc.service.impl.config;

import org.springframework.stereotype.Service;
import org.trc.domain.config.SystemConfig;
import org.trc.service.config.ISystemConfigService;
import org.trc.service.impl.BaseService;

/**
 * Created by hzwdx on 2017/8/16.
 */
@Service("systemConfigService")
public class SystemConfigService extends BaseService<SystemConfig,Long> implements ISystemConfigService{
}
