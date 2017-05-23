package org.trc.service.impl.supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.trc.domain.supplier.SupplierApplyAudit;
import org.trc.mapper.supplier.ISupplierApplyAuditMapper;
import org.trc.service.impl.BaseService;
import org.trc.service.supplier.ISupplierApplyAuditService;

import java.util.List;
import java.util.Map;

/**
 * Created by hzqph on 2017/5/12.
 */
@Service("supplierApplyAuditService")
public class SupplierApplyAuditService extends BaseService<SupplierApplyAudit,Long> implements ISupplierApplyAuditService {

    @Autowired
    private ISupplierApplyAuditMapper mapper;

    @Override
    public List<SupplierApplyAudit> selectList(Map<String, Object> map) {
        return mapper.selectList(map);
    }

    @Override
    public int selectCount(Map<String, Object> map) {
        return mapper.selectSupplierApplyCount(map);
    }

    @Override
    public SupplierApplyAudit selectOneById(Long id) {
        return mapper.selectOneById(id);
    }
}
