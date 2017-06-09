package org.trc.service.impl.category;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.trc.domain.category.Category;
import org.trc.mapper.category.ICategoryMapper;
import org.trc.service.category.ICategoryService;
import org.trc.service.impl.BaseService;

import java.util.List;


/**
 * Created by hzszy on 2017/5/5.
 */
@Service("categoryService")
public class CategoryService extends BaseService<Category, Long> implements ICategoryService {
    @Autowired
    private ICategoryMapper categoryMapper;
    @Override
    public int updateCategorySort(List<Category> categoryList) throws Exception {
        return   categoryMapper.updateSort(categoryList);
    }
}
