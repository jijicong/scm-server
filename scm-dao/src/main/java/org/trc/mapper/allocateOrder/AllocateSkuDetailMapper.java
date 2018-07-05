package org.trc.mapper.allocateOrder;

import java.util.List;

import org.trc.domain.allocateOrder.AllocateSkuDetail;
import org.trc.util.BaseMapper;

public interface AllocateSkuDetailMapper extends BaseMapper<AllocateSkuDetail> {

	void updateSkuDetailList(List<AllocateSkuDetail> updateList);

	void deleteByIds(String[] idArray);
}