package org.trc.service.impl.trcCategory;

import org.springframework.stereotype.Service;
import org.trc.domain.trcDomain.Brands;
import org.trc.service.impl.BaseService;
import org.trc.service.trcCategory.ITrcBrandsService;

@Service("trcBrandsService")
public class TrcBrandsService extends BaseService<Brands,Integer> implements ITrcBrandsService{
}
