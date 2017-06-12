package org.trc.service.impl.category;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.trc.domain.category.Brand;
import org.trc.mapper.category.IBrandMapper;
import org.trc.service.category.IBrandService;
import org.trc.service.impl.BaseService;

import java.util.List;

/**
 * Created by hzqph on 2017/4/27.
 */
@Service("brandService")
public class BrandService extends BaseService<Brand, Long> implements IBrandService {

    @Autowired
    private IBrandMapper brandMapper;

    @Override
    public List<Brand> selectBrandList(List<Long> brandIds) throws Exception {
        return brandMapper.selectBrandList(brandIds);
    }

    @Override
    public Brand selectOneById(Long id) throws Exception {
        return brandMapper.selectOneById(id);
    }
}
