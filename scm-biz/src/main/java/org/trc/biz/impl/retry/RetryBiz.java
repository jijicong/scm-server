package org.trc.biz.impl.retry;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.trc.biz.retry.IRetryBiz;
import org.trc.domain.config.RequestFlow;
import org.trc.domain.config.RetryConfig;
import org.trc.domain.config.TimeRecord;
import org.trc.enums.RequestFlowTypeEnum;
import org.trc.enums.ZeroToNineEnum;
import org.trc.form.ChannelOrderResponse;
import org.trc.form.LogisticNoticeForm;
import org.trc.form.TrcConfig;
import org.trc.model.ToGlyResultDO;
import org.trc.service.ITrcService;
import org.trc.service.config.IRequestFlowService;
import org.trc.service.config.IRetryConfigService;
import org.trc.service.config.ITimeRecordService;
import org.trc.util.AssertUtil;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

/**
 * Created by hzwyz on 2017/8/3 0003.
 */
@Service("retryBiz")
public class RetryBiz implements IRetryBiz {
    private Logger log = LoggerFactory.getLogger(RetryBiz.class);
    @Autowired
    private IRequestFlowService requestFlowService;

    @Autowired
    private ITimeRecordService timeRecordService;

    @Autowired
    private ITrcService trcService;

    @Autowired
    private TrcConfig trcConfig;

    @Autowired
    private IRetryConfigService retryConfigService;

    //订单信息查询路径
    private static final Long RETRY_INTERVAL = 600000L;

