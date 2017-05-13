package org.trc.service.impl.category;

import org.springframework.stereotype.Service;
import org.trc.domain.category.Category;
import org.trc.service.category.ICategoryService;
import org.trc.service.impl.BaseService;


/**
 * Created by hzszy on 2017/5/5.
 */
@Service("categoryService")
public class CategoryService extends BaseService<Category, Long> implements ICategoryService {

}
