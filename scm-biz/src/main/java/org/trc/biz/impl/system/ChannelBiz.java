package org.trc.biz.impl.system;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.trc.biz.system.IChannelBiz;
import org.trc.domain.System.Channel;
import org.trc.enums.ExceptionEnum;
import org.trc.enums.ValidEnum;
import org.trc.enums.ZeroToNineEnum;
import org.trc.exception.ChannelException;
import org.trc.form.system.ChannelForm;
import org.trc.service.System.IChannelService;
import org.trc.service.util.ISerialUtilService;
import org.trc.service.util.IUserNameUtilService;
import org.trc.util.*;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.*;

/**
 * Created by sone on 2017/5/2.
 */
@Service("channelBiz")
public class ChannelBiz implements IChannelBiz {

    private Logger logger = LoggerFactory.getLogger(ChannelBiz.class);

    private final static String  SERIALNAME = "QD";

    private final static Integer LENGTH = 3;

    @Resource
    private IChannelService channelService;

    @Resource
    private IUserNameUtilService userNameUtilService;

    @Resource
    private ISerialUtilService serialUtilService;

    @Override
    public Pagenation<Channel> channelPage(ChannelForm form, Pagenation<Channel> page) throws Exception {
        Example example = new Example(Channel.class);
        Example.Criteria criteria = example.createCriteria();
        if(!StringUtils.isBlank(form.getName())) {
            criteria.andLike("name", "%" + form.getName() + "%");
        }
        if(!StringUtils.isBlank(form.getIsValid())) {
            criteria.andEqualTo("isValid", form.getIsValid());
        }
        example.orderBy("updateTime").desc();
        Pagenation<Channel> pagenation = channelService.pagination(example,page,form);

        List<Channel> channelList = pagenation.getResult();
        userNameUtilService.handleUserName(channelList);
        return pagenation;

    }


    public Channel findChannelByName(String name) throws Exception{
        AssertUtil.notBlank(name, "根据渠道名称查询渠道的参数name为空");
        Channel channel=new Channel();
        channel.setName(name);
        return channelService.selectOne(channel);
    }

    @Override
    public List<Channel> queryChannels(ChannelForm channelForm) throws Exception {
        Channel channel = new Channel();
        if(StringUtils.isEmpty(channelForm.getIsValid())){
            channel.setIsValid(ZeroToNineEnum.ONE.getCode());
        }
        channel.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
        return channelService.select(channel);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void saveChannel(Channel channel) throws Exception {

        AssertUtil.notNull(channel,"渠道管理模块保存仓库信息失败，仓库信息为空");
        Channel tmp = findChannelByName(channel.getName());
        AssertUtil.isNull(tmp,String.format("渠道名称[name=%s]的数据已存在,请使用其他名称",channel.getName()));
        channel.setIsValid(ValidEnum.VALID.getCode()); //渠道状态一直为有效
        ParamsUtil.setBaseDO(channel);
        channel.setCode(serialUtilService.generateCode(LENGTH,SERIALNAME));
        int count = channelService.insert(channel);
        if(count==0){
            String msg = "渠道保存,数据库操作失败";
            logger.error(msg);
            throw new ChannelException(ExceptionEnum.SYSTEM_CHANNEL_SAVE_EXCEPTION, msg);
        }

    }


    @Override
    public void updateChannel(Channel channel) throws Exception {

        AssertUtil.notNull(channel.getId(), "修改渠道参数ID为空");
        Channel tmp = findChannelByName(channel.getName());
        if(tmp!=null){
            if(!tmp.getId().equals(channel.getId())){
                throw new ChannelException(ExceptionEnum.SYSTEM_CHANNEL_UPDATE_EXCEPTION, "其它的渠道已经使用该渠道名称");
            }
        }
        int count = 0;
        channel.setUpdateTime(Calendar.getInstance().getTime());
        count = channelService.updateByPrimaryKeySelective(channel);
        if(count == 0){
            String msg = String.format("修改渠道%s数据库操作失败",JSON.toJSONString(channel));
            logger.error(msg);
            throw new ChannelException(ExceptionEnum.SYSTEM_CHANNEL_UPDATE_EXCEPTION, msg);
        }

    }

    @Override
    public Channel findChannelById(Long id) throws Exception {

        AssertUtil.notNull(id, "根据ID查询渠道明细,参数ID不能为空");
        Channel channel = new Channel();
        channel.setId(id);
        channel = channelService.selectOne(channel);
        AssertUtil.notNull(channel,String.format("根据主键ID[id=%s]查询渠道为空",id.toString()));
        return channel;

    }

    @Override
    public void updateChannelState(Channel channel) throws Exception {

        AssertUtil.notNull(channel,"渠道管理模块修改渠道信息失败，仓库信息为空");
        Channel updateChannel=new Channel();
        updateChannel.setId(channel.getId());
        if (channel.getIsValid().equals(ValidEnum.VALID.getCode())) {
            updateChannel.setIsValid(ValidEnum.NOVALID.getCode());
        } else {
            updateChannel.setIsValid(ValidEnum.VALID.getCode());
        }
        updateChannel.setUpdateTime(Calendar.getInstance().getTime());
        int count=channelService.updateByPrimaryKeySelective(updateChannel);
        if(count == 0){
            String msg = String.format("修改渠道%s数据库操作失败",JSON.toJSONString(channel));
            logger.error(msg);
            throw new ChannelException(ExceptionEnum.SYSTEM_CHANNEL_UPDATE_EXCEPTION, msg);
        }

    }

}
