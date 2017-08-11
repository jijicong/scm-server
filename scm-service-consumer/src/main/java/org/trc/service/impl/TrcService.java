package org.trc.service.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.protocol.HTTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.trc.enums.SuccessFailureEnum;
import org.trc.enums.ZeroToNineEnum;
import org.trc.form.ChannelOrderResponse;
import org.trc.form.LogisticNoticeForm;
import org.trc.form.TrcConfig;
import org.trc.model.ToGlyResultDO;
import org.trc.service.ITrcService;
import org.trc.util.AssertUtil;
import org.trc.util.DateUtils;
import org.trc.util.HttpClientUtil;
import org.trc.util.HttpResult;

import java.net.ConnectException;
import java.util.Calendar;


/**
 * 通知泰然城
 * Created by hzdzf on 2017/6/6.
 */
@Service("trcService")
public class TrcService implements ITrcService {

    private final static Logger log = LoggerFactory.getLogger(TrcService.class);

    private final static int TIME_OUT = 3000;

    public final static Integer statusCode = 200;

    //接口重试次数
    public final static Integer RETRY_TIMES = 3;

    @Autowired
    private TrcConfig trcConfig;

    @Override
    public ToGlyResultDO sendBrandNotice(String brandUrl, String params) throws Exception {
        return invokeUpdateNotice(brandUrl, params);
    }

    @Override
    public ToGlyResultDO sendPropertyNotice(String propertyUrl, String params) throws Exception {
        return invokeUpdateNotice(propertyUrl, params);
    }

    //发送商品改动
    @Override
    public ToGlyResultDO sendItemsNotice(String itemsUrl, String params) throws Exception {
        return invokeUpdateNotice(itemsUrl, params);
    }

    @Override
    public String getJDLogistic(String getJDLogisticUrl) throws Exception {
        return HttpClientUtil.httpGetRequest(getJDLogisticUrl);
    }

    //发送分类属性改动
    @Override
    public ToGlyResultDO sendCategoryPropertyList(String categoryPropertyUrl, String params) throws Exception {
        return invokeUpdateNotice(categoryPropertyUrl, params);
    }

    //发送分类品牌改动
    @Override
    public ToGlyResultDO sendCategoryBrandList(String categoryBrandUrl, String params) throws Exception {
        return invokeUpdateNotice(categoryBrandUrl, params);
    }

    //发送分类改动
    @Override
    public ToGlyResultDO sendCategoryToTrc(String categoryUrl, String params) throws Exception {
        return invokeUpdateNotice(categoryUrl, params);
    }

    /**
     *
     * @param url
     * @param params
     * @return
     */
    private ToGlyResultDO invokeUpdateNotice(String url, String params){
        if(StringUtils.equals(trcConfig.getNoticeChannal(), ZeroToNineEnum.ZERO.getCode())){//不通知
            return new ToGlyResultDO(SuccessFailureEnum.SUCCESS.getCode(), "通知渠道开关关闭");
        }
        log.debug("开始调用泰然城信息更新同步服务" + url + ", 参数：" + params + ". 开始时间" +
                DateUtils.dateToString(Calendar.getInstance().getTime(), DateUtils.DATETIME_FORMAT));
        ToGlyResultDO toGlyResultDO = new ToGlyResultDO();
        toGlyResultDO.setStatus(SuccessFailureEnum.FAILURE.getCode());
        String response = null;
        try{
            HttpPost httpPost = new HttpPost(url);
            httpPost.addHeader(HTTP.CONTENT_TYPE,"text/plain; charset=utf-8");
            httpPost.setHeader("Accept", "application/json");
            response = HttpClientUtil.httpPostJsonRequest(url, params, httpPost, TIME_OUT);
            if(StringUtils.isNotBlank(response)){
                JSONObject jbo = JSONObject.parseObject(response);
                toGlyResultDO = jbo.toJavaObject(ToGlyResultDO.class);
            }else {
                toGlyResultDO.setMsg("调用泰然城信息更新同步服务返回结果为空");
            }
        }catch (Exception e){
            String msg = String.format("调用泰然城信息更新同步服务%s异常,错误信息:%s", url, e.getMessage());
            log.error(msg, e);
            toGlyResultDO.setMsg(msg);
        }
        log.debug("结束泰然城信息更新同步服务" + url + ", 返回结果：" + response + ". 结束时间" +
                DateUtils.dateToString(Calendar.getInstance().getTime(), DateUtils.DATETIME_FORMAT));
        return toGlyResultDO;
    }

