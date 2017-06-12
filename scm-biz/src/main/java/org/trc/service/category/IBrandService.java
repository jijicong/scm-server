package org.trc.service.category;

import org.apache.ibatis.annotations.Param;
import org.trc.domain.category.Brand;
import org.trc.service.IBaseService;

import java.util.List;

/**
 * Created by hzqph on 2017/4/27.
 */
public interface IBrandService extends IBaseService<Brand,Long> {

    List<Brand> selectBrandList(List<Long> brandIds) throws Exception;

    /**
     * 根据brandId查询品牌
     * @param id
     * @return
     */
    Brand selectOneById(Long id)throws Exception;
}
