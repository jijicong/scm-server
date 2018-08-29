package org.trc.biz.afterSale;

import java.util.List;

import org.trc.domain.System.LogisticsCompany;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.warehouseInfo.WarehouseInfo;
import org.trc.form.afterSale.AfterSaleOrderAddDO;
import org.trc.form.afterSale.AfterSaleOrderItemVO;


public interface IAfterSaleOrderBiz {

	List<AfterSaleOrderItemVO> selectAfterSaleInfo(String shopOrderCode) throws Exception;

	void addAfterSaleOrder(AfterSaleOrderAddDO afterSaleOrderAddDO,AclUserAccreditInfo aclUserAccreditInfo);

	List<LogisticsCompany> selectLogisticsCompany();

	List<WarehouseInfo> selectWarehouse();

	

}
