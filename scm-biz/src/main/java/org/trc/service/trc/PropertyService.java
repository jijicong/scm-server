package org.trc.service.trc;

import org.trc.domain.category.Property;
import org.trc.domain.category.PropertyValue;

import java.util.List;

/**
 * 泰然城分类回调
 * Created by hzdzf on 2017/5/24.
 */
public interface PropertyService {

    String sendPropertyNotice(Property property, String action, long operateDate, List<PropertyValue> valueList) throws Exception;
}
