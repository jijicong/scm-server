package org.trc.service.category;

import org.trc.domain.category.Category;

import org.trc.service.IBaseService;

/**
 * Created by hzszy on 2017/5/5.
 */
public interface ICategoryService extends IBaseService<Category,Long> {
    public int queryCategoryCount();

}
