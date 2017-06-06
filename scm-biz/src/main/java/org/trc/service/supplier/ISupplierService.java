package org.trc.service.supplier;

import org.trc.domain.supplier.Supplier;
import org.trc.service.IBaseService;

import java.util.List;

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

}
