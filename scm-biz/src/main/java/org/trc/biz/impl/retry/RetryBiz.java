package org.trc.biz.impl.retry;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.trc.biz.retry.IRetryBiz;
import org.trc.domain.config.QureyCondition;
import org.trc.domain.config.RequestFlow;
import org.trc.domain.config.RetryConfig;
import org.trc.domain.config.TimeRecord;
import org.trc.enums.NetwordStateEnum;
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
import java.util.*;
import java.util.concurrent.*;

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

    private static final int EXECUTOR_SIZE = 6;


    public void brandUpdateNoticeRetry(){
        //1、查询request flow表，找出需要重试的记录
        List<String> status = new ArrayList<String>();
        status.add(NetwordStateEnum.FAILED.getCode());
        status.add(NetwordStateEnum.SOCKET_TIME_OUT.getCode());
        QureyCondition condition = new QureyCondition();
        condition.setList(status);
        condition.setType(RequestFlowTypeEnum.BRAND_UPDATE_NOTICE.getCode());
        executor(condition);
    }

    public void propertyUpdateNoticeRetry(){
        //1、查询request flow表，找出需要重试的记录
        List<String> status = new ArrayList<String>();
        status.add(NetwordStateEnum.FAILED.getCode());
        status.add(NetwordStateEnum.SOCKET_TIME_OUT.getCode());
        QureyCondition condition = new QureyCondition();
        condition.setList(status);
        condition.setType(RequestFlowTypeEnum.PROPERTY_UPDATE_NOTICE.getCode());
        executor(condition);
    }

    public void categoryUpdateNoticeRetry(){
        //1、查询request flow表，找出需要重试的记录
        List<String> status = new ArrayList<String>();
        status.add(NetwordStateEnum.FAILED.getCode());
        status.add(NetwordStateEnum.SOCKET_TIME_OUT.getCode());
        QureyCondition condition = new QureyCondition();
        condition.setList(status);
        condition.setType(RequestFlowTypeEnum.CATEFORY_UPDATE_NOTICE.getCode());
        executor(condition);
    }

    public void categoryBrandUpdateNoticeRetry(){
        //1、查询request flow表，找出需要重试的记录
        List<String> status = new ArrayList<String>();
        status.add(NetwordStateEnum.FAILED.getCode());
        status.add(NetwordStateEnum.SOCKET_TIME_OUT.getCode());
        QureyCondition condition = new QureyCondition();
        condition.setList(status);
        condition.setType(RequestFlowTypeEnum.CATEFORY_BRAND_UPDATE_NOTICE.getCode());
        executor(condition);
    }

    public void categoryPropertyUpdateNoticeRetry(){
        //1、查询request flow表，找出需要重试的记录
        List<String> status = new ArrayList<String>();
        status.add(NetwordStateEnum.FAILED.getCode());
        status.add(NetwordStateEnum.SOCKET_TIME_OUT.getCode());
        QureyCondition condition = new QureyCondition();
        condition.setList(status);
        condition.setType(RequestFlowTypeEnum.CATEFORY_PROPERTY_UPDATE_NOTICE.getCode());
        executor(condition);
    }

    public void itemUpdateNoticeRetry(){
        //1、查询request flow表，找出需要重试的记录
        List<String> status = new ArrayList<String>();
        status.add(NetwordStateEnum.FAILED.getCode());
        status.add(NetwordStateEnum.SOCKET_TIME_OUT.getCode());
        QureyCondition condition = new QureyCondition();
        condition.setList(status);
        condition.setType(RequestFlowTypeEnum.ITEM_UPDATE_NOTICE.getCode());
        executor(condition);
    }

    public void externalItemUpdateNoticeRetry(){
        //1、查询request flow表，找出需要重试的记录
        List<String> status = new ArrayList<String>();
        status.add(NetwordStateEnum.FAILED.getCode());
        status.add(NetwordStateEnum.SOCKET_TIME_OUT.getCode());
        QureyCondition condition = new QureyCondition();
        condition.setList(status);
        condition.setType(RequestFlowTypeEnum.EXTERNAL_ITEM_UPDATE_NOTICE.getCode());
        executor(condition);
    }

    public void channelReceiveOrderSubmitResultRetry(){
        //1、查询request flow表，找出需要重试的记录
        List<String> status = new ArrayList<String>();
        status.add(NetwordStateEnum.FAILED.getCode());
        status.add(NetwordStateEnum.SOCKET_TIME_OUT.getCode());
        QureyCondition condition = new QureyCondition();
        condition.setList(status);
        condition.setType(RequestFlowTypeEnum.CHANNEL_RECEIVE_ORDER_SUBMIT_RESULT.getCode());
        executor(condition);
    }

    public void sendLogisticsInfoToChannelRetry(){
        //1、查询request flow表，找出需要重试的记录
        List<String> status = new ArrayList<String>();
        status.add(NetwordStateEnum.FAILED.getCode());
        status.add(NetwordStateEnum.SOCKET_TIME_OUT.getCode());
        QureyCondition condition = new QureyCondition();
        condition.setList(status);
        condition.setType(RequestFlowTypeEnum.SEND_LOGISTICS_INFO_TO_CHANNEL.getCode());
        executor(condition);
    }



    private void executor(QureyCondition condition){
        List<RequestFlow> requestFlowList = null;
        requestFlowList = requestFlowService.queryBatch(condition);
        //2、参数判断
        if(requestFlowList.size()==0){
            log.info("没有需要重试的记录");
        }
        //3、对请求失败的进行重试
        log.info("开始执行重试任务=======>");
        Long startTime = Calendar.getInstance().getTimeInMillis();
        retryJob(requestFlowList,condition.getType());
        Long endTime = Calendar.getInstance().getTimeInMillis();
        log.info("重试任务完成,耗时" + (endTime - startTime)/1000 + "秒");
        recordTime(startTime, endTime);
        if(requestFlowList.size()<1000){
            RequestFlow lastOne = requestFlowList.get(requestFlowList.size()-1);
            condition.setId(lastOne.getId());
            executor(condition);
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

    private void retryJob(List<RequestFlow> requestFlows,String type){
        final ExecutorService fixedThreadPool = Executors.newFixedThreadPool(EXECUTOR_SIZE);
        log.info("开始重试======>");
        //2.获取单个商品池内商品sku编号
        try {
            //1、对需要进行重试订单进行分类
            //Map<String,List<RequestFlow>> map = retryClassify(requestFlows);
            final CountDownLatch begin = new CountDownLatch(1);
            // 结束的倒数锁
            final CountDownLatch end = new CountDownLatch(requestFlows.size());
            for (RequestFlow flow :requestFlows){
                Runnable runnable = new Runnable(){
                    @Override
                    public void run() {
                        try {
                            switch (type){
                                case "BRAND_UPDATE_NOTICE":
                                    brandUpdateNoticeRetry(requestFlows);
                                    break;
                                case "PROPERTY_UPDATE_NOTICE":
                                    propertyUpdateNoticeRetry(requestFlows);
                                    break;
                                case "CATEFORY_UPDATE_NOTICE":
                                    categoryUpdateNoticeRetry(requestFlows);
                                    break;
                                case "CATEFORY_BRAND_UPDATE_NOTICE":
                                    categoryBrandUpdateNoticeRetry(requestFlows);
                                    break;
                                case "CATEFORY_PROPERTY_UPDATE_NOTICE":
                                    categoryPropertyUpdateNoticeRetry(requestFlows);
                                    break;
                                case "ITEM_UPDATE_NOTICE":
                                    itemUpdateNoticeRetry(requestFlows);
                                    break;
                                case "EXTERNAL_ITEM_UPDATE_NOTICE":
                                    externalItemUpdateNoticeRetry(requestFlows);
                                    break;
                                case "CHANNEL_RECEIVE_ORDER_SUBMIT_RESULT":
                                    channelReceiveOrderSubmitResultRetry(requestFlows);
                                    break;
                                default:
                                    sendLogisticsInfoToChannelRetry(requestFlows);
                                    break;
                            }
                        } catch (Exception e) {
                            log.error("重试机制异常", e);
                        } finally {
                            // 任务完成，end就减一
                            end.countDown();
                        }
                    }
                };
                fixedThreadPool.submit(runnable);
            }
            begin.countDown();
            end.await();
            fixedThreadPool.shutdown();
        }catch (Exception e){
            log.error("重试任务异常："+e.getMessage(),e);
        }
    }

    /*private Map<String,List<RequestFlow>> retryClassify(List<RequestFlow> requestFlows){
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
    }*/

    //品牌变更通知重试
    private void brandUpdateNoticeRetry(List<RequestFlow> list) throws Exception{
        try {
            for (RequestFlow requestFlow:list){
                String requestParam = requestFlow.getRequestParam();
                AssertUtil.notBlank(requestParam,"请求参数不能为空");
                boolean result = getNextExecut(requestFlow,RequestFlowTypeEnum.BRAND_UPDATE_NOTICE.getCode());
                if (!result){
                    continue;
                }
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
                boolean result = getNextExecut(requestFlow,RequestFlowTypeEnum.PROPERTY_UPDATE_NOTICE.getCode());
                if (!result){
                    continue;
                }
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
                boolean result = getNextExecut(requestFlow,RequestFlowTypeEnum.CATEFORY_UPDATE_NOTICE.getCode());
                if (!result){
                    continue;
                }
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
                boolean result = getNextExecut(requestFlow,RequestFlowTypeEnum.CATEFORY_BRAND_UPDATE_NOTICE.getCode());
                if (!result){
                    continue;
                }
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
                boolean result = getNextExecut(requestFlow,RequestFlowTypeEnum.CATEFORY_PROPERTY_UPDATE_NOTICE.getCode());
                if (!result){
                    continue;
                }
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
                boolean result = getNextExecut(requestFlow,RequestFlowTypeEnum.ITEM_UPDATE_NOTICE.getCode());
                if (!result){
                    continue;
                }
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
                boolean result = getNextExecut(requestFlow,RequestFlowTypeEnum.EXTERNAL_ITEM_UPDATE_NOTICE.getCode());
                if (!result){
                    continue;
                }
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
                boolean result = getNextExecut(requestFlow,RequestFlowTypeEnum.CHANNEL_RECEIVE_ORDER_SUBMIT_RESULT.getCode());
                if (!result){
                    continue;
                }
                //执行重试
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
                boolean result = getNextExecut(requestFlow,RequestFlowTypeEnum.SEND_LOGISTICS_INFO_TO_CHANNEL.getCode());
                if (!result){
                    continue;
                }
                //执行重试
                LogisticNoticeForm logisticNoticeForm = JSONObject.parseObject(requestParam,LogisticNoticeForm.class);
                ToGlyResultDO resultDO = trcService.sendLogisticInfoNotice(logisticNoticeForm);
                if (ZeroToNineEnum.ONE.getCode().equals(resultDO.getStatus())){
                    int count = requestFlowService.changeState(requestFlow.getRequestNum());
                    if (count==0){
                        log.error("发送物流信息给渠道重试：更新状态失败！");
                    }
                }
            }
        }catch (Exception e){
            log.error("发送物流信息给渠道重试异常："+e.getMessage(),e);
            throw new Exception("发送物流信息给渠道重试异常："+e.getMessage());
        }
    }

    private boolean getNextExecut(RequestFlow requestFlow,String type) throws Exception{
        try {
            AssertUtil.notNull(requestFlow,"输入参数requestFlow不能为空");
            AssertUtil.notBlank(type,"输入参数type不能为空");
            RetryConfig retryConfig = new RetryConfig();
            retryConfig.setType(type);
            RetryConfig record = retryConfigService.selectOne(retryConfig);
            AssertUtil.notNull(record,"配置表中没有该配置信息");
            //1.1计算结束启动时间
            RequestFlow flow = new RequestFlow();
            Long time = Calendar.getInstance().getTimeInMillis()+record.getPeriod()*10000;
            if (null == requestFlow.getEndTime()){
                Date endTime = new Date(time);
                flow.setEndTime(endTime);
            }
            Long count = requestFlow.getCount()+1;
            flow.setCount(count);
            flow.setRequestNum(requestFlow.getRequestNum());
            int i = requestFlowService.updateRequestFlowByRequestNum(flow);
            if (i<=0){
                log.error("流水记录表：更新状态失败！");
            }
            Calendar c1 = Calendar.getInstance();
            Calendar c2 = Calendar.getInstance();
            c1.setTime(requestFlow.getEndTime());
            int result = c1.compareTo(c2);
            //1.2判断是否满足重试条件
            if (1!=record.getCount().compareTo(requestFlow.getCount()) && 1 != result){
                return false;
            }
            return true;
        }catch (Exception e){
            log.error("重试机制更改下次执行状态异常："+e.getMessage(),e);
            return false;
        }
    }


}
