package org.trc.biz.impl.goods;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.trc.biz.goods.ISkuBiz;
import org.trc.domain.goods.Skus;
import org.trc.form.goods.SkusForm;
import org.trc.service.goods.ISkusService;
import org.trc.util.Pagenation;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Map;

/**
 * Created by hzdzf on 2017/5/27.
 */
@Service("skuBiz")
public class SkuBiz implements ISkuBiz {

    private static final Logger logger = LoggerFactory.getLogger(SkuBiz.class);

    @Resource
    private ISkusService skusService;

    @Override
    public Skus findByItemId(long itemId) throws Exception {
        Skus skus = new Skus();
        skus.setItemId(itemId);
        return skusService.selectOne(skus);
    }

    @Override
    public Pagenation<Skus> skusPage(SkusForm form, Pagenation<Skus> page) throws Exception {
        logger.info("查询条件： "+ JSON.toJSONString(form));
        Example example = new Example(Skus.class);
        Example.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(form.getSpuCode())){
            criteria.andEqualTo("spuCode",form.getSpuCode());
        }
        if (StringUtils.isNotBlank(form.getSkuCode())){
            criteria.andEqualTo("skuCode",form.getSkuCode());
        }
        example.orderBy("spuCode").desc();
        return skusService.pagination(example,page,form);
    }
}