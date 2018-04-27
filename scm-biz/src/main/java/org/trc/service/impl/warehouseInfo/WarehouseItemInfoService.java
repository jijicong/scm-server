package org.trc.service.impl.warehouseInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.trc.domain.warehouseInfo.WarehouseItemInfo;
import org.trc.mapper.warehouseInfo.IWarehouseInfoMapper;
import org.trc.mapper.warehouseInfo.IWarehouseItemInfoMapper;
import org.trc.service.impl.BaseService;
import org.trc.service.warehouseInfo.IWarehouseItemInfoService;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * Created by hzcyn on 2017/11/16.
 *
 * @author hzcyn
 */
@Service("warehouseItemInfoService")
public class WarehouseItemInfoService extends BaseService<WarehouseItemInfo, Long> implements IWarehouseItemInfoService {

    @Resource
    private IWarehouseInfoMapper warehouseInfoMapper;
    @Resource
    private IWarehouseItemInfoMapper warehouseItemInfoMapper;

    @Override
    public int batchUpdate(Map<String, Object> map) {
        return warehouseInfoMapper.batchUpdate(map);
    }

    @Override
    public int selectWarehouseItemInfoCount(Map<String, Object> map) {
        return warehouseItemInfoMapper.selectWarehouseItemInfoCount(map);
    }

    @Override
    public List<WarehouseItemInfo> selectWarehouseItemInfo(Map<String, Object> map) {
        return warehouseItemInfoMapper.selectWarehouseItemInfo(map);
    }
}
