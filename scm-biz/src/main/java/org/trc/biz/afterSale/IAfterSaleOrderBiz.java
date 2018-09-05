package org.trc.biz.afterSale;

import org.trc.domain.System.LogisticsCompany;
import org.trc.domain.afterSale.AfterSaleOrder;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.warehouseInfo.WarehouseInfo;
import org.trc.form.afterSale.*;
import org.trc.form.returnIn.ReturnInWmsResponseForm;
import org.trc.util.Pagenation;

import javax.ws.rs.core.Response;
import java.util.List;

public interface IAfterSaleOrderBiz {

	List<AfterSaleOrderItemVO> selectAfterSaleInfo(String scmShopOrderCode) throws Exception;

	void addAfterSaleOrder(AfterSaleOrderAddDO afterSaleOrderAddDO,AclUserAccreditInfo aclUserAccreditInfo);

	List<LogisticsCompany> selectLogisticsCompany();

	List<WarehouseInfo> selectWarehouse();

    /**
     * @Description: 售后单分页查询
     * @Author: hzluoxingcheng
     * @Date: 2018/8/29
     */
	public Pagenation<AfterSaleOrderVO> afterSaleOrderPage(AfterSaleOrderForm form, Pagenation<AfterSaleOrder> page,AclUserAccreditInfo aclUserAccreditInfo);

	/**
	 * 查询售后单详情
	 * @param id
	 * @return
	 */
	AfterSaleDetailVO queryAfterSaleOrderDetail(String id);
    
	/**
	 * @Description: 售后单导出
	 * @Author: hzluoxingcheng
	 * @Date: 2018/8/29
	 */ 
	public Response exportAfterSaleOrderVO(AfterSaleOrderForm form, Pagenation<AfterSaleOrder> page,AclUserAccreditInfo aclUserAccreditInfo) throws Exception;
	
	/**
	 * @Description: 检查订单是否可以收件售后单
	 * @Author: hzluoxingcheng
	 * @Date: 2018/8/30
	 */ 
	public boolean checkOrder(String shopOrderCode,AclUserAccreditInfo aclUserAccreditInfo );

	/**
	 * 退货入库单收货结果通知
	 * @param req
	 */
	void returnInOrderResultNotice(ReturnInWmsResponseForm req);


}
