package org.trc.biz.afterSale;


import org.trc.domain.afterSale.AfterSaleWarehouseNotice;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.form.afterSale.AfterSaleWarehouseNoticeDO;
import org.trc.form.afterSale.AfterSaleWarehouseNoticeVO;
import org.trc.util.Pagenation;


public interface IAfterSaleWarehouseNoticeBiz {

	Pagenation<AfterSaleWarehouseNotice> warehouseNoticeList(AfterSaleWarehouseNoticeDO afterSaleWarehouseNoticeDO,
			Pagenation<AfterSaleWarehouseNotice> page,AclUserAccreditInfo aclUserAccreditInfo);

	AfterSaleWarehouseNoticeVO warehouseNoticeInfo(String warehouseNoticeCode) throws Exception;

}
