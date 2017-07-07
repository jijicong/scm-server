package org.trc.service.impl;


import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.trc.enums.SuccessFailureEnum;
import org.trc.enums.ZeroToNineEnum;
import org.trc.form.ChannelOrderResponse;
import org.trc.form.JDModel.ReturnTypeDO;
import org.trc.form.LogisticNoticeForm;
import org.trc.form.TrcConfig;
import org.trc.model.ToGlyResultDO;
import org.trc.service.ITrcService;
import org.trc.util.*;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


/**
 * 通知泰然城
 * Created by hzdzf on 2017/6/6.
 */
@Service("trcService")
public class TrcService implements ITrcService {

    private final static Logger log = LoggerFactory.getLogger(TrcService.class);

    private final static int TIME_OUT = 3000;

    @Autowired
    private TrcConfig trcConfig;

    @Override
    public String sendBrandNotice(String brandUrl, String params) throws Exception {
        return HttpClientUtil.httpPostJsonRequest(brandUrl, params, 10000);
    }

    @Override
    public String sendPropertyNotice(String propertyUrl, String params) throws Exception {
        return HttpClientUtil.httpPostJsonRequest(propertyUrl, params, 10000);
    }

    //发送商品改动
    @Override
    public String sendItemsNotice(String itemsUrl, String params) throws Exception {
        return HttpClientUtil.httpPostJsonRequest(itemsUrl, params, 10000);
    }

    @Override
    public String getJDLogistic(String getJDLogisticUrl) throws Exception {
        return HttpClientUtil.httpGetRequest(getJDLogisticUrl);
    }

    //发送分类属性改动
    @Override
    public String sendCategoryPropertyList(String categoryPropertyUrl, String params) throws Exception {
        return HttpClientUtil.httpPostJsonRequest(categoryPropertyUrl, params, 10000);
    }

    //发送分类品牌改动
    @Override
    public String sendCategoryBrandList(String categoryBrandUrl, String params) throws Exception {
        return HttpClientUtil.httpPostJsonRequest(categoryBrandUrl, params, 10000);
    }

    //发送分类改动
    @Override
    public String sendCategoryToTrc(String categoryUrl, String params) throws Exception {
        return HttpClientUtil.httpPostJsonRequest(categoryUrl, params, 10000);
    }

    @Override
    public ToGlyResultDO sendOrderSubmitResultNotice(ChannelOrderResponse channelOrderResponse) {
        AssertUtil.notNull(channelOrderResponse, "同步订单提交结果给渠道参数不能为空");
        String url = trcConfig.getOrderSubmitNotifyUrl();
        Map<String, Object> map = BeanToMapUtil.convertBeanToMap(channelOrderResponse);
        log.debug("开始调用同步订单提交结果给渠服务" + url + ", 参数：" + JSONObject.toJSON(map) + ". 开始时间" +
                DateUtils.dateToString(Calendar.getInstance().getTime(), DateUtils.DATETIME_FORMAT));
        ToGlyResultDO toGlyResultDO = new ToGlyResultDO();
        toGlyResultDO.setStatus(SuccessFailureEnum.FAILURE.getCode());
        String response = null;
        try{
            response = HttpClientUtil.httpPostRequest(url, map, TIME_OUT);
            if(StringUtils.isNotBlank(response)){
                JSONObject jbo = JSONObject.parseObject(response);
                AppResult appResult = jbo.toJavaObject(AppResult.class);
                if(StringUtils.equals(appResult.getAppcode(), ZeroToNineEnum.ONE.getCode())){
                    toGlyResultDO.setStatus(SuccessFailureEnum.SUCCESS.getCode());
                }
                toGlyResultDO.setMsg(appResult.getDatabuffer());
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
        AssertUtil.notNull(logisticNoticeForm, "同步物理新给渠道参数logisticNoticeForm不能为空");
        AssertUtil.notBlank(logisticNoticeForm.getShopOrderCode(), "同步物理新给渠道店铺订单编码shopOrderCode不能为空");
        AssertUtil.notBlank(logisticNoticeForm.getType(), "同步物理新给渠道信息类型type不能为空");
        AssertUtil.notEmpty(logisticNoticeForm.getLogistics(), "同步物理新给渠道物流信息logistics不能为空");
        String url = trcConfig.getLogisticsNotifyUrl();
        Map<String, Object> map = BeanToMapUtil.convertBeanToMap(logisticNoticeForm);
        log.debug("开始调用同步物流信息给渠道服务" + url + ", 参数：" + JSONObject.toJSON(map) + ". 开始时间" +
                DateUtils.dateToString(Calendar.getInstance().getTime(), DateUtils.DATETIME_FORMAT));
        ToGlyResultDO toGlyResultDO = new ToGlyResultDO();
        toGlyResultDO.setStatus(SuccessFailureEnum.FAILURE.getCode());
        String response = null;
        try{
            response = HttpClientUtil.httpPostRequest(url, map, TIME_OUT);
            if(StringUtils.isNotBlank(response)){
                JSONObject jbo = JSONObject.parseObject(response);
                AppResult appResult = jbo.toJavaObject(AppResult.class);
                if(StringUtils.equals(appResult.getAppcode(), ZeroToNineEnum.ONE.getCode())){
                    toGlyResultDO.setStatus(SuccessFailureEnum.SUCCESS.getCode());
                }
                toGlyResultDO.setMsg(appResult.getDatabuffer());
            }else {
                toGlyResultDO.setMsg("调用同步物流信息给渠道服务返回结果为空");
            }
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
