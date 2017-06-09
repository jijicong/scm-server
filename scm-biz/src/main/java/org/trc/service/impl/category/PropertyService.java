package org.trc.service.impl.category;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.trc.domain.category.Property;
import org.trc.mapper.category.IPropertyMapper;
import org.trc.service.category.IPropertyService;
import org.trc.service.impl.BaseService;

import java.util.List;

/**
 * Created by hzqph on 2017/5/4.
 */
@Service("propertyService")
public class PropertyService extends BaseService<Property, Long> implements IPropertyService {
    @Autowired
    private IPropertyMapper propertyMapper;

    @Override
    public List<Property> queryPropertyList(List<Long> ids) throws Exception {

        return propertyMapper.queryPropertyList(ids);
    }

    @Override
    public Property selectOneById(Long id) throws Exception {
        return propertyMapper.selectOneById(id);
    }
}
