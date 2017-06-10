package org.trc.service.impl.category;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.trc.domain.category.PropertyValue;
import org.trc.mapper.category.IPropertyValueMapper;
import org.trc.service.category.IPropertyValueService;
import org.trc.service.impl.BaseService;

/**
 * Created by hzqph on 2017/5/4.
 */
@Service("propertyValueService")
public class PropertyValueService extends BaseService<PropertyValue,Long> implements IPropertyValueService {

    @Autowired
    private IPropertyValueMapper propertyValueMapper;

    @Override
    public Integer updateIsValidByPropertyId(Long propertyId) throws Exception {
        return propertyValueMapper.updateIsValidByPropertyId(propertyId);
    }
}
