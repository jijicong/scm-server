package org.trc.service.impl.allocateOrder;

import org.springframework.stereotype.Service;
import org.trc.domain.allocateOrder.AllocateOutOrder;
import org.trc.service.allocateOrder.IAllocateOutOrderService;
import org.trc.service.impl.BaseService;

@Service("allocateOutOrderSerivce")
public class AllocateOutOrderSerivce extends BaseService<AllocateOutOrder, Long> implements IAllocateOutOrderService{

}
