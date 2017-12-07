package org.trc.service.outbound;

import java.util.List;

import org.trc.domain.order.OutboundDetail;
import org.trc.service.IBaseService;

/**
 * Created by hzcyn on 2017/12/1.
 */
public interface IOutboundDetailService extends IBaseService<OutboundDetail,Long> {

	/**
	 * 根据仓库级订单获取自采出库单详情
	 * @param warehouseOrderCode 仓库级订单号
	 * @return
	 */
	List<OutboundDetail> selectByWarehouseOrderCode(String warehouseOrderCode);
}
