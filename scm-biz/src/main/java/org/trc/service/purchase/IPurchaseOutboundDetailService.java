package org.trc.service.purchase;

import java.util.List;

import org.trc.domain.purchase.PurchaseOutboundDetail;
import org.trc.service.IBaseService;

public interface IPurchaseOutboundDetailService extends IBaseService<PurchaseOutboundDetail,Long> {

	List<PurchaseOutboundDetail> selectDetailByNoticeCode(String outboundNoticeCode);

	void updateByOrderCode(String status, String outboundNoticeCode);
}
