package org.trc.service.impl.order;

import org.springframework.stereotype.Service;
import org.trc.domain.order.ExceptionOrderItem;
import org.trc.service.impl.BaseService;
import org.trc.service.order.IExceptionOrderItemService;

/**
 * Created by hzcyn on 2017/11/14.
 */
@Service("exceptionOrderItemService")
public class ExceptionOrderItemService extends BaseService<ExceptionOrderItem,Long> implements IExceptionOrderItemService {
}


