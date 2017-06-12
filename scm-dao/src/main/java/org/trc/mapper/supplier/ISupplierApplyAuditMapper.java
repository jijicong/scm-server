package org.trc.mapper.supplier;

import org.apache.ibatis.annotations.Param;
import org.trc.domain.supplier.SupplierApplyAudit;
import org.trc.util.BaseMapper;

import java.util.List;
import java.util.Map;

/**
 * Created by hzqph on 2017/5/12.
 */
public interface ISupplierApplyAuditMapper extends BaseMapper<SupplierApplyAudit> {

    List<SupplierApplyAudit> selectList(Map<String, Object> map);

    int selectSupplierApplyAuditCount(Map<String, Object> map);

    SupplierApplyAudit selectOneById(@Param(value="id")Long id);

    /**
     * 更新供应商申请审批状态
     * @param param
     * @return
     */
    int updateSupplierApplyAuditStatus(Map<String, Object> param);

}
