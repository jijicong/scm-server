package org.trc.service.impl.goods;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.trc.domain.goods.Skus;
import org.trc.mapper.goods.ISkusMapper;
import org.trc.service.goods.ISkusService;
import org.trc.service.impl.BaseService;

import java.util.List;

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
}
