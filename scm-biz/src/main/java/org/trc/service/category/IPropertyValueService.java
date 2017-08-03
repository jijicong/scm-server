package org.trc.service.category;

import org.trc.domain.category.PropertyValue;
import org.trc.service.IBaseService;

/**
 * Created by hzqph on 2017/5/4.
 */
public interface IPropertyValueService extends IBaseService<PropertyValue,Long> {

    /**
     * 根据属性id更新属性值is_valid字段
     * @param propertyId
     * @return
     * @throws Exception
     */
    Integer updateIsValidByPropertyId(Long propertyId)throws Exception;
}
