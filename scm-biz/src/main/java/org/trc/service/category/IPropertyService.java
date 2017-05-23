package org.trc.service.category;

import org.trc.domain.category.Property;
import org.trc.service.IBaseService;

import java.util.List;

/**
 * Created by hzqph on 2017/5/4.
 */
public interface IPropertyService extends IBaseService<Property, Long> {
    /**
     * 查询关联属性集合
     * @param id
     * @return
     * @throws Exception
     */
    List<Property> queryPropertyList(List<Long> id) throws Exception;
}
