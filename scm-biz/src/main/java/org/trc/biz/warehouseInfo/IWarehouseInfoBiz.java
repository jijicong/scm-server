package org.trc.biz.warehouseInfo;

import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.warehouseInfo.WarehouseInfo;
import org.trc.form.warehouseInfo.WarehouseInfoForm;
import org.trc.form.warehouseInfo.WarehouseInfoResult;
import org.trc.util.Pagenation;

import javax.ws.rs.core.Response;

/**
 * Created by wangyz on 2017/11/15.
 */
public interface IWarehouseInfoBiz {

    /**
     * 添加仓库
     * @param code 奇门仓库编码
     * @return
     */
    Response saveWarehouse(String code, AclUserAccreditInfo aclUserAccreditInfo);

    /**
     * 查询不在本地的仓库
     * @return
     */
    Response selectWarehouseNotInLocation();

    /**
     * 查询仓库名
     * @return
     */
    Response selectWarehouse();

    /**
     * 仓库信息分页查询
     * @param query 查询条件
     * @param page 分页
     * @return
     */
    Pagenation<WarehouseInfoResult> selectWarehouseInfoByPage(WarehouseInfoForm query, Pagenation<WarehouseInfo> page);

    /**
     * 保存货主信息
     * @param warehouseInfo 货主信息
     * @return
     */
    Response saveOwnerInfo(WarehouseInfo warehouseInfo);

    /**
     * 删除仓库信息
     * @param id 仓库ID
     * @return
     */
    Response deleteWarehouse(String id);

}
