package org.trc.service.impl.outbound;

import org.springframework.stereotype.Service;
import org.trc.domain.order.OutboundDetailLogistics;
import org.trc.service.impl.BaseService;
import org.trc.service.outbound.IOutboundDetailLogisticsService;

/**
 * Created by hzcyn on 2017/12/1.
 */
@Service("outboundDetailLogisticsService")
public class OutboundDetailLogisticsService extends BaseService<OutboundDetailLogistics, Long> implements IOutboundDetailLogisticsService {
}
