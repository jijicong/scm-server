package org.trc.service.impl.category;

import org.springframework.stereotype.Service;
import org.trc.domain.category.Category;
import org.trc.mapper.category.ICategoryMapper;
import org.trc.service.category.ICategoryService;
import org.trc.service.impl.BaseService;

import javax.annotation.Resource;

/**
 * Created by hzszy on 2017/5/5.
 */
@Service("categoryService")
public class CategoryService extends BaseService<Category,Long> implements ICategoryService{
    @Resource
    private ICategoryMapper categoryMapper;
    public  int queryCategoryCount(){
        return  categoryMapper.queryCount();
    }
}
