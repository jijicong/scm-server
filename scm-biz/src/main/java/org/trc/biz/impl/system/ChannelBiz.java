package org.trc.biz.impl.system;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.trc.biz.system.IChannelBiz;
import org.trc.biz.system.IWarehouseBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.System.Channel;
import org.trc.domain.System.ChannelExt;
import org.trc.domain.System.ChannelSellChannel;
import org.trc.domain.System.SellChannel;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.warehouseInfo.WarehouseInfo;
import org.trc.enums.*;
import org.trc.exception.ChannelException;
import org.trc.form.system.ChannelForm;
import org.trc.model.SearchResult;
import org.trc.service.IPageNationService;
import org.trc.service.System.IChannelSellChannelService;
import org.trc.service.System.IChannelService;
import org.trc.service.System.ISellChannelService;
import org.trc.service.config.ILogInfoService;
import org.trc.service.util.ISerialUtilService;
import org.trc.service.util.IUserNameUtilService;
import org.trc.service.warehouseInfo.IWarehouseInfoService;
import org.trc.util.AssertUtil;
import org.trc.util.Pagenation;
import org.trc.util.ParamsUtil;
import org.trc.util.TransportClientUtil;
import org.trc.util.cache.ChannelCacheEvict;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.*;

/**
 *
 * @author sone
 * @date 2017/5/2
 */
@Service("channelBiz")
public class ChannelBiz implements IChannelBiz {

    /**
     * 原渠道修该为业务线,流水编码方式修改
     * private final static String SERIALNAME = "QD";
     */
    //private final static String SERIALNAME ="YWX";
    private final static String SERIALNAME = "QD";
    private final static Integer LENGTH = 3;
    private Logger logger = LoggerFactory.getLogger(ChannelBiz.class);
    @Autowired
    private IChannelService channelService;

    @Autowired
    private IUserNameUtilService userNameUtilService;

    @Autowired
    private ISerialUtilService serialUtilService;

    @Autowired
    private IPageNationService pageNationService;
    @Autowired
    private ILogInfoService logInfoService;

    @Autowired
    private ISellChannelService sellChannelService;
    @Autowired
    private IChannelSellChannelService channelSellChannelService;
    @Autowired
    private IWarehouseInfoService warehouseInfoService;



    @Override
    @Cacheable(value = SupplyConstants.Cache.CHANNEL)
    public Pagenation<Channel> channelPage(ChannelForm form, Pagenation<Channel> page) {
        Example example = new Example(Channel.class);
        Example.Criteria criteria = example.createCriteria();
        if (!StringUtils.isBlank(form.getName())) {
            criteria.andLike("name", "%" + form.getName() + "%");
        }
        if (!StringUtils.isBlank(form.getIsValid())) {
            criteria.andEqualTo("isValid", form.getIsValid());
        }
        example.orderBy("updateTime").desc();
        Pagenation<Channel> pagenation = channelService.pagination(example, page, form);

        List<Channel> channelList = pagenation.getResult();
        handleSellChannel(channelList);
        userNameUtilService.handleUserName(channelList);
        
        return pagenation;

    }