    public void faileRetry() throws Exception{
        //1、查询request flow表，找出需要重试的记录
        Example example = new Example(RequestFlow.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("status","SEND_FAILED");
        Example.Criteria criteria01 = example.createCriteria();
        criteria01.andEqualTo("status","SOCKET_TIME_OUT");
        example.orderBy("desc");
        example.or(criteria01);
        List<RequestFlow> requestFlowList = requestFlowService.selectByExample(example);
        //2、参数判断
        AssertUtil.isTrue(requestFlowList.size()>0,"没有需要失败重试的记录");
        //3、对请求失败的进行重试
        //3.1重试前判断前一个任务是否结束
        List<TimeRecord> timeRecordList = timeRecordService.getLatestRecord();
        if (timeRecordList.size()==0){
            //第一次运行 不用去检测上个任务是否执行完毕，直接执行
            //3.2对执行失败的任务进行重试
            log.info("开始执行重试任务=======>");
            Long startTime = Calendar.getInstance().getTimeInMillis();
            retryJob(requestFlowList);
            Long endTime = Calendar.getInstance().getTimeInMillis();
            log.info("重试任务完成,耗时" + (endTime - startTime) / 1000 + "秒");
            recordTime(startTime, endTime);
        }else {
            TimeRecord timeRecord = timeRecordList.get(0);
            long dif = Calendar.getInstance().getTimeInMillis()-timeRecord.getEndTime().getTime();
            if (RETRY_INTERVAL.compareTo(dif) < 0) {
                log.info("上一次定时任务未执行完毕");
                throw new Exception("上一次定时任务未执行完毕");
            }
            //3.2对执行失败的任务进行重试
            log.info("开始执行重试任务=======>");
            Long startTime = Calendar.getInstance().getTimeInMillis();
            retryJob(requestFlowList);
            Long endTime = Calendar.getInstance().getTimeInMillis();
            log.info("重试任务完成,耗时" + (endTime - startTime) / 1000 + "秒");
            recordTime(startTime, endTime);
        }
    }

    /**
     * 记录时间
     *
     * @param startTime
     * @param endTime
     */
    private void recordTime(Long startTime, Long endTime) {
        try {
            TimeRecord timeRecord = new TimeRecord();
            timeRecord.setUseTime((endTime - startTime));
            timeRecord.setEndTime(new Date(endTime));
            timeRecord.setStartTime(new Date(startTime));
            timeRecordService.insert(timeRecord);
        } catch (Exception e) {
            log.error("时间记录失败", e);
        }
    }


    private void retryJob(List<RequestFlow> requestFlows) throws Exception{
        try {
            //1、对需要进行重试订单进行分类
            Map<String,List<RequestFlow>> map = retryClassify(requestFlows);
            for (Map.Entry<String,List<RequestFlow>> entry:map.entrySet()){
                if ("brandUpdateNotice".equals(entry.getKey())){
                    brandUpdateNoticeRetry(entry.getValue());
                }
                if ("propertyUpdateNotice".equals(entry.getKey())){
                    propertyUpdateNoticeRetry(entry.getValue());
                }
                if ("categoryUpdateNotice".equals(entry.getKey())){
                    categoryUpdateNoticeRetry(entry.getValue());
                }
                if ("categoryBrandUpdateNotice".equals(entry.getKey())){
                    categoryBrandUpdateNoticeRetry(entry.getValue());
                }
                if ("categoryPropertyUpdateNotice".equals(entry.getKey())){
                    categoryPropertyUpdateNoticeRetry(entry.getValue());
                }
                if ("itemUpdateNotice".equals(entry.getKey())){
                    itemUpdateNoticeRetry(entry.getValue());
                }
                if ("externalItemUpdateNotice".equals(entry.getKey())){
                    externalItemUpdateNoticeRetry(entry.getValue());
                }
                if ("channelReceiveOrderSubmitResult".equals(entry.getKey())){
                    channelReceiveOrderSubmitResultRetry(entry.getValue());
                }
                if ("sendLogisticsInfoToChannel".equals(entry.getKey())){


                    sendLogisticsInfoToChannelRetry(entry.getValue());

                }
            }
        }catch (Exception e){
            log.error("重试任务异常："+e.getMessage(),e);
            throw new Exception("重试任务异常："+e.getMessage());
        }
    }

    private Map<String,List<RequestFlow>> retryClassify(List<RequestFlow> requestFlows){
        Map<String,List<RequestFlow>> map = new HashMap<String,List<RequestFlow>>();
        //品牌变更通知重试
        List<RequestFlow> brandUpdateNotice = new ArrayList<RequestFlow>();
        //属性变更通知重试
        List<RequestFlow> propertyUpdateNotice = new ArrayList<RequestFlow>();
        //分类变更通知重试
        List<RequestFlow> categoryUpdateNotice = new ArrayList<RequestFlow>();
        //分类品牌变更通知重试
        List<RequestFlow> categoryBrandUpdateNotice = new ArrayList<RequestFlow>();
        //分类属性变更通知重试
        List<RequestFlow> categoryPropertyUpdateNotice = new ArrayList<RequestFlow>();
        //自营商品变更通知重试
        List<RequestFlow> itemUpdateNotice = new ArrayList<RequestFlow>();
        //代发商品变更通知重试
        List<RequestFlow> externalItemUpdateNotice = new ArrayList<RequestFlow>();
        //渠道接收订单提交结果重试
        List<RequestFlow> channelReceiveOrderSubmitResult = new ArrayList<RequestFlow>();
        //发送物流信息给渠道重试
        List<RequestFlow> sendLogisticsInfoToChannel = new ArrayList<RequestFlow>();
        for (RequestFlow requestFlow : requestFlows){
            if (StringUtils.equals(requestFlow.getType(), RequestFlowTypeEnum.BRAND_UPDATE_NOTICE.getCode())){
                brandUpdateNotice.add(requestFlow);
            }
            if (StringUtils.equals(requestFlow.getType(), RequestFlowTypeEnum.PROPERTY_UPDATE_NOTICE.getCode())){
                propertyUpdateNotice.add(requestFlow);
            }
            if (StringUtils.equals(requestFlow.getType(), RequestFlowTypeEnum.CATEFORY_UPDATE_NOTICE.getCode())){
                categoryUpdateNotice.add(requestFlow);
            }
            if (StringUtils.equals(requestFlow.getType(), RequestFlowTypeEnum.CATEFORY_BRAND_UPDATE_NOTICE.getCode())){
                categoryBrandUpdateNotice.add(requestFlow);
            }
            if (StringUtils.equals(requestFlow.getType(), RequestFlowTypeEnum.CATEFORY_PROPERTY_UPDATE_NOTICE.getCode())){
                categoryPropertyUpdateNotice.add(requestFlow);
            }
            if (StringUtils.equals(requestFlow.getType(), RequestFlowTypeEnum.ITEM_UPDATE_NOTICE.getCode())){
                itemUpdateNotice.add(requestFlow);
            }
            if (StringUtils.equals(requestFlow.getType(), RequestFlowTypeEnum.EXTERNAL_ITEM_UPDATE_NOTICE.getCode())){
                externalItemUpdateNotice.add(requestFlow);
            }
            if (StringUtils.equals(requestFlow.getType(), RequestFlowTypeEnum.CHANNEL_RECEIVE_ORDER_SUBMIT_RESULT.getCode())){
                channelReceiveOrderSubmitResult.add(requestFlow);
            }
            if (StringUtils.equals(requestFlow.getType(), RequestFlowTypeEnum.SEND_LOGISTICS_INFO_TO_CHANNEL.getCode())){
                sendLogisticsInfoToChannel.add(requestFlow);
            }
        }
        map.put("brandUpdateNotice",brandUpdateNotice);
        map.put("propertyUpdateNotice",propertyUpdateNotice);
        map.put("categoryUpdateNotice",categoryUpdateNotice);
        map.put("categoryBrandUpdateNotice",categoryBrandUpdateNotice);
        map.put("categoryPropertyUpdateNotice",categoryPropertyUpdateNotice);
        map.put("itemUpdateNotice",itemUpdateNotice);
        map.put("externalItemUpdateNotice",externalItemUpdateNotice);
        map.put("channelReceiveOrderSubmitResult",channelReceiveOrderSubmitResult);
        map.put("sendLogisticsInfoToChannel",sendLogisticsInfoToChannel);
        return map;
    }

    //品牌变更通知重试
    private void brandUpdateNoticeRetry(List<RequestFlow> list) throws Exception{
        try {
            for (RequestFlow requestFlow:list){
                String requestParam = requestFlow.getRequestParam();
                AssertUtil.notBlank(requestParam,"请求参数不能为空");
                ToGlyResultDO resultDO = trcService.sendBrandNotice(trcConfig.getBrandUrl(),requestFlow.getRequestParam());
                if (ZeroToNineEnum.ONE.getCode().equals(resultDO.getStatus())){
                    int count = requestFlowService.changeState(requestFlow.getRequestNum());
                    if (count==0){
                        log.error("品牌变更通知重试：更新状态失败！");
                    }
                }
            }
        }catch (Exception e){
            log.error("品牌变更通知重试异常："+e.getMessage(),e);
            throw new Exception("品牌变更通知重试异常："+e.getMessage());
        }

    }

    //属性变更通知重试
    private void propertyUpdateNoticeRetry(List<RequestFlow> list) throws Exception{
        try {
            for (RequestFlow requestFlow:list){
                String requestParam = requestFlow.getRequestParam();
                AssertUtil.notBlank(requestParam,"请求参数不能为空");
                ToGlyResultDO resultDO = trcService.sendPropertyNotice(trcConfig.getPropertyUrl(),requestFlow.getRequestParam());
                if (ZeroToNineEnum.ONE.getCode().equals(resultDO.getStatus())){
                    int count = requestFlowService.changeState(requestFlow.getRequestNum());
                    if (count==0){
                        log.error("属性变更通知重试：更新状态失败！");
                    }
                }
            }
        }catch (Exception e){
            log.error("属性变更通知重试异常："+e.getMessage(),e);
            throw new Exception("属性变更通知重试异常："+e.getMessage());
        }
    }

    //分类变更通知重试
    private void categoryUpdateNoticeRetry(List<RequestFlow> list) throws Exception{
        try {
            for (RequestFlow requestFlow:list){
                String requestParam = requestFlow.getRequestParam();
                AssertUtil.notBlank(requestParam,"请求参数不能为空");
                ToGlyResultDO resultDO = trcService.sendCategoryToTrc(trcConfig.getBrandUrl(),requestFlow.getRequestParam());
                if (ZeroToNineEnum.ONE.getCode().equals(resultDO.getStatus())){
                    int count = requestFlowService.changeState(requestFlow.getRequestNum());
                    if (count==0){
                        log.error("分类变更通知重试：更新状态失败！");
                    }
                }
            }
        }catch (Exception e){
            log.error("分类变更通知重试异常："+e.getMessage(),e);
            throw new Exception("分类变更通知重试异常："+e.getMessage());
        }
    }

    //分类品牌变更通知重试
    private void categoryBrandUpdateNoticeRetry(List<RequestFlow> list) throws Exception{
        try {
            for (RequestFlow requestFlow:list){
                String requestParam = requestFlow.getRequestParam();
                AssertUtil.notBlank(requestParam,"请求参数不能为空");
                ToGlyResultDO resultDO = trcService.sendCategoryBrandList(trcConfig.getCategoryBrandUrl(),requestFlow.getRequestParam());
                if (ZeroToNineEnum.ONE.getCode().equals(resultDO.getStatus())){
                    int count = requestFlowService.changeState(requestFlow.getRequestNum());
                    if (count==0){
                        log.error("分类品牌变更通知重试：更新状态失败！");
                    }
                }
            }
        }catch (Exception e){
            log.error("分类品牌变更通知重试异常："+e.getMessage(),e);
            throw new Exception("分类品牌变更通知重试异常："+e.getMessage());
        }
    }

    //分类属性变更通知重试
    private void categoryPropertyUpdateNoticeRetry(List<RequestFlow> list) throws Exception{
        try {
            for (RequestFlow requestFlow:list){
                String requestParam = requestFlow.getRequestParam();
                AssertUtil.notBlank(requestParam,"请求参数不能为空");
                ToGlyResultDO resultDO = trcService.sendCategoryPropertyList(trcConfig.getCategoryPropertyUrl(),requestFlow.getRequestParam());
                if (ZeroToNineEnum.ONE.getCode().equals(resultDO.getStatus())){
                    int count = requestFlowService.changeState(requestFlow.getRequestNum());
                    if (count==0){
                        log.error("分类属性变更通知重试：更新状态失败！");
                    }
                }
            }
        }catch (Exception e){
            log.error("分类属性变更通知重试异常："+e.getMessage(),e);
            throw new Exception("分类属性变更通知重试异常："+e.getMessage());
        }
    }

    //自营商品变更通知重试
    private void itemUpdateNoticeRetry(List<RequestFlow> list) throws Exception{
        try {
            for (RequestFlow requestFlow:list){
                String requestParam = requestFlow.getRequestParam();
                AssertUtil.notBlank(requestParam,"请求参数不能为空");
                ToGlyResultDO resultDO = trcService.sendItemsNotice(trcConfig.getItemUrl(),requestFlow.getRequestParam());
                if (ZeroToNineEnum.ONE.getCode().equals(resultDO.getStatus())){
                    int count = requestFlowService.changeState(requestFlow.getRequestNum());
                    if (count==0){
                        log.error("自营商品变更通知重试：更新状态失败！");
                    }
                }
            }
        }catch (Exception e){
            log.error("自营商品变更通知重试异常："+e.getMessage(),e);
            throw new Exception("自营商品变更通知重试异常："+e.getMessage());
        }
    }

    //代发商品变更通知重试
    private void externalItemUpdateNoticeRetry(List<RequestFlow> list) throws Exception{
        try {
            for (RequestFlow requestFlow:list){
                String requestParam = requestFlow.getRequestParam();
                AssertUtil.notBlank(requestParam,"请求参数不能为空");
                ToGlyResultDO resultDO = trcService.sendPropertyNotice(trcConfig.getExternalItemSkuUpdateUrl(),requestParam);
                if (ZeroToNineEnum.ONE.getCode().equals(resultDO.getStatus())){
                    int count = requestFlowService.changeState(requestFlow.getRequestNum());
                    if (count==0){
                        log.error("代发商品变更通知重试：更新状态失败！");
                    }
                }
            }
        }catch (Exception e){
            log.error("代发商品变更通知重试异常："+e.getMessage(),e);
            throw new Exception("代发商品变更通知重试异常："+e.getMessage());
        }
    }

    //渠道接收订单提交结果重试
    private void channelReceiveOrderSubmitResultRetry(List<RequestFlow> list) throws Exception{
        try {
            for (RequestFlow requestFlow:list){
                String requestParam = requestFlow.getRequestParam();
                AssertUtil.notBlank(requestParam,"请求参数不能为空");
                ChannelOrderResponse channelOrderResponse = JSONObject.parseObject(requestParam,ChannelOrderResponse.class);
                ToGlyResultDO resultDO = trcService.sendOrderSubmitResultNotice(channelOrderResponse);
                if (ZeroToNineEnum.ONE.getCode().equals(resultDO.getStatus())){
                    int count = requestFlowService.changeState(requestFlow.getRequestNum());
                    if (count==0){
                        log.error("渠道接收订单提交结果重试：更新状态失败！");
                    }
                }
            }
        }catch (Exception e){
            log.error("渠道接收订单提交结果重试异常："+e.getMessage(),e);
            throw new Exception("渠道接收订单提交结果重试异常："+e.getMessage());
        }
    }

    //解析requestParam

    //发送物流信息给渠道重试
    private void sendLogisticsInfoToChannelRetry(List<RequestFlow> list) throws Exception{
        try {
            for (RequestFlow requestFlow:list){
                String requestParam = requestFlow.getRequestParam();
                AssertUtil.notBlank(requestParam,"请求参数不能为空");
                LogisticNoticeForm logisticNoticeForm = JSONObject.parseObject(requestParam,LogisticNoticeForm.class);
                ToGlyResultDO resultDO = trcService.sendLogisticInfoNotice(logisticNoticeForm);
                if (ZeroToNineEnum.ONE.getCode().equals(resultDO.getStatus())){
                    int count = requestFlowService.changeState(requestFlow.getRequestNum());
                    if (count==0){
                        log.error("发送物流信息给渠道重试：更新状态失败！");
                    }
                }
                RetryConfig retryConfig = new RetryConfig();
                retryConfig.setType(RequestFlowTypeEnum.SEND_LOGISTICS_INFO_TO_CHANNEL.getCode());
                RetryConfig record = retryConfigService.selectOne(retryConfig);
                record.getCount();
                record.getPeriod();
            }
        }catch (Exception e){
            log.error("发送物流信息给渠道重试异常："+e.getMessage(),e);
            throw new Exception("发送物流信息给渠道重试异常："+e.getMessage());
        }
    }


}
