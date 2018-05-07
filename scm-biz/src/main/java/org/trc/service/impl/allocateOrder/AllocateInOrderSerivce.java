package org.trc.service.impl.allocateOrder;

import org.springframework.stereotype.Service;
import org.trc.domain.allocateOrder.AllocateInOrder;
import org.trc.service.allocateOrder.IAllocateInOrderService;
import org.trc.service.impl.BaseService;

@Service("allocateInOrderSerivce")
public class AllocateInOrderSerivce extends BaseService<AllocateInOrder, Long> implements IAllocateInOrderService {

}
