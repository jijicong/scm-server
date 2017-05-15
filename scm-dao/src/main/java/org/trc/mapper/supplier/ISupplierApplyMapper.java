package org.trc.mapper.supplier;

import org.trc.domain.supplier.SupplierApply;
import org.trc.util.BaseMapper;

import java.util.List;
import java.util.Map;

/**
 * Created by hzqph on 2017/5/12.
 */
public interface ISupplierApplyMapper extends BaseMapper<SupplierApply>{

    public List<SupplierApply> querySupplierApplyList(Map<String,Object> map);

    public int queryCountSupplierApply(Map<String,Object> map);
}
