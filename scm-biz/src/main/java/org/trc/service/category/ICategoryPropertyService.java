package org.trc.service.category;

import org.apache.ibatis.annotations.Param;
import org.trc.domain.category.CategoryBrand;
import org.trc.domain.category.CategoryBrandExt;
import org.trc.domain.category.CategoryProperty;
import org.trc.service.IBaseService;

import java.util.List;

/**
 * Created by hzwdx on 2017/5/18.
 */
public interface ICategoryPropertyService extends IBaseService<CategoryProperty, Long>{

    /**
     * 更新排序
     */
    int updateCategoryPropertySort(List<CategoryProperty> categoryPropertyList) throws Exception;
    /**
     * 删除关联的属性
     */
    void deleteCategoryPropertyList (List<CategoryProperty> categoryPropertyList) throws Exception;

    /**
     * 更新属性分类表中品牌停用的级联状态
     * @param isValid
     * @param propertyId
     * @return
     * @throws Exception
     */
    Integer updateCategoryPropertyIsValid(String isValid,Long propertyId) throws Exception;
}
