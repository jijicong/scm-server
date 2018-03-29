package org.trc.biz.system;

import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.warehouseInfo.WarehouseInfo;
import org.trc.form.system.WarehouseForm;
import org.trc.form.warehouseInfo.WarehouseInfoResult;
import org.trc.util.Pagenation;

import java.util.List;

/**
 * 仓库管理
 * Created by sone on 2017/5/5.
 */
public interface IWarehouseBiz {
    /**
     * 仓库保存
     * @param warehouse 仓库信息
     * @return  int 插入的数量
     * @throws Exception
     */
    void saveWarehouse(WarehouseInfo warehouse,AclUserAccreditInfo aclUserAccreditInfo);
    /**
     * 根据仓库名称查找仓库（仓库名是否被使用）
     * @param name 仓库名
     * @return WarehouseBiz
     * @throws Exception
     */
    WarehouseInfo findWarehouseByName(String name);
    /**
     * 分页查询仓库信息
     * @param form  仓库查询条件
     * @param page  分页信息
     * @return  分页信息及当前页面的数据
     */
    Pagenation<WarehouseInfoResult> warehousePage(WarehouseForm form, Pagenation<WarehouseInfo> page);
    /**
     * 更新仓库
     * @param warehouse 仓库信息
     * @return 整数改变
     * @throws Exception
     */
    void updateWarehouse(WarehouseInfo warehouse,AclUserAccreditInfo aclUserAccreditInfo);
    /**
     * 根据id查询仓库
     * @param id 主键
     * @return 仓库实例
     * @throws Exception
     */
    WarehouseInfo findWarehouseById(Long id);
    /**
     * 仓库状态修改
     * @param warehouse
     * @return
     * @throws Exception
     */
    void updateWarehouseState(WarehouseInfo warehouse, AclUserAccreditInfo aclUserAccreditInfo);

    /**
     * 查询有效的仓库
     * @return
     * @throws Exception
     */
    List<WarehouseInfo> findWarehouseValid();

    /**
     * 查询所有仓库
     * @return
     */
    List<WarehouseInfo> findWarehouse();

    /**
     * 模糊查询
     */
    Pagenation<WarehouseInfo> warehousePageEs(WarehouseForm form, Pagenation<WarehouseInfo> page);

    /**
     * 修改仓库信息配置
     * @param warehouse
     */
    void updateWarehouseConfig(WarehouseInfo warehouse);

}
