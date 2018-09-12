package org.trc.biz.afterSale;


import org.trc.domain.afterSale.AfterSaleWarehouseNotice;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.warehouseInfo.WarehouseInfo;
import org.trc.form.afterSale.AfterSaleWarehouseNoticeDO;
import org.trc.form.afterSale.AfterSaleWarehouseNoticeVO;
import org.trc.util.Pagenation;

import java.util.List;


public interface IAfterSaleWarehouseNoticeBiz {

	Pagenation<AfterSaleWarehouseNotice> warehouseNoticeList(AfterSaleWarehouseNoticeDO afterSaleWarehouseNoticeDO,
			Pagenation<AfterSaleWarehouseNotice> page,AclUserAccreditInfo aclUserAccreditInfo);

	AfterSaleWarehouseNoticeVO warehouseNoticeInfo(String warehouseNoticeCode) throws Exception;

	List<WarehouseInfo> selectWarehouse();

}
