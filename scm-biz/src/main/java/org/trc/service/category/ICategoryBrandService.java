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

    /**
     * 逻辑删除
     * @param categoryBrandList
     * @return
     * @throws Exception
     */
    int deleteCategoryBrand(List<CategoryBrand> categoryBrandList) throws Exception;

    /**
     * 更新品牌分类表中品牌停用的级联状态
     * @param isValid
     * @param brandId
     * @return
     * @throws Exception
     */
    Integer updateCategoryBrandIsValid(String isValid,Long brandId) throws Exception;
}
