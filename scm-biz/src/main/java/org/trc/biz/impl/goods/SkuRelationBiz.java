package org.trc.biz.impl.goods;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.trc.biz.goods.ISkuRelationBiz;
import org.trc.domain.goods.ExternalItemSku;
import org.trc.domain.goods.Skus;
import org.trc.service.goods.IExternalItemSkuService;
import org.trc.service.goods.ISkusService;
import org.trc.util.AssertUtil;

/**
 * @author: Ding
 * @mail: hzdzf@tairanchina.com
 * @create: 2017-06-19 16:01
 */
@Service("skuRelationBiz")
public class SkuRelationBiz implements ISkuRelationBiz {

    private Logger logger = LoggerFactory.getLogger(SkuRelationBiz.class);

    @Autowired
    @Qualifier("skusService")
    private ISkusService skusService;

    @Autowired
    @Qualifier("externalItemSkuService")
    private IExternalItemSkuService externalItemSkuService;

    @Override
    public String getSkuInformation(String skuCode) {
        if (skuCode.startsWith("SP0")){
            Skus skus = new Skus();
            skus.setSkuCode(skuCode);
            skus = skusService.selectOne(skus);
            return JSON.toJSONString(skus);
        }
        if (skuCode.startsWith("SP1")){
            ExternalItemSku externalItemSku = new ExternalItemSku();
            externalItemSku.setSkuCode(skuCode);
            externalItemSku = externalItemSkuService.selectOne(externalItemSku);
            return JSON.toJSONString(externalItemSku);
        }
        logger.info("警告，传入错误的skuCode");
        return null;
    }

}
