package org.trc.service.category;

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
    void updateCategoryPropertySort(List<CategoryProperty> categoryPropertyList) throws Exception;
    /**
     * 删除关联的属性
     */
    void deleteCategoryPropertyList (List<CategoryProperty> categoryPropertyList) throws Exception;
}
