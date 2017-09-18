package org.trc.service.impl.trcCategory;

import org.springframework.stereotype.Service;
import org.trc.domain.trcDomain.Categories;
import org.trc.service.impl.BaseService;
import org.trc.service.trcCategory.ITrcCategoriesService;

@Service("trcCategoriesService")
public class TrcCategoriesService extends BaseService<Categories,Integer> implements ITrcCategoriesService {
}
