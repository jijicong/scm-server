package org.trc.biz.impl.order;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.trc.biz.order.IOrderExtBiz;
import org.trc.domain.System.SellChannel;
import org.trc.domain.order.OrderBaseDO;
import org.trc.service.impl.system.SellChannelService;
import org.trc.util.Pagenation;
import org.trc.util.cache.SupplierOrderCacheEvict;
import tk.mybatis.mapper.entity.Example;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service("orderExtBiz")
public class OrderExtBiz implements IOrderExtBiz {

    @Autowired
    private SellChannelService sellChannelService;

    @Override
    @SupplierOrderCacheEvict
    public void cleanOrderCache() {

    }

    @Override
    public void setOrderSellName(Pagenation pagenation) {
        if(null == pagenation){
            return;
        }
        if(CollectionUtils.isEmpty(pagenation.getResult())){
            return;
        }
        List<OrderBaseDO> orderBaseDOList = (List<OrderBaseDO>)pagenation.getResult();
        Set<String> sellCodes = new HashSet<String>();
        for(OrderBaseDO baseDO: orderBaseDOList){
            sellCodes.add(baseDO.getSellCode());
        }
        Example example = new Example(SellChannel.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("sellCode", sellCodes);
        List<SellChannel> sellChannelList = sellChannelService.selectByExample(example);
        for(OrderBaseDO orderBaseDO: orderBaseDOList){
            for(SellChannel sellChannel: sellChannelList){
                if(StringUtils.equals(orderBaseDO.getSellCode(), sellChannel.getSellCode())){
                    orderBaseDO.setSellName(sellChannel.getSellName());
                    break;
                }
            }
        }
    }


}
