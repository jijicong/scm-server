package org.trc.mapper.category;

import org.apache.ibatis.annotations.Param;
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

    /**
     * 根据brandId查询品牌
     * @param id
     * @return
     */
    Brand selectOneById(@Param("brandId")Long id)throws Exception;
}
