package org.trc.service.impl.order;

import org.springframework.stereotype.Service;
import org.trc.domain.order.ExceptionOrder;
import org.trc.service.impl.BaseService;
import org.trc.service.order.IExceptionOrderService;

/**
 * Created by hzcyn on 2017/11/13.
 */
@Service("exceptionOrderService")
public class ExceptionOrderService extends BaseService<ExceptionOrder,Long> implements IExceptionOrderService {
}
