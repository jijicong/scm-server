package org.trc.service.System;

import org.trc.domain.System.Warehouse;
import org.trc.service.IBaseService;

import java.util.List;

/**
 * warehouse service
 * Created by sone on 2017/5/4.
 */
public interface IWarehouseService extends IBaseService<Warehouse,Long>{
    /**
     * 根据仓库的编码，查询仓库
     * @param strs
     * @return
     */
    List<Warehouse> selectWarehouseNames(String strs[]);

    /**
     * 获取仓库信息
     * @return
     */
    List<Warehouse> findNotConfigWarehouse();
}
