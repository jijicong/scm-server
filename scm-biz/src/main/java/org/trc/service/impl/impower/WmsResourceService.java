package org.trc.service.impl.impower;

import org.springframework.stereotype.Service;
import org.trc.domain.impower.WmsResource;
import org.trc.service.impl.BaseService;
import org.trc.service.impower.IWmsResourceService;

@Service("wmsResourceService")
public class WmsResourceService extends BaseService<WmsResource, Long> implements IWmsResourceService {
}
