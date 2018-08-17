package org.trc.biz.warehouseNotice;

import javax.ws.rs.core.Response;

import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.warehouseNotice.PurchaseOutboundNotice;
import org.trc.form.warehouse.PurchaseOutboundNoticeForm;
import org.trc.util.Pagenation;

public interface IPurchaseOutboundNoticeBiz {

	Pagenation<PurchaseOutboundNotice> getPageList (PurchaseOutboundNoticeForm form, Pagenation<PurchaseOutboundNotice> page,
			String channelCode);

	PurchaseOutboundNotice getDetail(Long id);

	void noticeOut(String code, AclUserAccreditInfo property);

	Response cancel(String code, String cancelReson, AclUserAccreditInfo property);

	void retryCancelOrder();

	void entryReturnDetailQuery();


}
