package org.trc.mapper.category;

import org.trc.domain.category.Property;
import org.trc.util.BaseMapper;

import java.util.List;

/**
 * Created by hzqph on 2017/5/4.
 */
public interface IPropertyMapper extends BaseMapper<Property> {

    List<Property> queryPropertyList(List<Long> ids) throws Exception;
}
