package org.trc.service.category.impl;

import org.springframework.stereotype.Service;
import org.trc.domain.category.Property;
import org.trc.service.category.IPropertyService;
import org.trc.service.impl.BaseService;

/**
 * Created by hzqph on 2017/5/4.
 */
@Service("propertyService")
public class PropertyService extends BaseService<Property,Long> implements IPropertyService {
}