    private void handleSellChannel( List<Channel> channelList) {
        for (Channel channel:channelList) {
            ChannelSellChannel channelSellChannel  = new ChannelSellChannel();
            channelSellChannel.setChannelId(channel.getId());
            List<ChannelSellChannel> channelSellChannelList=  channelSellChannelService.select(channelSellChannel);
            List<Long> sellChannelIdList =  new ArrayList<>();
            for (ChannelSellChannel sellChannel:channelSellChannelList){
                sellChannelIdList.add(sellChannel.getSellChannelId());
            }
            if (!AssertUtil.collectionIsEmpty(sellChannelIdList)){
            Example example = new Example(SellChannel.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andIn("id",sellChannelIdList);
            List<SellChannel> sellChannelList = sellChannelService.selectByExample(example);
            List<String> sellChannelNameList = new ArrayList<>();
            List<String> sellChannelCodeList = new ArrayList<>();
            for (SellChannel sellChannel:sellChannelList) {
                sellChannelNameList.add(sellChannel.getSellName());
                sellChannelCodeList.add(sellChannel.getSellCode());
            }
            channel.setSellChannelName(StringUtils.join(sellChannelNameList,SupplyConstants.Symbol.COMMA));
            channel.setSellChannel(StringUtils.join(sellChannelCodeList,SupplyConstants.Symbol.COMMA));
            }
        }
    }


    @Override
    @Cacheable(value = SupplyConstants.Cache.CHANNEL)
    public Channel findChannelByName(String name) {

        AssertUtil.notBlank(name, "根据业务线名称查询业务线的参数name为空");
        Channel channel = new Channel();
        channel.setName(name);
        return channelService.selectOne(channel);

    }

    @Override
    //@Cacheable(key = "#channelForm", isList = true)
    @Cacheable(value = SupplyConstants.Cache.CHANNEL)
    public List<Channel> queryChannels(ChannelForm channelForm) {//查询有效的渠道
        Channel channel = new Channel();
        if (StringUtils.isEmpty(channelForm.getIsValid())) {
            channel.setIsValid(ZeroToNineEnum.ONE.getCode());
        }
        channel.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
        return channelService.select(channel);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @ChannelCacheEvict
    public void saveChannel(Channel channel, AclUserAccreditInfo aclUserAccreditInfo) {
       List<SellChannel> sellChannelList =  checkSellChannel(channel);
        AssertUtil.notNull(channel, "业务线信息为空");
        Channel tmp = findChannelByName(channel.getName());
        AssertUtil.isNull(tmp, String.format("业务线名称[name=%s]的数据已存在,请使用其他名称", channel.getName()));
        channel.setIsValid(ValidEnum.VALID.getCode());
        ParamsUtil.setBaseDO(channel);
        channel.setCode(serialUtilService.generateCode(LENGTH, SERIALNAME));
        int count = channelService.insert(channel);
        if (count == 0) {
            String msg = "业务线保存,数据库操作失败";
            logger.error(msg);
            throw new ChannelException(ExceptionEnum.SYSTEM_CHANNEL_SAVE_EXCEPTION, msg);
        }
        //关联表保存
        AddLinkChannelSellChannel(channel, sellChannelList);
        //记录日志
        String userId = aclUserAccreditInfo.getUserId();
        logInfoService.recordLog(channel, channel.getId().toString(), userId, LogOperationEnum.ADD.getMessage(), "新增业务线", null);
    }


    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @ChannelCacheEvict
    public void updateChannel(Channel channel, AclUserAccreditInfo aclUserAccreditInfo) {

        AssertUtil.notNull(channel.getId(), "修改业务线参数ID为空");
       List<SellChannel> sellChannelList = checkSellChannel(channel);
        Channel tmp = findChannelByName(channel.getName());
        if (tmp != null) {
            if (!tmp.getId().equals(channel.getId())) {
                throw new ChannelException(ExceptionEnum.SYSTEM_CHANNEL_UPDATE_EXCEPTION, "其它的渠道已经使用该渠道名称");
            }
        }
        int count = 0;
        channel.setUpdateTime(Calendar.getInstance().getTime());
        count = channelService.updateByPrimaryKeySelective(channel);
        if (count == 0) {
            String msg = String.format("修改渠道%s数据库操作失败", JSON.toJSONString(channel));
            logger.error(msg);
            throw new ChannelException(ExceptionEnum.SYSTEM_CHANNEL_UPDATE_EXCEPTION, msg);
        }
        //修改关联表
        //1.清除当前业务线ID的关联信息
        channel = channelService.selectByPrimaryKey(channel.getId());
        Example example = new Example(ChannelSellChannel.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("channelId",channel.getId());
        channelSellChannelService.deleteByExample(example);
        //2.添加新的关联信息
        AddLinkChannelSellChannel(channel, sellChannelList);
        //记录日志
        String userId = aclUserAccreditInfo.getUserId();
        logInfoService.recordLog(channel, channel.getId().toString(), userId, LogOperationEnum.UPDATE.getMessage(), null, null);

    }

    /**
     * 添加关联数据
     * @param channel
     * @param sellChannelList
     */
    private void AddLinkChannelSellChannel(Channel channel, List<SellChannel> sellChannelList) {
        List<ChannelSellChannel> channelSellChannels = new ArrayList<>();
        for (SellChannel sellChannel:sellChannelList) {
            ChannelSellChannel channelSellChannel = new ChannelSellChannel();
            channelSellChannel.setChannelCode(channel.getCode());
            channelSellChannel.setChannelId(channel.getId());
            channelSellChannel.setSellChannelCode(sellChannel.getSellCode());
            channelSellChannel.setSellChannelId(sellChannel.getId());
            channelSellChannels.add(channelSellChannel);
        }
        if (!AssertUtil.collectionIsEmpty(channelSellChannels)){
            int count2 =  channelSellChannelService.insertList(channelSellChannels);
            if (count2 == 0) {
                String msg = "业务线关联销售渠道,数据库操作失败";
                logger.error(msg);
                throw new ChannelException(ExceptionEnum.SYSTEM_CHANNEL_SAVE_EXCEPTION, msg);
            }
        }
    }

    @Override
    //@Cacheable(key = "#id")
    @Cacheable(value = SupplyConstants.Cache.CHANNEL)
    public Channel findChannelById(Long id) {

        AssertUtil.notNull(id, "根据ID查询业务线明细,参数ID不能为空");
        Channel channel = new Channel();
        channel.setId(id);
        channel = channelService.selectOne(channel);
        AssertUtil.notNull(channel, String.format("根据主键ID[%s]查询业务线为空", id.toString()));
        return channel;

    }

    @Override
    @ChannelCacheEvict
    public void updateChannelState(Channel channel) {

        AssertUtil.notNull(channel, "业务线管理模块修改业务线信息失败，业务线信息为空");
        Channel updateChannel = new Channel();
        updateChannel.setId(channel.getId());
        if (channel.getIsValid().equals(ValidEnum.VALID.getCode())) {
            updateChannel.setIsValid(ValidEnum.NOVALID.getCode());
        } else {
            updateChannel.setIsValid(ValidEnum.VALID.getCode());
        }
        updateChannel.setUpdateTime(Calendar.getInstance().getTime());
        int count = channelService.updateByPrimaryKeySelective(updateChannel);
        if (count == 0) {
            String msg = String.format("修改渠道%s数据库操作失败", JSON.toJSONString(channel));
            logger.error(msg);
            throw new ChannelException(ExceptionEnum.SYSTEM_CHANNEL_UPDATE_EXCEPTION, msg);
        }

    }

    @Override
    @Cacheable(value = SupplyConstants.Cache.CHANNEL)
    public List<SellChannel> selectLinkSellChannelById(Long channelId) {
        AssertUtil.notNull(channelId,"查询条件业务线主键Id不能为空!");
        findChannelById(channelId);
        //查询关联表
        ChannelSellChannel channelSellChannel = new ChannelSellChannel();
        channelSellChannel.setChannelId(channelId);
        List<ChannelSellChannel> channelSellChannelList= channelSellChannelService.select(channelSellChannel);
        List<Long> sellChannelIdList = new ArrayList<>();
        List<SellChannel> sellChannelList = new ArrayList<>();
       if (!AssertUtil.collectionIsEmpty(channelSellChannelList)){
           for (ChannelSellChannel channelSell:channelSellChannelList) {
               sellChannelIdList.add(channelSell.getSellChannelId());
           }
           if (!AssertUtil.collectionIsEmpty(sellChannelIdList)){
               Example example = new Example(SellChannel.class);
               Example.Criteria criteria = example.createCriteria();
               criteria.andIn("id",sellChannelIdList);
               sellChannelList= sellChannelService.selectByExample(example);
           }
       }else {
           String msg ="当前业务线下没有关联的销售渠道";
           throw  new ChannelException(ExceptionEnum.SYSTEM_CHANNEL_QUERY_EXCEPTION,msg);
       }
        return sellChannelList;
    }

    @Override
    public ChannelExt queryChannelForUpdate(Long id) {
        Channel channel =  findChannelById(id);
        ChannelExt channelExt =JSON.parseObject(JSON.toJSONString(channel),ChannelExt.class);
        List<SellChannel> sellChannelList = selectLinkSellChannelById(id);
        channelExt.setSellChannelList(sellChannelList);
        return channelExt;
    }

    @Override
    public List<SellChannel> querySellChannel() {
        List<SellChannel> sellChannelList = sellChannelService.queryAllSellChannel();
        AssertUtil.notEmpty(sellChannelList,"销售渠道查询结果为空");
        return sellChannelList;
    }

    @Override
    public List<SellChannel> querySellChannelByChannelCode(AclUserAccreditInfo aclUserAccreditInfo) {
        long channelId = aclUserAccreditInfo.getChannelId();
        ChannelSellChannel channelSellChannel  = new ChannelSellChannel();
        channelSellChannel.setChannelId(channelId);
        List<ChannelSellChannel> channelSellChannelList=  channelSellChannelService.select(channelSellChannel);
        List<Long> sellChannelIdList =  new ArrayList<>();
        for (ChannelSellChannel sellChannel:channelSellChannelList){
            sellChannelIdList.add(sellChannel.getSellChannelId());
        }

        Example example = new Example(WarehouseInfo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIsNotNull("storeCorrespondChannel");
        List<WarehouseInfo> warehouseInfoList = warehouseInfoService.selectByExample(example);
        Set<String> storeCorrespondChannels = new HashSet<>();
        if(warehouseInfoList != null && warehouseInfoList.size() > 0){
            for(WarehouseInfo info : warehouseInfoList){
                storeCorrespondChannels.add(info.getStoreCorrespondChannel());
            }
        }

        List<SellChannel> sellChannelList = new ArrayList<>();
        if (!AssertUtil.collectionIsEmpty(sellChannelIdList)) {
            Example exampleSell = new Example(SellChannel.class);
            Example.Criteria criteriaSell = exampleSell.createCriteria();
            criteriaSell.andIn("id", sellChannelIdList);
            criteriaSell.andNotIn("sellCode", storeCorrespondChannels);
            criteriaSell.andEqualTo("sellType", SellChannelTypeEnum.STORE.getCode());
            sellChannelList = sellChannelService.selectByExample(exampleSell);
        }
        return sellChannelList;
    }


    /**
     * 业务销售渠道校验
     * 如果校验通过,返回选中的销售渠道信息
     * @param channel
     */
    private List<SellChannel> checkSellChannel(Channel channel) {
        AssertUtil.notBlank(channel.getSellChannel(),"业务销售渠道不能为空!");
        String[] sellChannelCodes = channel.getSellChannel().split(SupplyConstants.Symbol.COMMA);
        Example example = new Example(SellChannel.class);
        Example.Criteria  criteria = example.createCriteria();
        criteria.andIn("sellCode", Arrays.asList(sellChannelCodes));
        List<SellChannel> sellChannelList = sellChannelService.selectByExample(example);
        if (!AssertUtil.collectionIsEmpty(sellChannelList)){
            for (String sellCode: sellChannelCodes) {
                boolean isExist = false;
                for (SellChannel sellChannel:sellChannelList) {
                    if (StringUtils.equals(sellCode,sellChannel.getSellCode())){
                        isExist = true;
                    }
                }
                if (!isExist){
                    String msg ="未查询到需要关联的销售渠道,请检查销售渠道编码"+sellCode+"是否存在";
                    logger.error(msg);
                    throw  new ChannelException(ExceptionEnum.SYSTEM_CHANNEL_SAVE_EXCEPTION,msg);
                }
            }
        }else {
            String msg ="未查询到需要关联的销售渠道,请检查销售渠道是否存在";
            logger.error(msg);
            throw  new ChannelException(ExceptionEnum.SYSTEM_CHANNEL_SAVE_EXCEPTION,msg);
        }
        return  sellChannelList;
    }
}
