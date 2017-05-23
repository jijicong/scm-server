package org.trc.service.supplier;

import org.trc.domain.supplier.SupplierApply;
import org.trc.domain.supplier.SupplierApplyAudit;

import java.util.List;
import java.util.Map;

/**
 * Created by hzqph on 2017/5/23.
 */
public interface ISupplierApplyService {

    /**
     * 供应商审核分页查询中的列表查询
     *
     * @param map
     * @return
     */
    List<SupplierApply> selectList(Map<String, Object> map);

    /**
     * 供应商审核分页查询中获取记录总数
     *
     * @param map
     * @return
     */
    int selectCount(Map<String, Object> map);

    /**
     * 供应商审核查询单条记录
     *
     * @param id
     * @return
     */
    SupplierApply selectOneById(Long id);
}
