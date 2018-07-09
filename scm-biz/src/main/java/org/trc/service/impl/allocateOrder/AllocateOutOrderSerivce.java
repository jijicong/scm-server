package org.trc.service.impl.allocateOrder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.trc.domain.allocateOrder.AllocateOutOrder;
import org.trc.enums.ZeroToNineEnum;
import org.trc.mapper.allocateOrder.AllocateOutOrderMapper;
import org.trc.service.allocateOrder.IAllocateOutOrderService;
import org.trc.service.impl.BaseService;

@Service("allocateOutOrderSerivce")
public class AllocateOutOrderSerivce extends BaseService<AllocateOutOrder, Long> implements IAllocateOutOrderService{
	
	@Autowired
	private AllocateOutOrderMapper mapper;
	
	@Override
	public void updateOutOrderById(String status, Long id, String errMsg, String wmsAllocatOutCode, Integer orderSeq) {
		AllocateOutOrder record = new AllocateOutOrder();
		record.setId(id);
		record.setStatus(status);
		record.setFailedCause(errMsg);
		record.setOutOrderSeq(orderSeq);
		record.setWmsAllocateOutOrderCode(wmsAllocatOutCode);
		record.setIsCancel(ZeroToNineEnum.ZERO.getCode());// 出库通知成功和失败，取消状态都设置成0
		mapper.updateByPrimaryKeySelective(record);
	}


}
