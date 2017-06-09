package org.trc.service.impl.category;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.trc.domain.category.CategoryBrand;
import org.trc.domain.category.CategoryBrandExt;
import org.trc.domain.category.CategoryProperty;
import org.trc.mapper.category.ICategoryBrandMapper;
import org.trc.mapper.category.ICategoryPropertyMapper;
import org.trc.service.category.ICategoryBrandService;
import org.trc.service.category.ICategoryPropertyService;
import org.trc.service.impl.BaseService;

import java.util.List;

/**
 * Created by hzwdx on 2017/5/18.
 */
@Service("categoryPropertyService")
public class CategoryPropertyService extends BaseService<CategoryProperty, Long> implements ICategoryPropertyService {
    @Autowired
    private ICategoryPropertyMapper categoryPropertyMapper;
    @Override
    public int updateCategoryPropertySort(List<CategoryProperty> categoryPropertyList) throws Exception {
      return   categoryPropertyMapper.updateCategoryPropertySort(categoryPropertyList);
    }

    @Override
    public void deleteCategoryPropertyList(List<CategoryProperty> categoryPropertyList) throws Exception {
        categoryPropertyMapper.deleteCategoryPropertyList(categoryPropertyList);
    }

    @Override
    public Integer updateCategoryPropertyIsValid(String isValid, Long propertyId) throws Exception {
        return categoryPropertyMapper.updateCategoryPropertyIsValid(isValid,propertyId);
    }
}
