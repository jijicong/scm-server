package org.trc.service.impl.allocateOrder;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.trc.domain.allocateOrder.AllocateOutOrder;
import org.trc.domain.allocateOrder.AllocateSkuDetail;
import org.trc.mapper.allocateOrder.AllocateSkuDetailMapper;
import org.trc.service.allocateOrder.IAllocateSkuDetailService;
import org.trc.service.impl.BaseService;

import tk.mybatis.mapper.entity.Example;

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

	@Override
	public void updateOutSkuStatusByOutOrderCode(String status, String allocateOrderCode) {
		AllocateSkuDetail record = new AllocateSkuDetail();
		record.setOutStatus(status);
        Example example = new Example(AllocateSkuDetail.class);
        Example.Criteria ca = example.createCriteria();
        ca.andEqualTo("allocateOrderCode", allocateOrderCode);
		allocateSkuDetailMapper.updateByExampleSelective(record, example);
		
	}

}
