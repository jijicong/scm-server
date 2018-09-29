package org.trc.service.impl.system;

import org.springframework.stereotype.Service;
import org.trc.domain.System.SellChannel;
import org.trc.service.System.ISellChannelService;
import org.trc.service.impl.BaseService;

import java.util.List;

/**
 * Created by sone on 2017/5/2.
 */
@Service("sellChannelService")
public class SellChannelService extends BaseService<SellChannel,Long> implements ISellChannelService {

    /**
     * 查询所有销售渠道
     * @return
     */
    @Override
    public List<SellChannel> queryAllSellChannel() {
        SellChannel sellChannel = new SellChannel();
        List<SellChannel> sellChannelList = this.select(sellChannel);
        return sellChannelList;
    }

    @Override
    public SellChannel selectSellByCode(String sellCode) {
        SellChannel sellChannel = new SellChannel();
        sellChannel.setSellCode(sellCode);
        return this.selectOne(sellChannel);
    }
}
