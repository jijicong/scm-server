package org.trc.service.supplier;

import org.trc.domain.supplier.SupplierApplyAudit;
import org.trc.service.IBaseService;

import java.util.List;
import java.util.Map;

/**
 * Created by hzqph on 2017/5/12.
 */
public interface ISupplierApplyAuditService extends IBaseService<SupplierApplyAudit, Long> {

    /**
     * 供应商审核分页查询中的列表查询
     * @param map
     * @return
     */
    List<SupplierApplyAudit> selectList(Map<String, Object> map);

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
    SupplierApplyAudit selectOneById(Long id);

    /**
     * 更新供应商申请审批状态
     * @param map
     * @return
     */
    int updateSupplierApplyAuditStatus(Map<String, Object> map);

}
