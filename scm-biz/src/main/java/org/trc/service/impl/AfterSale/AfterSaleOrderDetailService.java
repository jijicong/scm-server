package org.trc.service.impl.AfterSale;

import org.springframework.stereotype.Service;
import org.trc.domain.afterSale.AfterSaleOrderDetail;
import org.trc.service.afterSale.IAfterSaleOrderDetailService;
import org.trc.service.impl.BaseService;

/**
 * <p>
 * 售后单明细表 服务类
 * </p>
 *
 * @author wangjie
 * @since 2018-08-27
 */
@Service("afterSaleOrderDetailService")
public class AfterSaleOrderDetailService extends BaseService<AfterSaleOrderDetail, Long> implements IAfterSaleOrderDetailService {
	
}
