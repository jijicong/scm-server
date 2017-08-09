package org.trc.biz.impl.goods;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.trc.biz.goods.ISkuRelationBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.goods.ExternalItemSku;
import org.trc.domain.goods.Skus;
import org.trc.enums.ValidEnum;
import org.trc.service.goods.IExternalItemSkuService;
import org.trc.service.goods.ISkusService;
import org.trc.util.AssertUtil;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;
import java.util.List;

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
    public List<Skus> getSkuInformation(String skuCode) {
        AssertUtil.notBlank(skuCode,"查询SKU信息sckCode不能为空");
        String[] skuCodes = skuCode.split(SupplyConstants.Symbol.COMMA);
        for(String _skuCode: skuCodes) {
            AssertUtil.isTrue(_skuCode.startsWith(SupplyConstants.Goods.SKU_PREFIX), String.format("skuCode[%s]不是自采商品", _skuCode));
        }
        Example example = new Example(Skus.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("skuCode", Arrays.asList(skuCodes));
        criteria.andEqualTo("isValid", ValidEnum.VALID.getCode());
        return skusService.selectByExample(example);
    }

    @Override
    public List<ExternalItemSku> getExternalSkuInformation(String skuCode) {
        AssertUtil.notBlank(skuCode,"查询SKU信息sckCode不能为空");
        String[] skuCodes = skuCode.split(SupplyConstants.Symbol.COMMA);
        for(String _skuCode: skuCodes){
            AssertUtil.isTrue(_skuCode.startsWith(SupplyConstants.Goods.EXTERNAL_SKU_PREFIX), String.format("skuCode[%s]不是代发商品", _skuCode));
        }
        Example example = new Example(ExternalItemSku.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("skuCode", Arrays.asList(skuCodes));
        criteria.andEqualTo("isValid", ValidEnum.VALID.getCode());
        return externalItemSkuService.selectByExample(example);
    }

}
