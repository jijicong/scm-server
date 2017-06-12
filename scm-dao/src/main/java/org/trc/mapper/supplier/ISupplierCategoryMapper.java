package org.trc.mapper.supplier;

import org.apache.ibatis.annotations.Param;
import org.trc.domain.category.CategoryBrandExt;
import org.trc.domain.supplier.SupplierCategory;
import org.trc.domain.supplier.SupplierCategoryExt;
import org.trc.util.BaseMapper;

import java.util.List;

/**
 * Created by hzwdx on 2017/5/5.
 */
public interface ISupplierCategoryMapper extends BaseMapper<SupplierCategory>{

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


    Integer updateSupplierCategoryIsValid(@Param("isValid")String isValid, @Param("categoryId")Long categoryId) throws Exception;
}
