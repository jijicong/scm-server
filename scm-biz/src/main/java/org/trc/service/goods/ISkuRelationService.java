package org.trc.service.goods;

import org.trc.domain.goods.ExternalItemSku;
import org.trc.domain.goods.SkuRelation;
import org.trc.domain.order.OrderItem;
import org.trc.service.IBaseService;

import java.util.List;

/**
 * @author: Ding
 * @mail: hzdzf@tairanchina.com
 * @create: 2017-06-19 15:21
 */
public interface ISkuRelationService extends IBaseService<SkuRelation,Long> {

    //获取仓库的skuCode
    List<String> selectSupplierCode(List<OrderItem> list) throws Exception;


    List<String> selectSkuCode(List<ExternalItemSku> list) throws Exception;

}
