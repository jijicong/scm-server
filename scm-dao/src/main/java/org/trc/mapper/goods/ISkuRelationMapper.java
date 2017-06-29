package org.trc.mapper.goods;

import org.apache.ibatis.annotations.Param;
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


    List<String> selectSkuCodeList(List<ExternalItemSku> list) throws Exception;

}
