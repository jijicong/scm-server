package org.trc.mapper.supplier;

import org.apache.ibatis.annotations.SelectProvider;
import org.trc.domain.supplier.SupplierApply;
import org.trc.mapper.builder.SupplierApplyBuildSql;
import org.trc.util.BaseMapper;

import java.util.List;
import java.util.Map;

/**
 * Created by hzqph on 2017/5/12.
 */
public interface ISupplierApplyMapper extends BaseMapper<SupplierApply>{

    @SelectProvider(type = SupplierApplyBuildSql.class, method = "querySupplierApplyList")
    public List<SupplierApply> querySupplierApplyList(Map<String,Object> map);

    @SelectProvider(type = SupplierApplyBuildSql.class, method = "queryCountSupplierApply")
    public int queryCountSupplierApply(Map<String,Object> map);
}
