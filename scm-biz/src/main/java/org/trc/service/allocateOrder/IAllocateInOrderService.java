package org.trc.service.allocateOrder;

import org.trc.domain.allocateOrder.AllocateInOrder;
import org.trc.service.IBaseService;

public interface IAllocateInOrderService extends IBaseService<AllocateInOrder, Long>{

	void updateInOrderById(String status, Long id, String errMsg, String wmsAllocatInCode);

}
