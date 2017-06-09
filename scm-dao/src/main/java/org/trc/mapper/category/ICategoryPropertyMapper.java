package org.trc.mapper.category;

import org.apache.ibatis.annotations.Param;
import org.trc.domain.category.CategoryProperty;
import org.trc.util.BaseMapper;

import java.util.List;

/**
 * Created by hzSzy 2017/5/18.
 */
public interface ICategoryPropertyMapper extends BaseMapper<CategoryProperty> {
    /**
     * 更新排序
     */
    int updateCategoryPropertySort(List<CategoryProperty> categoryPropertyList) throws Exception;

    /**
     * 删除关联的属性
     */
    void deleteCategoryPropertyList(List<CategoryProperty> categoryPropertyList) throws Exception;

    /**
     * 更新属性分类表中品牌停用的级联状态
     *
     * @param isValid
     * @param propertyId
     * @return
     * @throws Exception
     */
    Integer updateCategoryPropertyIsValid(@Param("isValid") String isValid, @Param("propertyId") Long propertyId) throws Exception;
}
