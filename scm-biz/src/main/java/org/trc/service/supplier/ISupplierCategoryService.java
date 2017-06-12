package org.trc.service.supplier;

import org.trc.domain.supplier.SupplierCategory;
import org.trc.domain.supplier.SupplierCategoryExt;
import org.trc.service.IBaseService;

import java.util.List;

/**
 * Created by hzwdx on 2017/5/5.
 */
public interface ISupplierCategoryService extends IBaseService<SupplierCategory, Long>{

    /**
     * 根据供应商编码查询供应商分类
     * @param supplierCode
     * @return
     * @throws Exception
     */
    List<SupplierCategoryExt> selectSupplierCategorys(String supplierCode) throws Exception;

    /**
     * 根据列表批量更新供应商分类
     * @param list
     * @return
     * @throws Exception
     */
    Integer updateSupplerCategory(List<SupplierCategory> list) throws Exception;

    Integer updateSupplierCategoryIsValid(String isValid,Long categoryId) throws  Exception;

}
