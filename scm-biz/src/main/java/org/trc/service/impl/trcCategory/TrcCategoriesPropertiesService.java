package org.trc.service.impl.trcCategory;

import org.springframework.stereotype.Service;
import org.trc.domain.trcDomain.CategoryPropertyRels;
import org.trc.service.impl.BaseService;
import org.trc.service.trcCategory.ITrcCategoriesPropertiesService;

@Service("trcCategoriesPropertiesService")
public class TrcCategoriesPropertiesService extends BaseService<CategoryPropertyRels,Integer> implements ITrcCategoriesPropertiesService {
}
