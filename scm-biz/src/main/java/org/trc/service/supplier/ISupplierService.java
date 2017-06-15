package org.trc.service.supplier;

import org.apache.ibatis.annotations.Param;
import org.trc.domain.supplier.Supplier;
import org.trc.service.IBaseService;

import java.util.List;
import java.util.Map;

/**
 * Created by hzwdx on 2017/5/5.
 */
public interface ISupplierService extends IBaseService<Supplier, Long>{
    /**
     * 根据供应商的编码查询供应商
     * @param strs
     * @return
     */
    List<Supplier> selectSupplierNames(String strs[]);

    /**
     * 申请供应商时的供应商分页查询
     * @param map
     * @return
     * @throws Exception
     */
    List<Supplier> selectSupplierListByApply(Map<String,Object> map)throws Exception;

    Integer selectSupplierListCount(Map<String,Object> map)throws Exception;
}
