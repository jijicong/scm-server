package org.trc.service.purchase;

import org.trc.domain.warehouseNotice.WarehouseNotice;
import org.trc.service.IBaseService;

import java.util.List;
import java.util.Map;

/**
 * Created by sone on 2017/7/10.
 */
public interface IWarehouseNoticeService extends IBaseService<WarehouseNotice,Long>{
    /**
     * 查询入库通知的列表
     * @param map
     * @return
     */
    List<WarehouseNotice> selectWarehouseNoticeList(Map<String,Object> map);

    /**
     * 查询该查询条件的总条数
     * @param map
     * @return
     */
    int selectCountWarehouseNotice(Map<String,Object> map);

    /**
     * 通过入库通知单编号查询入库通知
     */
    List<WarehouseNotice> selectWarehouseNoticeListByNoticeCode(String warehouseNoticeCode);
}
