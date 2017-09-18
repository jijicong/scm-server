package org.trc.service.impl.trcCategory;

import org.springframework.stereotype.Service;
import org.trc.domain.trcDomain.Properties;
import org.trc.service.impl.BaseService;
import org.trc.service.trcCategory.ITrcPropertiesService;

@Service("trcPropertiesService")
public class TrcPropertiesService extends BaseService<Properties,Integer> implements ITrcPropertiesService {
}
