package org.trc.mapper.category;

import org.apache.ibatis.annotations.Select;
import org.trc.domain.category.Category;
import org.trc.util.BaseMapper;

/**
 * Created by hzszy on 2017/5/5.
 */
public interface ICategoryMapper extends BaseMapper<Category> {
    @Select("select MAX(id) from category")
    public Integer queryCount();
}
