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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.trc.biz.system.IChannelBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.System.Channel;
import org.trc.enums.ExceptionEnum;
import org.trc.enums.LogOperationEnum;
import org.trc.enums.ValidEnum;
import org.trc.enums.ZeroToNineEnum;
import org.trc.exception.ChannelException;
import org.trc.form.system.ChannelForm;
import org.trc.model.SearchResult;
import org.trc.service.IPageNationService;
import org.trc.service.System.IChannelService;
import org.trc.service.config.ILogInfoService;
import org.trc.service.util.ISerialUtilService;
import org.trc.service.util.IUserNameUtilService;
import org.trc.util.AssertUtil;
import org.trc.util.Pagenation;
import org.trc.util.ParamsUtil;
import org.trc.util.TransportClientUtil;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.ws.rs.container.ContainerRequestContext;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by sone on 2017/5/2.
 */
@Service("channelBiz")
public class ChannelBiz implements IChannelBiz {

    private Logger logger = LoggerFactory.getLogger(ChannelBiz.class);

    private final static String SERIALNAME = "QD";

    private final static Integer LENGTH = 3;

    @Autowired
    private IChannelService channelService;

    @Autowired
    private IUserNameUtilService userNameUtilService;

    @Autowired
    private ISerialUtilService serialUtilService;

    @Autowired
    private IPageNationService pageNationService;
    @Resource
    private ILogInfoService logInfoService;


    @Override
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
        userNameUtilService.handleUserName(channelList);
        return pagenation;

    }

    @Override
    public Pagenation<Channel> channelPageES(ChannelForm queryModel, Pagenation<Channel> page) {
        TransportClient clientUtil = TransportClientUtil.getTransportClient();
        HighlightBuilder hiBuilder = new HighlightBuilder();
        hiBuilder.preTags("<b style=\"color: red\">");
        hiBuilder.postTags("</b>");
        hiBuilder.field("name.pinyin");//http://172.30.250.164:9100/ 模糊字段可在这里找到
        SearchRequestBuilder srb = clientUtil.prepareSearch("channel")
                .highlighter(hiBuilder)
                .addSort(SortBuilders.fieldSort("update_time").order(SortOrder.DESC))
                .setFrom(page.getStart())//第几个开始
                .setSize(page.getPageSize());//长度
        String name = "name.pinyin";
        if (StringUtils.isNotBlank(queryModel.getName())) {
            QueryBuilder matchQuery = QueryBuilders.matchQuery(name, queryModel.getName());
            srb.setQuery(matchQuery);
        }
        if (!StringUtils.isBlank(queryModel.getIsValid())) {
            QueryBuilder filterBuilder = QueryBuilders.termQuery("is_valid", queryModel.getIsValid());
            srb.setPostFilter(filterBuilder);
        }
        SearchResult searchResult;
        try {
            searchResult = pageNationService.resultES(srb, clientUtil);
        } catch (Exception e) {
            logger.error("es查询失败" + e.getMessage(), e);
            return page;
        }
        List<Channel> channelList = new ArrayList<>();
        for (SearchHit searchHit : searchResult.getSearchHits()) {
            Channel channel = JSON.parseObject(JSON.toJSONString(searchHit.getSource()), Channel.class);
            if (StringUtils.isNotBlank(queryModel.getName())) {
                for (Text text : searchHit.getHighlightFields().get(name).getFragments()) {
                    channel.setHighLightName(text.string());
                }
            }
            channelList.add(channel);
        }
        if (AssertUtil.collectionIsEmpty(channelList)) {
            return page;
        }
        page.setResult(channelList);
        userNameUtilService.handleUserName(page.getResult());
        page.setTotalCount(searchResult.getCount());
        return page;

    }


    public Channel findChannelByName(String name) {

        AssertUtil.notBlank(name, "根据渠道名称查询渠道的参数name为空");
        Channel channel = new Channel();
        channel.setName(name);
        return channelService.selectOne(channel);

    }

    @Override
    public List<Channel> queryChannels(ChannelForm channelForm) {
        Channel channel = new Channel();
        if (StringUtils.isEmpty(channelForm.getIsValid())) {
            channel.setIsValid(ZeroToNineEnum.ONE.getCode());
        }
        channel.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
        return channelService.select(channel);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void saveChannel(Channel channel, ContainerRequestContext requestContext) {

        AssertUtil.notNull(channel, "渠道管理模块保存仓库信息失败，仓库信息为空");
        Channel tmp = findChannelByName(channel.getName());
        AssertUtil.isNull(tmp, String.format("渠道名称[name=%s]的数据已存在,请使用其他名称", channel.getName()));
        channel.setIsValid(ValidEnum.VALID.getCode()); //渠道状态一直为有效
        ParamsUtil.setBaseDO(channel);
        channel.setCode(serialUtilService.generateCode(LENGTH, SERIALNAME));
        int count = channelService.insert(channel);
        if (count == 0) {
            String msg = "渠道保存,数据库操作失败";
            logger.error(msg);
            throw new ChannelException(ExceptionEnum.SYSTEM_CHANNEL_SAVE_EXCEPTION, msg);
        }
        String userId = (String) requestContext.getProperty(SupplyConstants.Authorization.USER_ID);
        logInfoService.recordLog(channel, channel.getId().toString(), userId, LogOperationEnum.ADD.getMessage(), "新增渠道", null);

    }


    @Override
    public void updateChannel(Channel channel, ContainerRequestContext requestContext) {

        AssertUtil.notNull(channel.getId(), "修改渠道参数ID为空");
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


        String userId = (String) requestContext.getProperty(SupplyConstants.Authorization.USER_ID);
        logInfoService.recordLog(channel, channel.getId().toString(), userId, LogOperationEnum.UPDATE.getMessage(), null, null);

    }

    @Override
    public Channel findChannelById(Long id) {

        AssertUtil.notNull(id, "根据ID查询渠道明细,参数ID不能为空");
        Channel channel = new Channel();
        channel.setId(id);
        channel = channelService.selectOne(channel);
        AssertUtil.notNull(channel, String.format("根据主键ID[id=%s]查询渠道为空", id.toString()));
        return channel;

    }

    @Override
    public void updateChannelState(Channel channel) {

        AssertUtil.notNull(channel, "渠道管理模块修改渠道信息失败，仓库信息为空");
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

}
