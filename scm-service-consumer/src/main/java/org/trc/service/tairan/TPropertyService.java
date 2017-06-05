package org.trc.service.tairan;

import org.trc.domain.category.Property;
import org.trc.domain.category.PropertyValue;
import org.trc.service.tairan.model.ResultModel;

import java.util.List;

/**
 * 泰然城分类回调
 * Created by hzdzf on 2017/5/24.
 */
public interface TPropertyService {

    /**
     * @param action      行为
     * @param oldProperty 旧属性信息
     * @param property    属性信息
     * @param valueList   修改后属性值信息
     * @param operateTime 时间戳
     * @return 渠道调回信息
     * @throws Exception
     */
    ResultModel sendPropertyNotice(String action, Property oldProperty, Property property, List<PropertyValue> valueList, long operateTime) throws Exception;
}
