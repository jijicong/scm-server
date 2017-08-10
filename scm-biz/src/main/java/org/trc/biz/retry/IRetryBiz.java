package org.trc.biz.retry;


/**
 * Created by hzwyz on 2017/8/3 0003.
 */
public interface IRetryBiz {

    void brandUpdateNoticeRetry();

    void propertyUpdateNoticeRetry();

    void categoryUpdateNoticeRetry();

    void categoryBrandUpdateNoticeRetry();

    void categoryPropertyUpdateNoticeRetry();

    void itemUpdateNoticeRetry();

    void externalItemUpdateNoticeRetry();

    void channelReceiveOrderSubmitResultRetry();

    void sendLogisticsInfoToChannelRetry();
}
