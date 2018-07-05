package org.trc.service.impl.purchase;

import org.springframework.stereotype.Service;
import org.trc.domain.warehouseNotice.WarehouseNotice;
import org.trc.mapper.warehouseNotice.IWarehouseNoticeMapper;
import org.trc.service.impl.BaseService;
import org.trc.service.purchase.IWarehouseNoticeService;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * Created by sone on 2017/7/10.
 */
@Service("warehouseNoticeService")
public class WarehouseNoticeService extends BaseService<WarehouseNotice,Long> implements IWarehouseNoticeService{

    @Resource
    private IWarehouseNoticeMapper iWarehouseNoticeMapper;

    @Override
    public List<WarehouseNotice> selectWarehouseNoticeList(Map<String, Object> map) {
        return iWarehouseNoticeMapper.selectWarehouseNoticeList(map);
    }

    @Override
    public int selectCountWarehouseNotice(Map<String, Object> map) {
        return iWarehouseNoticeMapper.selectCountWarehouseNotice(map);
    }

}
