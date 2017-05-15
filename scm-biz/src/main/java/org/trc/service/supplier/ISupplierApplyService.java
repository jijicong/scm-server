package org.trc.service.supplier;

import org.apache.ibatis.annotations.Param;
import org.trc.domain.supplier.SupplierApply;
import org.trc.service.IBaseService;
import org.trc.service.impl.BaseService;

import java.util.List;
import java.util.Map;

/**
 * Created by hzqph on 2017/5/12.
 */
public interface ISupplierApplyService extends IBaseService<SupplierApply, Long> {

    /**
     * 供应商审核分页查询中的列表查询
     * @param map
     * @return
     */
    List<SupplierApply> selectList(Map<String, Object> map);

    /**
     * 供应商审核分页查询中获取记录总数
     * @param map
     * @return
     */
    int selectCount(Map<String, Object> map);

    /**
     * 供应商审核查询单条记录
     * @param id
     * @return
     */
    SupplierApply selectOneById(Long id);
}
