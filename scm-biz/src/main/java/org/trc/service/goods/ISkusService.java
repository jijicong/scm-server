package org.trc.service.goods;

import org.trc.domain.goods.Skus;
import org.trc.service.IBaseService;

import java.util.List;

/**
 * Created by hzwdx on 2017/5/24.
 */
public interface ISkusService extends IBaseService<Skus, String>{

    Integer updateSkus(List<Skus> skusList) throws Exception;

}
