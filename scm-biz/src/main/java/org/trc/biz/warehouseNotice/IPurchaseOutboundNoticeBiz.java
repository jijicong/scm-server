package org.trc.biz.warehouseNotice;

import org.trc.domain.warehouseNotice.PurchaseOutboundNotice;
import org.trc.form.warehouse.PurchaseOutboundNoticeForm;
import org.trc.util.Pagenation;

public interface IPurchaseOutboundNoticeBiz {

	Pagenation<PurchaseOutboundNotice> pageList (PurchaseOutboundNoticeForm form, Pagenation<PurchaseOutboundNotice> page,
			String channelCode);

}
