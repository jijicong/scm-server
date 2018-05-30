package org.trc.service.impl.allocateOrder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.trc.domain.allocateOrder.AllocateInOrder;
import org.trc.mapper.allocateOrder.AllocateInOrderMapper;
import org.trc.service.allocateOrder.IAllocateInOrderService;
import org.trc.service.impl.BaseService;

@Service("allocateInOrderSerivce")
public class AllocateInOrderSerivce extends BaseService<AllocateInOrder, Long> implements IAllocateInOrderService {

	@Autowired
	private AllocateInOrderMapper mapper;
	
	@Override
	public void updateInOrderStatusById(String status, Long id, String errMsg) {
		AllocateInOrder record = new AllocateInOrder();
		record.setId(id);
		record.setStatus(status);
		record.setFailedCause(errMsg);
		mapper.updateByPrimaryKeySelective(record);
	}

}
