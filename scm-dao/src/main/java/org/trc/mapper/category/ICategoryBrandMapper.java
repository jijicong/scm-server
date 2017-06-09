package org.trc.mapper.category;

import org.trc.domain.category.CategoryBrand;
import org.trc.domain.category.CategoryBrandExt;
import org.trc.util.BaseMapper;
import tk.mybatis.mapper.entity.Example;

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
}
