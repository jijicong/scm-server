package org.trc.service.impl.warehouseInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.trc.domain.warehouseInfo.WarehouseInfo;
import org.trc.mapper.warehouseInfo.IWarehouseInfoMapper;
import org.trc.service.impl.BaseService;
import org.trc.service.warehouseInfo.IWarehouseInfoService;

/**
 * Created by wangyz on 2017/11/15.
 */
@Service("warehouseInfoService")
public class WarehouseInfoServiceImpl extends BaseService<WarehouseInfo,Long> implements IWarehouseInfoService {
	@Autowired
	private IWarehouseInfoMapper mapper;
	
	@Override
	public WarehouseInfo selectOneByCode(String warehouseCode) {
        WarehouseInfo whi = new WarehouseInfo();
        whi.setCode(warehouseCode);
        return mapper.selectOne(whi);
	}
}
