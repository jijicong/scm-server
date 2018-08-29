package org.trc.biz.afterSale;

import org.trc.domain.System.LogisticsCompany;
import org.trc.domain.afterSale.AfterSaleOrder;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.warehouseInfo.WarehouseInfo;
import org.trc.form.afterSale.AfterSaleDetailVO;
import org.trc.form.afterSale.AfterSaleOrderAddDO;
import org.trc.form.afterSale.AfterSaleOrderForm;
import org.trc.form.afterSale.AfterSaleOrderItemVO;
import org.trc.form.afterSale.AfterSaleOrderVO;
import org.trc.util.Pagenation;

import javax.ws.rs.core.Response;
import java.util.List;

public interface IAfterSaleOrderBiz {

	List<AfterSaleOrderItemVO> selectAfterSaleInfo(String shopOrderCode) throws Exception;

	void addAfterSaleOrder(AfterSaleOrderAddDO afterSaleOrderAddDO,AclUserAccreditInfo aclUserAccreditInfo);

	List<LogisticsCompany> selectLogisticsCompany();

	List<WarehouseInfo> selectWarehouse();

    /**
     * @Description: 售后单分页查询
     * @Author: hzluoxingcheng
     * @Date: 2018/8/29
     */
	public Pagenation<AfterSaleOrderVO> afterSaleOrderPage(AfterSaleOrderForm form, Pagenation<AfterSaleOrder> page);

	/**
	 * 查询售后单详情
	 * @param id
	 * @return
	 */
	AfterSaleDetailVO queryAfterSaleOrderDetail(Long id);
    
	/**
	 * @Description: 售后单导出
	 * @Author: hzluoxingcheng
	 * @Date: 2018/8/29
	 */ 
	public Response exportAfterSaleOrderVO(AfterSaleOrderForm form, Pagenation<AfterSaleOrder> page) throws Exception;


}
