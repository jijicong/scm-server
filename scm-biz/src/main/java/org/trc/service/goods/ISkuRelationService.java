package org.trc.service.goods;

import org.trc.domain.goods.ExternalItemSku;
import org.trc.domain.goods.SkuRelation;
import org.trc.service.IBaseService;

import java.util.List;

/**
 * @author: Ding
 * @mail: hzdzf@tairanchina.com
 * @create: 2017-06-19 15:21
 */
public interface ISkuRelationService extends IBaseService<SkuRelation, Long> {

    List<String> selectSkuCode(List<ExternalItemSku> list) throws Exception;

}
