package org.trc.service.impl.supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.trc.domain.supplier.SupplierApply;
import org.trc.mapper.supplier.ISupplierApplyMapper;
import org.trc.service.impl.BaseService;
import org.trc.service.supplier.ISupplierApplyService;

import java.util.List;
import java.util.Map;

/**
 * Created by hzqph on 2017/5/12.
 */
@Service("supplierApplyService")
public class SupplierApplyService extends BaseService<SupplierApply,Long> implements ISupplierApplyService {

    @Autowired
    private ISupplierApplyMapper mapper;

    @Override
    public List<SupplierApply> selectList(Map<String, Object> map) {
        return mapper.selectList(map);
    }

    @Override
    public int selectCount(Map<String, Object> map) {
        return mapper.selectSupplierApplyCount(map);
    }

    @Override
    public SupplierApply selectOneById(Long id) {
        return mapper.selectOneById(id);
    }
}
