package org.trc.biz.warehouseNotice;

import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.warehouseNotice.PurchaseOutboundNotice;
import org.trc.form.warehouse.PurchaseOutboundNoticeForm;
import org.trc.util.Pagenation;

public interface IPurchaseOutboundNoticeBiz {

	Pagenation<PurchaseOutboundNotice> getPageList (PurchaseOutboundNoticeForm form, Pagenation<PurchaseOutboundNotice> page,
			String channelCode);

	PurchaseOutboundNotice getDetail(Long id);

	void noticeOut(String code, AclUserAccreditInfo property);


}
