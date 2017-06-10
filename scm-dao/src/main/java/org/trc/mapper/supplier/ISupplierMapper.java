package org.trc.mapper.supplier;

import org.apache.ibatis.annotations.Param;
import org.trc.domain.supplier.Supplier;
import org.trc.util.BaseMapper;

import java.util.List;

/**
 * Created by hzwdx on 2017/5/5.
 */
public interface ISupplierMapper extends BaseMapper<Supplier>{

    List<Supplier> selectSupplierNames(String strs[]);

    List<Supplier> selectSupplierListByApply(@Param("ids")Long ...ids)throws Exception;

    Integer selectSupplierListCount(@Param("ids")Long ...ids)throws Exception;
}
