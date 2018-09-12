package org.trc.service.afterSale;


import java.util.List;
import java.util.Map;

import org.trc.domain.afterSale.AfterSaleOrder;
import org.trc.service.IBaseService;


/**
 * <p>
 * 售后主表 服务类
 * </p>
 *
 * @author wangjie
 * @since 2018-08-27
 */
public interface IAfterSaleOrderService extends IBaseService<AfterSaleOrder, String> {
	
	/**
	 * 售后单，取消发货
	 * @param scmShopOrderCode 系统订单号
	 * @param skuList  待取消的商品编码列表
	 * @return 
	 * @throws Exception 
	 */
	public Map<String, Object> deliveryCancel(String scmShopOrderCode, String skuCode) throws Exception;
}
	