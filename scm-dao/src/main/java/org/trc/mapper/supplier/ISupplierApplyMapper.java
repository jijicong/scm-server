package org.trc.mapper.supplier;

import org.apache.ibatis.annotations.Param;
import org.trc.domain.supplier.SupplierApply;
import org.trc.util.BaseMapper;

import java.util.List;
import java.util.Map;

/**
 * Created by hzqph on 2017/5/12.
 */
public interface ISupplierApplyMapper extends BaseMapper<SupplierApply> {

    List<SupplierApply> selectList(Map<String, Object> map);

    int selectSupplierApplyCount(Map<String, Object> map);

    SupplierApply selectOneById(@Param(value="id")Long id);
}
