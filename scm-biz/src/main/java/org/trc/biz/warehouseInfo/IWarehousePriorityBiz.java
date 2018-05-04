package org.trc.biz.warehouseInfo;

import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.warehouseInfo.WarehouseInfo;
import org.trc.domain.warehouseInfo.WarehousePriority;
import org.trc.util.Pagenation;

import java.util.List;

public interface IWarehousePriorityBiz {

    /**
     * 仓库商品信息
     * @return
     */
    List<WarehousePriority> warehousePriorityList();

    /**
     * 查询启用仓库信息
     * @return
     */
    List<WarehouseInfo> queryWarehouseInfoList();

    /**
     * 保存仓库优先级信息
     * @param warehousePriorityInfo
     */
    void saveWarehousePriority(String warehousePriorityInfo, AclUserAccreditInfo aclUserAccreditInfo);

}
