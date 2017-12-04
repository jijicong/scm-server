package org.trc.service.impl.outbound;

import org.springframework.stereotype.Service;
import org.trc.domain.order.OutboundDetail;
import org.trc.service.impl.BaseService;
import org.trc.service.outbound.IOutboundDetailService;

/**
 * Created by hzcyn on 2017/12/1.
 */
@Service("outboundDetailService")
public class OutboundDetailService extends BaseService<OutboundDetail, Long> implements IOutboundDetailService {
}
