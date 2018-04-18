package org.trc.service.goods;

import org.trc.domain.goods.Skus;
import org.trc.service.IBaseService;

import java.util.List;
import java.util.Map;

/**
 * Created by hzwdx on 2017/5/24.
 */
public interface ISkusService extends IBaseService<Skus, String>{

    Integer updateSkus(List<Skus> skusList) throws Exception;

    List<Skus> selectSkuList(Map<String, Object> map);

    Integer selectSkuListCount(Map<String, Object> map);

    List<String> selectAllBarCode(List<String> notInList);
}
