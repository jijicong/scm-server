package org.trc.mapper.supplier;

import org.trc.domain.supplier.Supplier;
import org.trc.util.BaseMapper;

import java.util.List;
import java.util.Map;

/**
 * Created by hzwdx on 2017/5/5.
 */
public interface ISupplierMapper extends BaseMapper<Supplier>{

    List<Supplier> selectSupplierNames(String strs[]);

    List<Supplier> selectSupplierListByApply(Map<String,Object> map)throws Exception;

    Integer selectSupplierListCount(Map<String,Object> map)throws Exception;

    List<Supplier> selectSupplierByName(String name);

}
