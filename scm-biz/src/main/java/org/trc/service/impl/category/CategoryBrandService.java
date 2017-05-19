package org.trc.service.impl.category;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.trc.domain.category.CategoryBrand;
import org.trc.domain.category.CategoryBrandExt;
import org.trc.mapper.category.ICategoryBrandMapper;
import org.trc.service.category.ICategoryBrandService;
import org.trc.service.impl.BaseService;

import java.util.List;

/**
 * Created by hzwdx on 2017/5/18.
 */
@Service("categoryBrandService")
public class CategoryBrandService extends BaseService<CategoryBrand, Long> implements ICategoryBrandService {

    @Autowired
    private ICategoryBrandMapper categoryBrandMapper;

    @Override
    public List<CategoryBrandExt> queryCategoryBrands(List<Long> categoryList) throws Exception{
        return categoryBrandMapper.selectCategoryBrands(categoryList);
    }

}
