package org.trc.service.category;

import org.trc.domain.category.Category;
import org.trc.service.IBaseService;

import java.util.List;

/**
 * Created by hzszy on 2017/5/5.
 */
public interface ICategoryService extends IBaseService<Category, Long> {
    int updateCategorySort(List<Category> categoryList) throws  Exception;

    /**
     * 根据第三级分类，查询该分类的全路径名称组合
     * @param categoryId 分类id
     * @return
     */
    String selectAllCategoryName(Long categoryId);
}
