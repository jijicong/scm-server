package org.trc.mapper.warehouseNotice;

import org.trc.domain.warehouseNotice.WarehouseNotice;
import org.trc.util.BaseMapper;

import java.util.List;
import java.util.Map;

/**
 * Created by sone on 2017/7/10.
 */
public interface IWarehouseNoticeMapper extends BaseMapper<WarehouseNotice>{

    List<WarehouseNotice> selectWarehouseNoticeList(Map<String, Object> map);

    Integer selectCountWarehouseNotice(Map<String, Object> map);

}
