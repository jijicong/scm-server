package org.trc.service.impl.purchase;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.trc.domain.allocateOrder.AllocateSkuDetail;
import org.trc.domain.purchase.PurchaseOutboundDetail;
import org.trc.enums.ZeroToNineEnum;
import org.trc.mapper.purchase.IPurchaseOutboundDetailMapper;
import org.trc.service.impl.BaseService;
import org.trc.service.purchase.IPurchaseOutboundDetailService;

import tk.mybatis.mapper.entity.Example;

/**
 * Description〈〉
 *
 * @author hzliuwei
 * @create 2018/7/25
 */
@Service("purchaseOutboundDetailService")
public class PurchaseOutboundDetailService extends BaseService<PurchaseOutboundDetail,Long> implements IPurchaseOutboundDetailService {

	@Autowired
	private IPurchaseOutboundDetailMapper detailMapper;
	
	@Override
	public List<PurchaseOutboundDetail> selectDetailByNoticeCode(String outboundNoticeCode) {
		PurchaseOutboundDetail queryDetail = new PurchaseOutboundDetail();
		queryDetail.setOutboundNoticeCode(outboundNoticeCode);
		// 未删除的记录
		queryDetail.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
		return detailMapper.select(queryDetail);

	}

	@Override
	public void updateByOrderCode(String status, String outboundNoticeCode) {
		PurchaseOutboundDetail updateRecord = new PurchaseOutboundDetail();
		updateRecord.setStatus(status);
		Example example = new Example(PurchaseOutboundDetail.class);
		Example.Criteria ca = example.createCriteria();
		ca.andEqualTo("outboundNoticeCode", outboundNoticeCode);
		ca.andEqualTo("isDeleted", ZeroToNineEnum.ZERO.getCode());
		detailMapper.updateByExampleSelective(updateRecord, example);
		
	}
}
