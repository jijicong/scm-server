package org.trc.biz.system;

import org.trc.domain.System.Channel;
import org.trc.form.system.ChannelForm;
import org.trc.util.Pagenation;

import java.util.List;

/**
 * Created by sone on 2017/5/2.
 */
public interface IChannelBiz {

    /**
     * 分页查询渠道信息
     * @param form  渠道查询条件
     * @param page  分页信息
     * @return  分页信息及当前页面的数据
     */
    Pagenation<Channel> channelPage(ChannelForm form,Pagenation<Channel> page) throws Exception;

    /**
     * 根据渠道的name查询
     * @param name 渠道
     * @return  渠道
     */
    Channel findChannelByName(String name) throws Exception;
    /**
     *保存渠道
     * @return 整数改变
     */
    void saveChannel(Channel channel) throws Exception;

    /**
     * 更新渠道
     * @param channel 信息
     * @param id 主键
     * @return 整数改变
     * @throws Exception
     */
    void updateChannel(Channel channel) throws Exception;

    /**
     * 根据id查询渠道
     * @param id 主键
     * @return 渠道实例
     * @throws Exception
     */
    Channel findChannelById(Long id) throws Exception;

    /**
     * 渠道状态修改
     * @param channel
     * @return
     * @throws Exception
     */
    void updateChannelState(Channel channel) throws Exception;


}
