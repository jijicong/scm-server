package org.trc.mapper.category;

import org.trc.domain.category.Category;
import org.trc.util.BaseMapper;

import java.util.List;

/**
 * Created by hzszy on 2017/5/5.
 */
public interface ICategoryMapper extends BaseMapper<Category> {

    int updateSort(List<Category> categoryList) throws Exception;
}
