package org.trc.domain.System;

import java.util.List;

/**
 *
 * @author szy
 * @date 2017/11/15
 */
public class ChannelExt extends Channel{
    /**
     * 关联的销售渠道
     */
    private List<SellChannel> sellChannelList;

    public List<SellChannel> getSellChannelList() {
        return sellChannelList;
    }

    public void setSellChannelList(List<SellChannel> sellChannelList) {
        this.sellChannelList = sellChannelList;
    }
}
