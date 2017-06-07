package org.trc.service.category;

import org.trc.domain.category.CategoryBrand;
import org.trc.domain.category.CategoryBrandExt;
import org.trc.service.IBaseService;

import java.util.List;

/**
 * Created by hzwdx on 2017/5/18.
 */
public interface ICategoryBrandService extends IBaseService<CategoryBrand, Long>{

    /**
     * 根据分类ID列表查询分类和品牌信息
     * @param categoryList
     * @return
     * @throws Exception
     */
    List<CategoryBrandExt> queryCategoryBrands(List<Long> categoryList) throws Exception;

    int deleteByCategoryId(Long categoryId) throws Exception;
}
