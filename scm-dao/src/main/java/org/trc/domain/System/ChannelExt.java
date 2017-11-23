package org.trc.domain.System;

import com.alibaba.fastjson.JSONObject;

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

    private JSONObject nameValue;

    public void setSellChannelList(List<SellChannel> sellChannelList) {
        this.sellChannelList = sellChannelList;
    }

    public JSONObject getNameValue() {
        return nameValue;
    }

    public void setNameValue(JSONObject nameValue) {
        this.nameValue = nameValue;
    }
}
