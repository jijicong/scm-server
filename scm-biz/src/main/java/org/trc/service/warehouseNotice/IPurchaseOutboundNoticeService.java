package org.trc.service.warehouseNotice;

import java.util.List;

import org.trc.domain.purchase.PurchaseOutboundDetail;
import org.trc.domain.warehouseNotice.PurchaseOutboundNotice;
import org.trc.form.warehouse.PurchaseOutboundNoticeForm;
import org.trc.service.IBaseService;
import org.trc.util.Pagenation;

public interface IPurchaseOutboundNoticeService extends IBaseService<PurchaseOutboundNotice,Long> {

	Pagenation<PurchaseOutboundNotice> pageList(PurchaseOutboundNoticeForm form, Pagenation<PurchaseOutboundNotice> page, String channelCode);

	List<PurchaseOutboundDetail> selectDetailByNoticeCode(String outboundNoticeCode);

	List<PurchaseOutboundNotice> selectNoticeBycode(String code);
}
