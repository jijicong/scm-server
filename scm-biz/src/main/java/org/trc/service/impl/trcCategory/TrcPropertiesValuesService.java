package org.trc.service.impl.trcCategory;

import org.springframework.stereotype.Service;
import org.trc.domain.trcDomain.PropertyValues;
import org.trc.service.impl.BaseService;
import org.trc.service.trcCategory.ITrcPropertiesValuesService;

@Service("trcPropertiesValuesService")
public class TrcPropertiesValuesService extends BaseService<PropertyValues,Integer> implements ITrcPropertiesValuesService {
}
