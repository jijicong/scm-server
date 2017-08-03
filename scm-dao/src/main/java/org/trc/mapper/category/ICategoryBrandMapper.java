package org.trc.mapper.category;

import org.apache.ibatis.annotations.Param;
import org.trc.domain.category.CategoryBrand;
import org.trc.domain.category.CategoryBrandExt;
import org.trc.util.BaseMapper;

import java.util.List;

/**
 * Created by hzwdx on 2017/5/18.
 */
public interface ICategoryBrandMapper extends BaseMapper<CategoryBrand> {

    /**
     * 根据分类ID列表查询分类品牌
     *
     * @param categoryList
     * @return
     * @throws Exception
     */
    List<CategoryBrandExt> selectCategoryBrands(List<Long> categoryList) throws Exception;

    //删除
    int deleteCategoryBrand(List<CategoryBrand> categoryBrandList) throws Exception;

    /**
     * 更新品牌分类表中品牌停用的级联状态
     * @param isValid
     * @param brandId
     * @return
     * @throws Exception
     */
    Integer updateCategoryBrandIsValid(@Param("isValid")String isValid, @Param("brandId") Long brandId) throws Exception;
}
