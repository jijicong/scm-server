package org.trc.service.supplier;

import org.trc.domain.supplier.SupplierApply;
import org.trc.service.IBaseService;
import org.trc.service.impl.BaseService;

import java.util.List;
import java.util.Map;

/**
 * Created by hzqph on 2017/5/12.
 */
public interface ISupplierApplyService extends IBaseService<SupplierApply,Long> {

    public List<SupplierApply> querySupplierApplyList(Map<String,Object> map);

    public int queryCountSupplierApply(Map<String,Object> map);
}
