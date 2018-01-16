package org.trc.biz.system;

import org.trc.domain.System.Warehouse;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.form.system.WarehouseForm;
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
    void saveWarehouse(Warehouse warehouse,AclUserAccreditInfo aclUserAccreditInfo);
    /**
     * 根据仓库名称查找仓库（仓库名是否被使用）
     * @param name 仓库名
     * @return WarehouseBiz
     * @throws Exception
     */
    Warehouse findWarehouseByName(String name);
    /**
     * 分页查询仓库信息
     * @param form  仓库查询条件
     * @param page  分页信息
     * @return  分页信息及当前页面的数据
     */
    Pagenation<Warehouse> warehousePage(WarehouseForm form, Pagenation<Warehouse> page);
    /**
     * 更新仓库
     * @param warehouse 仓库信息
     * @return 整数改变
     * @throws Exception
     */
    void updateWarehouse(Warehouse warehouse,AclUserAccreditInfo aclUserAccreditInfo);
    /**
     * 根据id查询仓库
     * @param id 主键
     * @return 仓库实例
     * @throws Exception
     */
    Warehouse findWarehouseById(Long id);
    /**
     * 仓库状态修改
     * @param warehouse
     * @return
     * @throws Exception
     */
    void updateWarehouseState(Warehouse warehouse, AclUserAccreditInfo aclUserAccreditInfo);

    /**
     * 查询有效的仓库
     * @return
     * @throws Exception
     */
    List<Warehouse> findWarehouseValid();

    /**
     * 查询所有仓库
     * @return
     */
    List<Warehouse> findWarehouse();

    /**
     * 模糊查询
     */
    Pagenation<Warehouse> warehousePageEs(WarehouseForm form, Pagenation<Warehouse> page);

    /**
     * 修改仓库信息配置
     * @param warehouse
     */
    void updateWarehouseConfig(Warehouse warehouse);

    /**
     * 获取仓库信息
     * @return
     */
    List<Warehouse> findNotConfigWarehouse();
}
