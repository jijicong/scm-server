package org.trc.service.impl.allocateOrder;

import org.springframework.stereotype.Service;
import org.trc.domain.allocateOrder.AllocateOrder;
import org.trc.service.allocateOrder.IAllocateOrderService;
import org.trc.service.impl.BaseService;

@Service("allocateOrderSerivce")
public class AllocateOrderSerivce extends BaseService<AllocateOrder, String> implements IAllocateOrderService{

}
