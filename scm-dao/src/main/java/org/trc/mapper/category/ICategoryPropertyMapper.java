package org.trc.mapper.category;

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
    void updateCategoryPropertySort(List<CategoryProperty> categoryPropertyList) throws Exception;
    /**
     * 删除关联的属性
     */
    void deleteCategoryPropertyList (List<CategoryProperty> categoryPropertyList) throws Exception;
}
