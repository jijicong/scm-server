package org.trc.service.impl.allocateOrder;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.trc.domain.allocateOrder.AllocateSkuDetail;
import org.trc.mapper.allocateOrder.AllocateSkuDetailMapper;
import org.trc.service.allocateOrder.IAllocateSkuDetailService;
import org.trc.service.impl.BaseService;

@Service("allocateSkuDetailSerivce")
public class AllocateSkuDetailService extends BaseService<AllocateSkuDetail, Long> implements IAllocateSkuDetailService{

	@Autowired
	private AllocateSkuDetailMapper allocateSkuDetailMapper;
	
	@Override
	public void updateSkuDetailList(List<AllocateSkuDetail> updateList) {
		allocateSkuDetailMapper.updateSkuDetailList(updateList);
	}

	@Override
	public void deleteByIds(String[] idArray) {
		allocateSkuDetailMapper.deleteByIds(idArray);
	}

}
