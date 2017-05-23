package org.trc.mapper.category;

import org.trc.domain.category.Brand;
import org.trc.util.BaseMapper;

import java.util.List;

/**
 * Created by hzqph on 2017/4/27.
 */
public interface IBrandMapper extends BaseMapper<Brand>{
    /**
     *根据brandIds，查询对应的品牌
     */
    List<Brand> selectBrandList(List<Long> brandIds) throws Exception;
}
