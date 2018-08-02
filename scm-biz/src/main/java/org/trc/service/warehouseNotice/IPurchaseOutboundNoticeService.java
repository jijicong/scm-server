package org.trc.service.warehouseNotice;

import java.util.List;

import org.trc.domain.purchase.PurchaseOutboundDetail;
import org.trc.domain.warehouseNotice.PurchaseOutboundNotice;
import org.trc.enums.WarehouseNoticeStatusEnum;
import org.trc.enums.warehouse.PurchaseOutboundNoticeStatusEnum;
import org.trc.form.warehouse.PurchaseOutboundNoticeForm;
import org.trc.form.warehouse.ScmOrderCancelResponse;
import org.trc.service.IBaseService;
import org.trc.util.AppResult;
import org.trc.util.Pagenation;

public interface IPurchaseOutboundNoticeService extends IBaseService<PurchaseOutboundNotice,Long> {

	Pagenation<PurchaseOutboundNotice> pageList(PurchaseOutboundNoticeForm form, Pagenation<PurchaseOutboundNotice> page, String channelCode);

	List<PurchaseOutboundNotice> selectNoticeBycode(String code);

	void updateById(PurchaseOutboundNoticeStatusEnum status, Long id, String errMsg, String wmsEntryRtCode);

	void generateNames(Pagenation<PurchaseOutboundNotice> resultPage);

	List<PurchaseOutboundNotice> selectNoticeByStatus(PurchaseOutboundNoticeStatusEnum status);

	PurchaseOutboundNotice selectOneByEntryOrderCode(String entryOrderCode);

	void updateCancelOrder(AppResult<ScmOrderCancelResponse> responseAppResult, String orderCode);
	
	
}
