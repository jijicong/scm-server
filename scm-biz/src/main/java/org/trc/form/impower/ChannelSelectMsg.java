package org.trc.form.impower;

import java.util.List;

/**
 * @author hzszy
 */
public class ChannelSelectMsg {
    private String channelName;
    private  String channelCode;
    private List<SellChannelSelectMsg> sellChannelList;

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getChannelCode() {
        return channelCode;
    }

    public void setChannelCode(String channelCode) {
        this.channelCode = channelCode;
    }

    public List<SellChannelSelectMsg> getSellChannelList() {
        return sellChannelList;
    }

    public void setSellChannelList(List<SellChannelSelectMsg> sellChannelList) {
        this.sellChannelList = sellChannelList;
    }
}
