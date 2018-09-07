package org.trc.service.impl.AfterSale;


import org.springframework.stereotype.Service;
import org.trc.domain.afterSale.AfterSaleOrder;
import org.trc.service.afterSale.IAfterSaleOrderService;
import org.trc.service.impl.BaseService;


/**
 * <p>
 * 售后主表 服务类
 * </p>
 *
 * @author wangjie
 * @since 2018-08-27
 */
@Service("afterSaleOrderService")
public class AfterSaleOrderService extends BaseService<AfterSaleOrder, String> implements IAfterSaleOrderService {
	
}
