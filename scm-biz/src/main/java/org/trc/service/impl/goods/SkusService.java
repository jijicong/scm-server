package org.trc.service.impl.goods;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.trc.domain.goods.Skus;
import org.trc.mapper.goods.ISkusMapper;
import org.trc.service.goods.ISkusService;
import org.trc.service.impl.BaseService;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by hzwdx on 2017/5/24.
 */
@Service("skusService")
public class SkusService extends BaseService<Skus, String> implements ISkusService{

    @Autowired
    private ISkusMapper skusMapper;

    @Override
    public Integer updateSkus(List<Skus> skusList) throws Exception {
        return skusMapper.updateSkus(skusList);
    }

    @Override
    public List<Skus> selectSkuList(Map<String, Object> map){
        return skusMapper.selectSkuList(map);
    }

    @Override
    public Integer selectSkuListCount(Map<String, Object> map){
        return skusMapper.selectSkuListCount(map);
    }

    @Override
    public List<String> selectAllBarCode(List<String> notInList) {
        return skusMapper.selectAllBarCode(notInList);
    }

    @Override
    public Set<String> selectSkuListByBarCode(List<String> barCodeList) {
        return skusMapper.selectSkuListByBarCode(barCodeList);
    }


}
