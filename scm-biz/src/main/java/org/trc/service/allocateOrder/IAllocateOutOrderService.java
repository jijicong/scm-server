package org.trc.service.allocateOrder;

import org.trc.domain.allocateOrder.AllocateOutOrder;
import org.trc.service.IBaseService;

public interface IAllocateOutOrderService extends IBaseService<AllocateOutOrder, Long>{

	//void updateOutOrderStatusById(String status, Long id);

	void updateOutOrderById(String status, Long id, String errMsg);

}
