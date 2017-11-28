package org.trc.mapper.warehouseNotice;

import org.trc.domain.warehouseNotice.WarehouseNotice;
import org.trc.util.BaseMapper;

import java.util.List;
import java.util.Map;

/**
 *
 * @author sone
 * @date 2017/7/10
 */
public interface IWarehouseNoticeMapper extends BaseMapper<WarehouseNotice>{

    /**
     * 查询入库通知单
     * @param map
     * @return
     */
    List<WarehouseNotice> selectWarehouseNoticeList(Map<String, Object> map);

    /**
     * 查询入库通知单数量
     * @param map
     * @return
     */
    Integer selectCountWarehouseNotice(Map<String, Object> map);

}
