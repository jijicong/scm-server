package org.trc.biz.impl.retry;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.trc.biz.goods.IGoodsBiz;
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
import org.trc.service.util.IRealIpService;
import org.trc.util.IpUtil;

import java.net.SocketException;
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

    @Autowired
    private IRealIpService iRealIpService;

    @Value("${retry.server.ip}")
    private String taskIp;

    private static final int EXECUTOR_SIZE = 6;
    private static final Long hours = 3600000L;

    private static final String BRAND_UPDATE_NOTICE = "BRAND_UPDATE_NOTICE";
    private static final String PROPERTY_UPDATE_NOTICE = "PROPERTY_UPDATE_NOTICE";
    private static final String CATEFORY_UPDATE_NOTICE = "CATEFORY_UPDATE_NOTICE";
    private static final String CATEFORY_BRAND_UPDATE_NOTICE = "CATEFORY_BRAND_UPDATE_NOTICE";
    private static final String CATEFORY_PROPERTY_UPDATE_NOTICE = "CATEFORY_PROPERTY_UPDATE_NOTICE";
    private static final String ITEM_UPDATE_NOTICE = "ITEM_UPDATE_NOTICE";
    private static final String EXTERNAL_ITEM_UPDATE_NOTICE = "EXTERNAL_ITEM_UPDATE_NOTICE";
    private static final String CHANNEL_RECEIVE_ORDER_SUBMIT_RESULT = "CHANNEL_RECEIVE_ORDER_SUBMIT_RESULT";


    public void brandUpdateNoticeRetry(){
        if (iRealIpService.isRealTimerService(taskIp)) return;
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
        if (iRealIpService.isRealTimerService(taskIp)) return;
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
        if (iRealIpService.isRealTimerService(taskIp)) return;
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
        if (iRealIpService.isRealTimerService(taskIp)) return;
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
        if (iRealIpService.isRealTimerService(taskIp)) return;
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
        if (iRealIpService.isRealTimerService(taskIp)) return;
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
        if (iRealIpService.isRealTimerService(taskIp)) return;
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
        if (iRealIpService.isRealTimerService(taskIp)) return;
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
        if (iRealIpService.isRealTimerService(taskIp)) return;
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
        if(requestFlowList.size()!=0){
            //3、对请求失败的进行重试
            log.info("开始执行重试任务=======>");
            retryJob(requestFlowList,condition.getType());
            if(requestFlowList.size()<1000){
                return;
            }else {
                RequestFlow lastOne = requestFlowList.get(requestFlowList.size()-1);
                condition.setId(lastOne.getId());
                executor(condition);
            }
        }
    }

    /**
     * 记录时间
     *
     * @param startTime
     * @param method
     */
    private void recordTime(Long startTime, String method) {
        Long endTime = Calendar.getInstance().getTimeInMillis();
        try {
            TimeRecord timeRecord = new TimeRecord();
            timeRecord.setUseTime((endTime - startTime));
            timeRecord.setEndTime(Calendar.getInstance().getTime());
            timeRecord.setStartTime(new Date(startTime));
            timeRecord.setMethod(method);
            timeRecordService.insert(timeRecord);
        } catch (Exception e) {
            log.error("时间记录失败", e);
        }
    }

    private void retryJob(List<RequestFlow> requestFlows,String type){
        final ExecutorService fixedThreadPool = Executors.newFixedThreadPool(EXECUTOR_SIZE);
        Long startTime = Calendar.getInstance().getTimeInMillis();
        log.info("重试任务开始======>");
        //2.获取单个商品池内商品sku编号
        try {
            //1、对需要进行重试订单进行分类
            final CountDownLatch begin = new CountDownLatch(1);
            // 结束的倒数锁
            final CountDownLatch end = new CountDownLatch(requestFlows.size());
            for (RequestFlow flow :requestFlows){
                Runnable runnable = new Runnable(){
                    @Override
                    public void run() {
                        try {
                            switch (type){
                                case BRAND_UPDATE_NOTICE:
                                    brandUpdateNoticeRetry(flow);
                                    break;
                                case PROPERTY_UPDATE_NOTICE:
                                    propertyUpdateNoticeRetry(flow);
                                    break;
                                case CATEFORY_UPDATE_NOTICE:
                                    categoryUpdateNoticeRetry(flow);
                                    break;
                                case CATEFORY_BRAND_UPDATE_NOTICE:
                                    categoryBrandUpdateNoticeRetry(flow);
                                    break;
                                case CATEFORY_PROPERTY_UPDATE_NOTICE:
                                    categoryPropertyUpdateNoticeRetry(flow);
                                    break;
                                case ITEM_UPDATE_NOTICE:
                                    itemUpdateNoticeRetry(flow);
                                    break;
                                case EXTERNAL_ITEM_UPDATE_NOTICE:
                                    externalItemUpdateNoticeRetry(flow);
                                    break;
                                case CHANNEL_RECEIVE_ORDER_SUBMIT_RESULT:
                                    channelReceiveOrderSubmitResultRetry(flow);
                                    break;
                                default:
                                    sendLogisticsInfoToChannelRetry(flow);
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
            Long endTime = Calendar.getInstance().getTimeInMillis();
            log.info("重试任务完成,耗时" + (endTime - startTime) / 1000 + "秒");
            recordTime(startTime, Thread.currentThread().getStackTrace()[1].getMethodName());
        }catch (Exception e){
            log.error("重试任务异常："+e.getMessage(),e);
        }
    }

    //品牌变更通知重试
    private void brandUpdateNoticeRetry(RequestFlow requestFlow) throws Exception{
        try {
            log.info("重试请求参数："+requestFlow.getRequestParam());
            String requestParam = requestFlow.getRequestParam();
            if (StringUtils.isNotBlank(requestParam)){
                boolean result = getNextExecut(requestFlow,RequestFlowTypeEnum.BRAND_UPDATE_NOTICE.getCode());
                if (result){
                    ToGlyResultDO resultDO = trcService.sendBrandNotice(trcConfig.getBrandUrl(),requestFlow.getRequestParam());
                    if (ZeroToNineEnum.ONE.getCode().equals(resultDO.getStatus())){
                        RequestFlow tem = new RequestFlow();
                        tem.setRequestNum(requestFlow.getRequestNum());
                        tem.setStatus(NetwordStateEnum.SUCCESS.getCode());
                        int count = requestFlowService.changeState(tem);
                        if (count==0){
                            log.error("品牌变更通知重试：更新状态失败！");
                        }
                    }
                }
            }else {
                log.error("请求参数为空");
            }
        }catch (Exception e){
            log.error("品牌变更通知重试异常："+e.getMessage(),e);
        }

    }

    //属性变更通知重试
    private void propertyUpdateNoticeRetry(RequestFlow requestFlow) throws Exception{
        try {
            log.info("重试请求参数："+requestFlow.getRequestParam());
            String requestParam = requestFlow.getRequestParam();
            if (StringUtils.isNotBlank(requestParam)){
                boolean result = getNextExecut(requestFlow,RequestFlowTypeEnum.PROPERTY_UPDATE_NOTICE.getCode());
                if (result){
                    ToGlyResultDO resultDO = trcService.sendPropertyNotice(trcConfig.getPropertyUrl(),requestFlow.getRequestParam());
                    if (ZeroToNineEnum.ONE.getCode().equals(resultDO.getStatus())){
                        RequestFlow tem = new RequestFlow();
                        tem.setRequestNum(requestFlow.getRequestNum());
                        tem.setStatus(NetwordStateEnum.SUCCESS.getCode());
                        int count = requestFlowService.changeState(tem);
                        if (count==0){
                            log.error("属性变更通知重试：更新状态失败！");
                        }
                    }
                }
            }else {
                log.error("请求参数为空");
            }
        }catch (Exception e){
            log.error("属性变更通知重试异常："+e.getMessage(),e);
        }
    }

    //分类变更通知重试
    private void categoryUpdateNoticeRetry(RequestFlow requestFlow) throws Exception{
        try {
            log.info("重试请求参数："+requestFlow.getRequestParam());
            String requestParam = requestFlow.getRequestParam();
            if (StringUtils.isNotBlank(requestParam)){
                boolean result = getNextExecut(requestFlow,RequestFlowTypeEnum.CATEFORY_UPDATE_NOTICE.getCode());
                if (result){
                    ToGlyResultDO resultDO = trcService.sendCategoryToTrc(trcConfig.getBrandUrl(),requestFlow.getRequestParam());
                    if (ZeroToNineEnum.ONE.getCode().equals(resultDO.getStatus())){
                        RequestFlow tem = new RequestFlow();
                        tem.setRequestNum(requestFlow.getRequestNum());
                        tem.setStatus(NetwordStateEnum.SUCCESS.getCode());
                        int count = requestFlowService.changeState(tem);
                        if (count==0){
                            log.error("分类变更通知重试：更新状态失败！");
                        }
                    }
                }
            }else {
                log.error("请求参数为空");
            }
        }catch (Exception e){
            log.error("分类变更通知重试异常："+e.getMessage(),e);
        }
    }

    //分类品牌变更通知重试
    private void categoryBrandUpdateNoticeRetry(RequestFlow requestFlow) throws Exception{
        try {
            log.info("重试请求参数："+requestFlow.getRequestParam());
            String requestParam = requestFlow.getRequestParam();
            if (StringUtils.isNotBlank(requestParam)){
                boolean result = getNextExecut(requestFlow,RequestFlowTypeEnum.CATEFORY_BRAND_UPDATE_NOTICE.getCode());
                if (result){
                    ToGlyResultDO resultDO = trcService.sendCategoryBrandList(trcConfig.getCategoryBrandUrl(),requestFlow.getRequestParam());
                    if (ZeroToNineEnum.ONE.getCode().equals(resultDO.getStatus())){
                        RequestFlow tem = new RequestFlow();
                        tem.setRequestNum(requestFlow.getRequestNum());
                        tem.setStatus(NetwordStateEnum.SUCCESS.getCode());
                        int count = requestFlowService.changeState(tem);
                        if (count==0){
                            log.error("分类品牌变更通知重试：更新状态失败！");
                        }
                    }
                }
            }else {
                log.error("请求参数为空");
            }
        }catch (Exception e){
            log.error("分类品牌变更通知重试异常："+e.getMessage(),e);
        }
    }

    //分类属性变更通知重试
    private void categoryPropertyUpdateNoticeRetry(RequestFlow requestFlow) throws Exception{
        try {
            log.info("重试请求参数："+requestFlow.getRequestParam());
            String requestParam = requestFlow.getRequestParam();
            if (StringUtils.isNotBlank(requestParam)){
                boolean result = getNextExecut(requestFlow,RequestFlowTypeEnum.CATEFORY_PROPERTY_UPDATE_NOTICE.getCode());
                if (result){
                    ToGlyResultDO resultDO = trcService.sendCategoryPropertyList(trcConfig.getCategoryPropertyUrl(),requestFlow.getRequestParam());
                    if (ZeroToNineEnum.ONE.getCode().equals(resultDO.getStatus())){
                        RequestFlow tem = new RequestFlow();
                        tem.setRequestNum(requestFlow.getRequestNum());
                        tem.setStatus(NetwordStateEnum.SUCCESS.getCode());
                        int count = requestFlowService.changeState(tem);
                        if (count==0){
                            log.error("分类属性变更通知重试：更新状态失败！");
                        }
                    }
                }
            }else {
                log.error("请求参数为空");
            }
        }catch (Exception e){
            log.error("分类属性变更通知重试异常："+e.getMessage(),e);
        }
    }

    //自营商品变更通知重试
    private void itemUpdateNoticeRetry(RequestFlow requestFlow) throws Exception{
        try {
            log.info("重试请求参数："+requestFlow.getRequestParam());
            String requestParam = requestFlow.getRequestParam();
            if (StringUtils.isNotBlank(requestParam)){
                boolean result = getNextExecut(requestFlow,RequestFlowTypeEnum.ITEM_UPDATE_NOTICE.getCode());
                if (result){
                    ToGlyResultDO resultDO = trcService.sendItemsNotice(trcConfig.getItemUrl(),requestFlow.getRequestParam());
                    if (ZeroToNineEnum.ONE.getCode().equals(resultDO.getStatus())){
                        RequestFlow tem = new RequestFlow();
                        tem.setRequestNum(requestFlow.getRequestNum());
                        tem.setStatus(NetwordStateEnum.SUCCESS.getCode());
                        int count = requestFlowService.changeState(tem);
                        if (count==0){
                            log.error("自营商品变更通知重试：更新状态失败！");
                        }
                    }
                }
            }else {
                log.error("请求参数为空");
            }
        }catch (Exception e){
            log.error("自营商品变更通知重试异常："+e.getMessage(),e);
        }
    }

    //代发商品变更通知重试
    private void externalItemUpdateNoticeRetry(RequestFlow requestFlow) throws Exception{
        try {
            log.info("重试请求参数："+requestFlow.getRequestParam());
            String requestParam = requestFlow.getRequestParam();
            if (StringUtils.isNotBlank(requestParam)){
                boolean result = getNextExecut(requestFlow,RequestFlowTypeEnum.EXTERNAL_ITEM_UPDATE_NOTICE.getCode());
                if (result){
                    ToGlyResultDO resultDO = trcService.sendPropertyNotice(trcConfig.getExternalItemSkuUpdateUrl(),requestParam);
                    if (ZeroToNineEnum.ONE.getCode().equals(resultDO.getStatus())){
                        RequestFlow tem = new RequestFlow();
                        tem.setRequestNum(requestFlow.getRequestNum());
                        tem.setStatus(NetwordStateEnum.SUCCESS.getCode());
                        int count = requestFlowService.changeState(tem);
                        if (count==0){
                            log.error("代发商品变更通知重试：更新状态失败！");
                        }
                    }
                }
            }else {
                log.error("请求参数为空");
            }
        }catch (Exception e){
            log.error("代发商品变更通知重试异常："+e.getMessage(),e);
        }
    }

    //渠道接收订单提交结果重试
    private void channelReceiveOrderSubmitResultRetry(RequestFlow requestFlow) throws Exception{
        try {
            log.info("重试请求参数："+requestFlow.getRequestParam());
            String requestParam = requestFlow.getRequestParam();
            if (StringUtils.isNotBlank(requestParam)){
                boolean result = getNextExecut(requestFlow,RequestFlowTypeEnum.CHANNEL_RECEIVE_ORDER_SUBMIT_RESULT.getCode());
                if (result){
                    //执行重试
                    ChannelOrderResponse channelOrderResponse = JSONObject.parseObject(requestParam,ChannelOrderResponse.class);
                    ToGlyResultDO resultDO = trcService.sendOrderSubmitResultNotice(channelOrderResponse);
                    if (ZeroToNineEnum.ONE.getCode().equals(resultDO.getStatus())){
                        RequestFlow tem = new RequestFlow();
                        tem.setRequestNum(requestFlow.getRequestNum());
                        tem.setStatus(NetwordStateEnum.SUCCESS.getCode());
                        int count = requestFlowService.changeState(tem);
                        if (count==0){
                            log.error("渠道接收订单提交结果重试：更新状态失败！");
                        }
                    }
                }
            }else {
                log.error("请求参数为空");
            }
        }catch (Exception e){
            log.error("渠道接收订单提交结果重试异常："+e.getMessage(),e);
        }
    }

    //解析requestParam

    //发送物流信息给渠道重试
    private void sendLogisticsInfoToChannelRetry(RequestFlow requestFlow) throws Exception{
        try {
            log.info("重试请求参数："+requestFlow.getRequestParam());
            String requestParam = requestFlow.getRequestParam();
            if (StringUtils.isNotBlank(requestParam)){
                boolean result = getNextExecut(requestFlow,RequestFlowTypeEnum.SEND_LOGISTICS_INFO_TO_CHANNEL.getCode());
                if (result){
                    //执行重试
                    LogisticNoticeForm logisticNoticeForm = JSONObject.parseObject(requestParam,LogisticNoticeForm.class);
                    ToGlyResultDO resultDO = trcService.sendLogisticInfoNotice(logisticNoticeForm);
                    if (ZeroToNineEnum.ONE.getCode().equals(resultDO.getStatus())){
                        RequestFlow tem = new RequestFlow();
                        tem.setRequestNum(requestFlow.getRequestNum());
                        tem.setStatus(NetwordStateEnum.SUCCESS.getCode());
                        int count = requestFlowService.changeState(tem);
                        if (count==0){
                            log.error("发送物流信息给渠道重试：更新状态失败！");
                        }
                    }
                }
            }else {
                log.error("请求参数为空");
            }
        }catch (Exception e){
            log.error("发送物流信息给渠道重试异常："+e.getMessage(),e);
        }
    }

    private boolean getNextExecut(RequestFlow requestFlow,String type) throws Exception{
        try {
            if (null == requestFlow.getCount()){
                requestFlow.setCount(0L);
            }
            if (null != requestFlow && StringUtils.isNotBlank(type)){
                RetryConfig retryConfig = new RetryConfig();
                retryConfig.setType(type);
                RetryConfig record = retryConfigService.selectOne(retryConfig);
                //1.1计算结束启动时间
                RequestFlow flow = new RequestFlow();
                Long time = Calendar.getInstance().getTimeInMillis()+record.getPeriod()*hours;
                Date endTime = new Date(time);
                if (null == requestFlow.getEndTime()){
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
                c1.setTime(null != requestFlow.getEndTime()? requestFlow.getEndTime():endTime);
                int result = c1.compareTo(c2);
                //1.2判断是否满足重试条件
                if (1!=record.getCount().compareTo(requestFlow.getCount()) || 1 != result){
                    RequestFlow tem = new RequestFlow();
                    tem.setRequestNum(requestFlow.getRequestNum());
                    tem.setStatus(NetwordStateEnum.ERROR.getCode());
                    int j = requestFlowService.changeState(tem);
                    if (j<=0){
                        log.error("流水记录表：不满足重试条件，更新状态失败！");
                    }
                    return false;
                }
                return true;
            }else {
                log.error("输入参数为空！");
                return false;
            }
        }catch (Exception e){
            log.error("重试机制更改下次执行状态异常："+e.getMessage(),e);
            return false;
        }
    }


}
