package org.trc.service.impl.warehouseInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.trc.domain.warehouseInfo.WarehouseInfo;
import org.trc.mapper.warehouseInfo.IWarehouseInfoMapper;
import org.trc.service.impl.BaseService;
import org.trc.service.warehouseInfo.IWarehouseInfoService;

import java.util.List;
import java.util.Map;

/**
 * Created by wangyz on 2017/11/15.
 */
@Service("warehouseInfoService")
public class WarehouseInfoServiceImpl extends BaseService<WarehouseInfo,Long> implements IWarehouseInfoService {
    @Autowired
    private IWarehouseInfoMapper warehouseInfoMapper;
    @Override
    public List<WarehouseInfo> selectWarehouseInfo(Map<String, String> map) {
        return warehouseInfoMapper.selectWarehouseInfo(map);
    }
}
