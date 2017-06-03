package org.trc.mapper.goods;

import org.trc.domain.goods.Skus;
import org.trc.util.BaseMapper;

import java.util.List;

/**
 * Created by hzwdx on 2017/5/24.
 */
public interface ISkusMapper extends BaseMapper<Skus>{

    Integer updateSkus(List<Skus> skusList) throws Exception;

}
