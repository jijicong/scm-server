package org.trc.biz.goods;

import org.trc.domain.goods.ExternalItemSku;
import org.trc.domain.goods.Skus;

import java.util.List;

/**
 * @author: Ding
 * @mail: hzdzf@tairanchina.com
 * @create: 2017-06-19 16:01
 */
public interface ISkuRelationBiz {

    /**
     * 查询自采商品SKU信息
     * @param skuCode
     * @return
     */
    List<Skus> getSkuInformation(String skuCode);

    /**
     * 查询代发商品SKU信息
     * @param skuCode
     * @return
     */
    List<ExternalItemSku> getExternalSkuInformation(String skuCode,String channelCode);
}
