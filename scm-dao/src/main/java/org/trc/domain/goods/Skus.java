package org.trc.domain.goods;

import org.trc.domain.util.ScmDO;

import java.util.Date;

public class Skus  extends ScmDO {
    private String skuCode;

    private Long itemId;

    private String spuCode;

    private String propertyValueId;

    private String propertyValue;

    private String barCode;

    private Long marketPrice;

    private Long predictChannelPrice;

    private String picture;

    private Long channel1PreSellPrices;

    private Long channel2PreSellPrices;

    private Long channel3PreSellPrices;

    private Long channel4PreSellPrices;

    private Long channel5PreSellPrices;

    private Long channel6PreSellPrices;

    private Long channel7PreSellPrices;


    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode == null ? null : skuCode.trim();
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public String getSpuCode() {
        return spuCode;
    }

    public void setSpuCode(String spuCode) {
        this.spuCode = spuCode == null ? null : spuCode.trim();
    }

    public String getPropertyValueId() {
        return propertyValueId;
    }

    public void setPropertyValueId(String propertyValueId) {
        this.propertyValueId = propertyValueId == null ? null : propertyValueId.trim();
    }

    public String getPropertyValue() {
        return propertyValue;
    }

    public void setPropertyValue(String propertyValue) {
        this.propertyValue = propertyValue == null ? null : propertyValue.trim();
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode == null ? null : barCode.trim();
    }

    public Long getMarketPrice() {
        return marketPrice;
    }

    public void setMarketPrice(Long marketPrice) {
        this.marketPrice = marketPrice;
    }

    public Long getPredictChannelPrice() {
        return predictChannelPrice;
    }

    public void setPredictChannelPrice(Long predictChannelPrice) {
        this.predictChannelPrice = predictChannelPrice;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture == null ? null : picture.trim();
    }

    public Long getChannel1PreSellPrices() {
        return channel1PreSellPrices;
    }

    public void setChannel1PreSellPrices(Long channel1PreSellPrices) {
        this.channel1PreSellPrices = channel1PreSellPrices;
    }

    public Long getChannel2PreSellPrices() {
        return channel2PreSellPrices;
    }

    public void setChannel2PreSellPrices(Long channel2PreSellPrices) {
        this.channel2PreSellPrices = channel2PreSellPrices;
    }

    public Long getChannel3PreSellPrices() {
        return channel3PreSellPrices;
    }

    public void setChannel3PreSellPrices(Long channel3PreSellPrices) {
        this.channel3PreSellPrices = channel3PreSellPrices;
    }

    public Long getChannel4PreSellPrices() {
        return channel4PreSellPrices;
    }

    public void setChannel4PreSellPrices(Long channel4PreSellPrices) {
        this.channel4PreSellPrices = channel4PreSellPrices;
    }

    public Long getChannel5PreSellPrices() {
        return channel5PreSellPrices;
    }

    public void setChannel5PreSellPrices(Long channel5PreSellPrices) {
        this.channel5PreSellPrices = channel5PreSellPrices;
    }

    public Long getChannel6PreSellPrices() {
        return channel6PreSellPrices;
    }

    public void setChannel6PreSellPrices(Long channel6PreSellPrices) {
        this.channel6PreSellPrices = channel6PreSellPrices;
    }

    public Long getChannel7PreSellPrices() {
        return channel7PreSellPrices;
    }

    public void setChannel7PreSellPrices(Long channel7PreSellPrices) {
        this.channel7PreSellPrices = channel7PreSellPrices;
    }

}