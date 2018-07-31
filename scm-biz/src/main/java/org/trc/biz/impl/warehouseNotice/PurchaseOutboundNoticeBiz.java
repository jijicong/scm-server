package org.trc.biz.impl.warehouseNotice;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.trc.biz.warehouseNotice.IPurchaseOutboundNoticeBiz;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.purchase.PurchaseOutboundDetail;
import org.trc.domain.warehouseNotice.PurchaseOutboundNotice;
import org.trc.enums.warehouse.PurchaseOutboundNoticeStatusEnum;
import org.trc.form.warehouse.PurchaseOutboundNoticeForm;
import org.trc.service.warehouseNotice.IPurchaseOutboundNoticeService;
import org.trc.util.AssertUtil;
import org.trc.util.Pagenation;

/**
 * Description〈〉
 *
 * @author hzliuwei
 * @create 2018/7/24
 */
@Service("purchaseOutboundNoticeBiz")
public class PurchaseOutboundNoticeBiz implements IPurchaseOutboundNoticeBiz {
	
	@Autowired
	private IPurchaseOutboundNoticeService noticeService;

	@Override
	public Pagenation<PurchaseOutboundNotice> getPageList(PurchaseOutboundNoticeForm form,
			Pagenation<PurchaseOutboundNotice> page, String channelCode) {
		AssertUtil.notNull(channelCode, "业务线编号不能为空");
		return noticeService.pageList(form, page, channelCode);
	}

	@Override
	public PurchaseOutboundNotice getDetail(Long id) {
		PurchaseOutboundNotice notice = noticeService.selectByPrimaryKey(id);
		AssertUtil.notNull(notice, "未找到相应的退货单号");
		
		List<PurchaseOutboundDetail> skuList = noticeService.selectDetailByNoticeCode(notice.getOutboundNoticeCode());
		notice.setSkuList(skuList);
		return notice;
	}

	@Override
	public void noticeOut(String code, AclUserAccreditInfo property) {
		
		AssertUtil.notNull(code, "退货出库通知单编号不能为空!");
		
		List<PurchaseOutboundNotice> noticeList = noticeService.selectNoticeBycode(code);
		if (CollectionUtils.isEmpty(noticeList)) {
			throw new IllegalArgumentException("退货出库通知单编号有误!");
		} else if (noticeList.size() > 1) {
			throw new IllegalArgumentException("退货出库通知单编号重复!");
		}
		PurchaseOutboundNotice notice = noticeList.get(0);
		PurchaseOutboundNoticeStatusEnum.TO_BE_NOTIFIED.getCode().equals(notice.getStatus())    ;
		
	}

        
}
