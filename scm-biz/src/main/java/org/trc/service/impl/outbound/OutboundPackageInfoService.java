package org.trc.service.impl.outbound;

import org.springframework.stereotype.Service;
import org.trc.domain.order.OutboundPackageInfo;
import org.trc.service.impl.BaseService;
import org.trc.service.outbound.IOutboundPackageInfoService;

/**
 * Created by hzcyn on 2018/4/12.
 */
@Service("outboundPackageInfoService")
public class OutboundPackageInfoService extends BaseService<OutboundPackageInfo, Long> implements IOutboundPackageInfoService {
}
