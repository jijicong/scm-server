package org.trc.service.allocateOrder;

import java.util.List;

import org.trc.domain.allocateOrder.AllocateSkuDetail;
import org.trc.service.IBaseService;

public interface IAllocateSkuDetailService extends IBaseService<AllocateSkuDetail, Long>{

	void updateSkuDetailList(List<AllocateSkuDetail> updateList);

	void deleteByIds(String[] idArray);

	void updateOutSkuStatusByOrderCode(String status, String allocateOrderCode);

	List<AllocateSkuDetail> getDetailListByOrderCode(String allocateOrderCode);

	void updateInSkuStatusByOrderCode(String status, String allocateOrderCode);

}
