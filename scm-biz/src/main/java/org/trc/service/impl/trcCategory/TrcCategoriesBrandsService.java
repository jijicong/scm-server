package org.trc.service.impl.trcCategory;

import org.springframework.stereotype.Service;
import org.trc.domain.trcDomain.CategoryBrandRels;
import org.trc.service.impl.BaseService;
import org.trc.service.trcCategory.ITrcCategoriesBrandsService;

@Service("trcCategoriesBrandsService")
public class TrcCategoriesBrandsService extends BaseService<CategoryBrandRels,Integer> implements ITrcCategoriesBrandsService {
}
