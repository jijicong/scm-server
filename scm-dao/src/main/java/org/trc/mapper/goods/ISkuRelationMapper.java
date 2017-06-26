package org.trc.mapper.goods;

import org.trc.domain.goods.ExternalItemSku;
import org.trc.domain.goods.SkuRelation;
import org.trc.domain.order.OrderItem;
import org.trc.util.BaseMapper;

import java.util.List;

/**
 * @author: Ding
 * @mail: hzdzf@tairanchina.com
 * @create: 2017-06-19 15:17
 */
public interface ISkuRelationMapper extends BaseMapper<SkuRelation> {

    //获取仓库的skuCode
    List<String> selectSupplierCodeList(List<OrderItem> list) throws Exception;

    List<String> selectSkuCodeList(List<ExternalItemSku> list) throws Exception;

}
