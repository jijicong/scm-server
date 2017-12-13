package org.trc.service.impl.outbound;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.trc.domain.order.OutboundDetail;
import org.trc.mapper.outbound.IOutboundDetailMapper;
import org.trc.service.impl.BaseService;
import org.trc.service.outbound.IOutboundDetailService;

/**
 * Created by hzcyn on 2017/12/1.
 */
@Service("outboundDetailService")
public class OutboundDetailService extends BaseService<OutboundDetail, Long> implements IOutboundDetailService {

	@Autowired
	private IOutboundDetailMapper outboundDetailMapper;
	
	@Override
	public List<OutboundDetail> selectByWarehouseOrderCode(String warehouseOrderCode) {
		return outboundDetailMapper.selectByWarehouseOrderCode(warehouseOrderCode);
	}
}
