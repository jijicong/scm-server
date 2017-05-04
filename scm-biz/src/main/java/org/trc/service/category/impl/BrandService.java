package org.trc.service.category.impl;

import org.springframework.stereotype.Service;
import org.trc.domain.category.Brand;
import org.trc.service.category.IBrandService;
import org.trc.service.impl.BaseService;

/**
 * Created by hzqph on 2017/4/27.
 */
@Service("brandService")
public class BrandService extends BaseService<Brand,Long> implements IBrandService{

}
