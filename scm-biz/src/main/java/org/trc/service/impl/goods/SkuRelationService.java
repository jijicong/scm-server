package org.trc.service.impl.goods;

import org.springframework.stereotype.Service;
import org.trc.domain.goods.ExternalItemSku;
import org.trc.domain.goods.SkuRelation;
import org.trc.domain.order.OrderItem;
import org.trc.mapper.goods.ISkuRelationMapper;
import org.trc.service.goods.ISkuRelationService;
import org.trc.service.impl.BaseService;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author: Ding
 * @mail: hzdzf@tairanchina.com
 * @create: 2017-06-19 15:23
 */
@Service("skuRelationService")
public class SkuRelationService extends BaseService<SkuRelation,Long> implements ISkuRelationService {

    @Resource
    private ISkuRelationMapper skuRelationMapper;

    @Override
    public List<String> selectSupplierCode(List<OrderItem> list) throws Exception {
        return skuRelationMapper.selectSupplierCodeList(list);
    }

    @Override
    public List<String> selectSkuCode(List<ExternalItemSku> list) throws Exception {
        return skuRelationMapper.selectSkuCodeList(list);
    }
}
