package org.trc.mapper.category;

import org.apache.ibatis.annotations.Param;
import org.trc.domain.category.PropertyValue;
import org.trc.util.BaseMapper;

/**
 * Created by hzqph on 2017/5/4.
 */
public interface IPropertyValueMapper extends BaseMapper<PropertyValue> {

    /**
     * 根据属性id更新属性值is_valid字段
     * @param propertyId
     * @return
     * @throws Exception
     */
    Integer updateIsValidByPropertyId(@Param("propertyId")Long propertyId)throws Exception;
}
