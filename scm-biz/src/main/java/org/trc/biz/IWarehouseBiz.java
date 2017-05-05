package org.trc.biz;

import org.trc.domain.System.Warehouse;
import org.trc.form.system.WarehouseForm;
import org.trc.util.Pagenation;

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
    public int saveWarehouse(Warehouse warehouse) throws Exception;
    /**
     * 根据仓库名称查找仓库（仓库名是否被使用）
     * @param name 仓库名
     * @return WarehouseBiz
     * @throws Exception
     */
    public Warehouse findWarehouseByName(String name) throws Exception;
    /**
     * 分页查询仓库信息
     * @param form  仓库查询条件
     * @param page  分页信息
     * @return  分页信息及当前页面的数据
     */
    Pagenation<Warehouse> warehousePage(WarehouseForm form, Pagenation<Warehouse> page) throws Exception;
    /**
     * 更新仓库
     * @param warehouse 仓库信息
     * @param id 主键
     * @return 整数改变
     * @throws Exception
     */
    public int updateWarehouse(Warehouse warehouse,Long id) throws Exception;
    /**
     * 根据id查询仓库
     * @param id 主键
     * @return 仓库实例
     * @throws Exception
     */
    public Warehouse findWarehouseById(Long id) throws Exception;
    /**
     * 仓库状态修改
     * @param warehouse
     * @return
     * @throws Exception
     */
    public int updateWarehouseState(Warehouse warehouse) throws Exception;
}
