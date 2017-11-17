package org.trc.biz.warehouseInfo;

import org.trc.domain.goods.Skus;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.warehouseInfo.WarehouseInfo;
import org.trc.domain.warehouseInfo.WarehouseItemInfo;
import org.trc.form.warehouseInfo.*;
import org.trc.util.Pagenation;

import javax.ws.rs.core.Response;
import java.util.List;

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

    /**
     *仓库商品信息分页查询
     * @param query
     * @param page
     * @return
     */
    Pagenation<WarehouseItemInfo> queryWarehouseItemInfoPage(WarehouseItemInfoForm form, Long warehouseInfoId, Pagenation<WarehouseItemInfo> page);

    /**
     * 删除仓库商品信息
     * @param id
     */
    void deleteWarehouseItemInfoById(Long id);

    /**
     * 编辑仓库商品信息
     * @param warehouseItemInfo
     */
    void updateWarehouseItemInfo(WarehouseItemInfo warehouseItemInfo);

    /**
     * 商品信息导出
     * @param form 查询条件
     * @param warehouseInfoId 仓库ID
     * @return
     */
    Response exportWarehouseItems(WarehouseItemInfoForm form, Long warehouseInfoId);

    //新增商品
    Response saveWarehouseItemsSku(List<Skus> itemsList,Long warehouseInfoId);

   //新增商品信息分页查询
   Pagenation<ItemsResult> queryWarehouseItemsSku(SkusForm form, Pagenation<Skus> page, Long warehouseInfoId);
}
