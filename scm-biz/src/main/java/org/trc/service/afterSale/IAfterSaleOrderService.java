package org.trc.service.afterSale;


import org.trc.domain.afterSale.AfterSaleOrder;
import org.trc.form.afterSale.AfterSaleNoticeWmsForm;
import org.trc.form.afterSale.AfterSaleNoticeWmsResultVO;
import org.trc.service.IBaseService;

import java.util.Map;


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
	 * @param skuCode  待取消的商品编码
	 * @return 
	 * @throws Exception 
	 */
	public Map<String, Object> deliveryCancel(String scmShopOrderCode, String skuCode);
	
	/**
	 * 售后单，取消结果
	 * @param form
	 * @return
	 */
	public List<AfterSaleNoticeWmsResultVO> deliveryCancelResult(List<AfterSaleNoticeWmsForm> form);
	
	
}
	