    @Override
    public ToGlyResultDO sendOrderSubmitResultNotice(ChannelOrderResponse channelOrderResponse) {
        if(StringUtils.equals(trcConfig.getNoticeChannal(), ZeroToNineEnum.ZERO.getCode())){//不通知
            return new ToGlyResultDO(SuccessFailureEnum.SUCCESS.getCode(), "通知渠道开关关闭");
        }
        AssertUtil.notNull(channelOrderResponse, "同步订单提交结果给渠道参数不能为空");
        String url = trcConfig.getOrderSubmitNotifyUrl();
        String paramObj = JSON.toJSON(channelOrderResponse).toString();
        ToGlyResultDO toGlyResultDO = new ToGlyResultDO();
        toGlyResultDO.setStatus(SuccessFailureEnum.FAILURE.getCode());
        log.debug("开始调用同步订单提交结果给渠服务" + url + ", 参数：" + paramObj + ". 开始时间" +
                DateUtils.dateToString(Calendar.getInstance().getTime(), DateUtils.DATETIME_FORMAT));
        String response = null;
        try{
            HttpPost httpPost = new HttpPost(url);
            httpPost.addHeader(HTTP.CONTENT_TYPE,"text/plain; charset=utf-8");
            httpPost.setHeader("Accept", "application/json");
            response = HttpClientUtil.httpPostJsonRequest(url, paramObj, httpPost, TIME_OUT);
            if(StringUtils.isNotBlank(response)){
                JSONObject jbo = JSONObject.parseObject(response);
                toGlyResultDO = jbo.toJavaObject(ToGlyResultDO.class);
            }else {
                toGlyResultDO.setMsg("调用同步订单提交结果给渠服务返回结果为空");
            }
        }catch (Exception e){
            String msg = String.format("调用同步订单提交结果给渠服务异常,错误信息:%s", e.getMessage());
            log.error(msg, e);
            toGlyResultDO.setMsg(msg);
        }
        log.debug("结束同步订单提交结果给渠服务" + url + ", 返回结果：" + JSONObject.toJSON(toGlyResultDO) + ". 结束时间" +
                DateUtils.dateToString(Calendar.getInstance().getTime(), DateUtils.DATETIME_FORMAT));
        return toGlyResultDO;
    }

    @Override
    public ToGlyResultDO sendLogisticInfoNotice(LogisticNoticeForm logisticNoticeForm) {
        if(StringUtils.equals(trcConfig.getNoticeChannal(), ZeroToNineEnum.ZERO.getCode())){//不通知
            return new ToGlyResultDO(SuccessFailureEnum.SUCCESS.getCode(), "通知渠道开关关闭");
        }
        AssertUtil.notNull(logisticNoticeForm, "同步物理新给渠道参数logisticNoticeForm不能为空");
        AssertUtil.notBlank(logisticNoticeForm.getShopOrderCode(), "同步物理新给渠道店铺订单编码shopOrderCode不能为空");
        AssertUtil.notBlank(logisticNoticeForm.getType(), "同步物理新给渠道信息类型type不能为空");
        AssertUtil.notEmpty(logisticNoticeForm.getLogistics(), "同步物理新给渠道物流信息logistics不能为空");
        String url = trcConfig.getLogisticsNotifyUrl();
        String paramObj = JSON.toJSONString(logisticNoticeForm);
        ToGlyResultDO toGlyResultDO = new ToGlyResultDO();
        toGlyResultDO.setStatus(SuccessFailureEnum.FAILURE.getCode());
        log.debug("开始调用同步物流信息给渠道服务" + url + ", 参数：" + paramObj + ". 开始时间" +
                DateUtils.dateToString(Calendar.getInstance().getTime(), DateUtils.DATETIME_FORMAT));
        String response = null;
        try{
            HttpPost httpPost = new HttpPost(url);
            httpPost.addHeader(HTTP.CONTENT_TYPE,"text/plain; charset=utf-8");
            httpPost.setHeader("Accept", "application/json");
            response = HttpClientUtil.httpPostJsonRequest(url, paramObj, httpPost, TIME_OUT);
            if(StringUtils.isNotBlank(response)){
                JSONObject json = JSONObject.parseObject(response);
                HttpResult httpResult = json.toJavaObject(HttpResult.class);
                JSONObject jbo = JSONObject.parseObject(httpResult.getResult());
                toGlyResultDO = jbo.toJavaObject(ToGlyResultDO.class);
                //具体业务重试代码设置状态
                if (toGlyResultDO.getStatus().equals("1")){
                    toGlyResultDO.setStatus(SuccessFailureEnum.SUCCESS.getCode());
                    toGlyResultDO.setMsg("处理成功！");
                }
                if (toGlyResultDO.getStatus().equals("2")){
                    toGlyResultDO.setStatus(SuccessFailureEnum.ERROR.getCode());
                    toGlyResultDO.setMsg("异常数据！");
                }

            }else {
                toGlyResultDO.setMsg("调用同步物流信息给渠道服务返回结果为空");
            }
        }catch (ConnectException e){
            toGlyResultDO.setStatus(SuccessFailureEnum.SOCKET_TIME_OUT.getCode());
            String msg = String.format("调用同步物流信息给渠道服务异常,错误信息:%s", e.getMessage());
            log.error(msg, e);
            toGlyResultDO.setMsg(msg);
        }catch (Exception e){
            String msg = String.format("调用同步物流信息给渠道服务异常,错误信息:%s", e.getMessage());
            log.error(msg, e);
            toGlyResultDO.setMsg(msg);
        }
        log.debug("结束同步物流信息给渠道服务" + url + ", 返回结果：" + JSONObject.toJSON(toGlyResultDO) + ". 结束时间" +
                DateUtils.dateToString(Calendar.getInstance().getTime(), DateUtils.DATETIME_FORMAT));
        return toGlyResultDO;
    }



}
