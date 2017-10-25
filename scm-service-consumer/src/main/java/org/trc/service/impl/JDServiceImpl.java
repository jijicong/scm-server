package org.trc.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.protocol.HTTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.trc.enums.ExceptionEnum;
import org.trc.enums.JingDongEnum;
import org.trc.enums.ZeroToNineEnum;
import org.trc.form.JDModel.*;
import org.trc.form.SupplyItemsExt;
import org.trc.form.external.*;
import org.trc.form.liangyou.LiangYouSupplierOrder;
import org.trc.service.IJDService;
import org.trc.util.*;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.*;

/**
 * Created by hzwyz on 2017/5/18 0018.
 */
@Service("jDService")
public class JDServiceImpl implements IJDService {
    private final static Logger log = LoggerFactory.getLogger(JDServiceImpl.class);
    @Autowired
    private ExternalSupplierConfig externalSupplierConfig;
    //接口调用超时时间
    public final static Integer TIME_OUT = 10000;
    //接口重试次数
    public final static Integer RETRY_TIMES = 3;

    @Override
    public ReturnTypeDO<Pagenation<SupplyItemsExt>> skuPage(SupplyItemsExt form, Pagenation<SupplyItemsExt> page) throws Exception {
        AssertUtil.notNull(page.getPageNo(), "分页查询参数pageNo不能为空");
        AssertUtil.notNull(page.getPageSize(), "分页查询参数pageSize不能为空");
        AssertUtil.notNull(page.getStart(), "分页查询参数start不能为空");
        Map<String, Object> map = BeanToMapUtil.convertBeanToMap(page);
        map.putAll(BeanToMapUtil.convertBeanToMap(form));
        ReturnTypeDO<Pagenation<SupplyItemsExt>> returnTypeDO = new ReturnTypeDO<Pagenation<SupplyItemsExt>>();
        returnTypeDO.setSuccess(false);
        String response = null;
        try{
            String url = externalSupplierConfig.getScmExternalUrl()+externalSupplierConfig.getSkuPageUrl();
            response = HttpClientUtil.httpGetRequest(url, map);
            if(StringUtils.isNotBlank(response)){
                JSONObject jbo = JSONObject.parseObject(response);
                AppResult appResult = jbo.toJavaObject(AppResult.class);
                if(StringUtils.equals(appResult.getAppcode(), ZeroToNineEnum.ONE.getCode())){
                    page = jbo.getJSONObject("result").toJavaObject(Pagenation.class);
                    List<SupplyItemsExt> supplyItemsExtList = new ArrayList<SupplyItemsExt>();
                    for(Object obj: page.getResult()){
                        JSONObject bo = (JSONObject)obj;
                        SupplyItemsExt supplyItemsExt = (SupplyItemsExt)bo.toJavaObject(SupplyItemsExt.class);
                        supplyItemsExt.setSupplierPrice(new BigDecimal(CommonUtil.getMoneyYuan(supplyItemsExt.getSupplierPrice())));
                        supplyItemsExt.setSupplyPrice(new BigDecimal(CommonUtil.getMoneyYuan(supplyItemsExt.getSupplyPrice())));
                        supplyItemsExt.setMarketPrice(new BigDecimal(CommonUtil.getMoneyYuan(supplyItemsExt.getMarketPrice())));
                        supplyItemsExt.setSkuName(bo.getString("name"));
                        supplyItemsExt.setBrand(bo.getString("brandName"));
                        supplyItemsExtList.add(supplyItemsExt);
                    }
                    page.setResult(supplyItemsExtList);
                    returnTypeDO.setSuccess(true);
                    returnTypeDO.setResult(page);
                }
                returnTypeDO.setResultMessage(appResult.getDatabuffer());
            }else {
                returnTypeDO.setResultMessage("调用外部供应商商品查询接口返回结果为空");
            }
        }catch (IOException e){
            String msg = String.format("调用京东商品查询服务网络超时,错误信息:%s", e.getMessage());
            log.error(msg, e);
            returnTypeDO.setResultMessage("调用京东商品查询服务网络超时");
        } catch (Exception e){
            String msg = String.format("调用外部供应商商品查询接口异常,错误信息:%s", e.getMessage());
            log.error(msg, e);
            returnTypeDO.setResultMessage(msg);
        }
        return returnTypeDO;
    }


    @Override
    public ReturnTypeDO noticeUpdateSkuUsedStatus(List<SkuDO> skuDOList) {
        AssertUtil.notEmpty(skuDOList, "调用外部供应商商品使用状态更新参数skuDOList不能为空");
        ReturnTypeDO returnTypeDO = new ReturnTypeDO();
        returnTypeDO.setSuccess(false);
        String response = null;
        try{
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("skus", JSONArray.toJSON(skuDOList));
            String url = externalSupplierConfig.getScmExternalUrl()+externalSupplierConfig.getSkuAddNotice();
            response = HttpClientUtil.httpPostRequest(url, map, TIME_OUT);
            if(StringUtils.isNotBlank(response)){
                JSONObject jbo = JSONObject.parseObject(response);
                AppResult appResult = jbo.toJavaObject(AppResult.class);
                if(StringUtils.equals(appResult.getAppcode(), ZeroToNineEnum.ONE.getCode())){
                    returnTypeDO.setSuccess(true);
                }
                returnTypeDO.setResultMessage(appResult.getDatabuffer());
            }else {
                returnTypeDO.setResultMessage("调用外部供应商品使用状态更新接口返回结果为空");
            }
        }catch (Exception e){
            String msg = String.format("调用外部供应商商品使用状态更新接口异常,错误信息:%s", e.getMessage());
            log.error(msg, e);
            returnTypeDO.setResultMessage(msg);
        }
        return returnTypeDO;
    }

    @Override
    public ResponseAck submitJingDongOrder(JingDongSupplierOrder jingDongOrder) {
        AssertUtil.notNull(jingDongOrder, "提交京东订单参数不能为空");
        String url = externalSupplierConfig.getScmExternalUrl()+externalSupplierConfig.getJdSubmitOrderUrl();
        return invokeSubmitOrder(url, JSON.toJSON(jingDongOrder).toString());
    }

    @Override
    public ResponseAck submitLiangYouOrder(LiangYouSupplierOrder liangYouOrder) {
        AssertUtil.notNull(liangYouOrder, "提交粮油订单参数不能为空");
        String url = externalSupplierConfig.getScmExternalUrl()+externalSupplierConfig.getLySubmitOrderUrl();
        return invokeSubmitOrder(url, JSON.toJSON(liangYouOrder).toString());
    }

    @Override
    public ReturnTypeDO getLogisticsInfo(String warehouseOrderCode, String flag) {
        AssertUtil.notBlank(warehouseOrderCode, "查询代发供应商订单物流信息参数仓库订单编码不能为空");
        ReturnTypeDO returnTypeDO = new ReturnTypeDO();
        returnTypeDO.setSuccess(false);
        String url = "";
        String response = null;
        try{
            url = externalSupplierConfig.getScmExternalUrl()+externalSupplierConfig.getOrderLogisticsUrl()+"/"+warehouseOrderCode+"/"+flag;
            log.debug("开始调用物流查询" + url + ", 参数：warehouseOrderCode=" + warehouseOrderCode + "flag="+flag+". 开始时间" +
                    DateUtils.dateToString(Calendar.getInstance().getTime(), DateUtils.DATETIME_FORMAT));
            response = HttpClientUtil.httpGetRequest(url);
            if(StringUtils.isNotBlank(response)){
                JSONObject jbo = JSONObject.parseObject(response);
                AppResult appResult = jbo.toJavaObject(AppResult.class);
                if(StringUtils.equals(appResult.getAppcode(), ZeroToNineEnum.ONE.getCode())){
                    returnTypeDO.setSuccess(true);
                    returnTypeDO.setResult(appResult.getResult());
                }
                returnTypeDO.setResultMessage(appResult.getDatabuffer());
            }else {
                returnTypeDO.setResultMessage("调用物流查询服务返回结果为空");
            }
        }catch (Exception e){
            String msg = String.format("调用物流查询服务异常,错误信息:%s", e.getMessage());
            log.error(msg, e);
            returnTypeDO.setResultMessage(msg);
        }
        log.debug("结束调用物流查询" + url + ", 返回结果：" + JSONObject.toJSON(returnTypeDO) + ". 结束时间" +
                DateUtils.dateToString(Calendar.getInstance().getTime(), DateUtils.DATETIME_FORMAT));

        return returnTypeDO;
    }

    @Override
    public ReturnTypeDO getSellPrice(String skus) {
        String url = externalSupplierConfig.getScmExternalUrl()+externalSupplierConfig.getJdSkuPriceUrl();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("skus", skus);
        log.debug("开始调用京东sku价格查询服务" + url + ", 参数：" + map + ". 开始时间" +
                DateUtils.dateToString(Calendar.getInstance().getTime(), DateUtils.DATETIME_FORMAT));
        ReturnTypeDO returnTypeDO = new ReturnTypeDO();
        returnTypeDO.setSuccess(false);
        String response = null;
        try{
            response = HttpClientUtil.httpPostRequest(url, map, TIME_OUT);
            if(StringUtils.isNotBlank(response)){
                JSONObject jbo = JSONObject.parseObject(response);
                AppResult appResult = jbo.toJavaObject(AppResult.class);
                if(StringUtils.equals(appResult.getAppcode(), ZeroToNineEnum.ONE.getCode())){
                    returnTypeDO.setSuccess(true);
                    returnTypeDO.setResult(appResult.getResult());
                }
                returnTypeDO.setResultMessage(appResult.getDatabuffer());
            }else {
                returnTypeDO.setResultMessage("调用京东sku价格查询服务返回结果为空");
            }
        }catch (Exception e){
            String msg = String.format("调用京东sku价格查询服务异常,错误信息:%s", e.getMessage());
            log.error(msg, e);
            returnTypeDO.setResultMessage(msg);
        }
        log.debug("结束调用京东sku价格查询服务" + url + ", 返回结果：" + JSONObject.toJSON(returnTypeDO) + ". 结束时间" +
                DateUtils.dateToString(Calendar.getInstance().getTime(), DateUtils.DATETIME_FORMAT));
        return returnTypeDO;
    }


    private ResponseAck invokeSubmitOrder(String url, String jsonParams){
        ResponseAck responseAck = null;
        log.debug("开始调用提交订单服务" + url + ", 参数：" + jsonParams + ". 开始时间" +
                DateUtils.dateToString(Calendar.getInstance().getTime(), DateUtils.DATETIME_FORMAT));
        String response = null;
        try{
            HttpPost httpPost = new HttpPost(url);
            httpPost.addHeader(HTTP.CONTENT_TYPE,"text/plain; charset=utf-8");
            httpPost.setHeader("Accept", "application/json");
            response = HttpClientUtil.httpPostJsonRequest(url, jsonParams, httpPost, 20000);
            if(StringUtils.isNotBlank(response)){
                JSONObject jbo = JSONObject.parseObject(response);
                responseAck = jbo.toJavaObject(ResponseAck.class);
            }else {
                responseAck = new ResponseAck(ExceptionEnum.SYSTEM_BUSY, "");
            }
        }catch (IOException e){
            String msg = String.format("调用提交订单服务网络超时,错误信息:%s", e.getMessage());
            log.error(msg, e);
            responseAck = new ResponseAck(ExceptionEnum.REMOTE_INVOKE_TIMEOUT_EXCEPTION, "");
        }catch (Exception e){
            String msg = String.format("调用提交订单服务异常,错误信息:%s", e.getMessage());
            log.error(msg, e);
            responseAck = new ResponseAck(ExceptionEnum.SYSTEM_EXCEPTION, "");
        }
        log.debug("结束调用提交订单服务" + url + ", 返回结果：" + JSONObject.toJSON(responseAck) + ". 结束时间" +
                DateUtils.dateToString(Calendar.getInstance().getTime(), DateUtils.DATETIME_FORMAT));
        return responseAck;
    }

    /*public ReturnTypeDO checkBalanceDetail(BalanceDetailDO queryModel, Pagenation<JdBalanceDetail> page){
        AssertUtil.notNull(page.getPageNo(), "分页查询参数pageNo不能为空");
        AssertUtil.notNull(page.getPageSize(), "分页查询参数pageSize不能为空");
        AssertUtil.notNull(page.getStart(), "分页查询参数start不能为空");
        Map<String, Object> map = BeanToMapUtil.convertBeanToMap(page);
        map.putAll(BeanToMapUtil.convertBeanToMap(queryModel));
        ReturnTypeDO<Pagenation<JdBalanceDetail>> returnTypeDO = new ReturnTypeDO<Pagenation<JdBalanceDetail>>();
        returnTypeDO.setSuccess(false);
        String response = null;
        try{
            String url = externalSupplierConfig.getScmExternalUrl()+externalSupplierConfig.getCheckOrderDetailUrl();
            response = HttpClientUtil.httpGetRequest(url, map);
            if(StringUtils.isNotBlank(response)){
                JSONObject jbo = JSONObject.parseObject(response);
                AppResult appResult = jbo.toJavaObject(AppResult.class);
                if(StringUtils.equals(appResult.getAppcode(), ZeroToNineEnum.ONE.getCode())){
                    page = jbo.getJSONObject("result").toJavaObject(Pagenation.class);
                    List<JdBalanceDetail> balanceDetails = new ArrayList<JdBalanceDetail>();
                    for(Object obj: page.getResult()){
                        JSONObject bo = (JSONObject)obj;
                        JdBalanceDetail detail = (JdBalanceDetail)bo.toJavaObject(JdBalanceDetail.class);
                        detail.setAmount(new Double(CommonUtil.getMoneyYuan(detail.getAmount())));
                        balanceDetails.add(detail);
                    }
                    page.setResult(balanceDetails);
                    returnTypeDO.setSuccess(true);
                    returnTypeDO.setResult(page);
                }
                returnTypeDO.setResultMessage(appResult.getDatabuffer());
            }else {
                returnTypeDO.setResultMessage("调用对账明细接口返回结果为空");
            }
        }catch (Exception e){
            String msg = String.format("调用对账明细服务异常,错误信息:%s", e.getMessage());
            log.error(msg, e);
            returnTypeDO.setResultMessage(msg);
        }
        return returnTypeDO;
    }*/

    @Override
    public ReturnTypeDO getAllTreadType(){
        ReturnTypeDO<List> returnTypeDO = new ReturnTypeDO<List>();
        returnTypeDO.setSuccess(false);
        String response = null;
        try{
            String url = externalSupplierConfig.getScmExternalUrl()+externalSupplierConfig.getTreadTypeUrl();
            response = HttpClientUtil.httpGetRequest(url);
            if(StringUtils.isNotBlank(response)){
                JSONObject jbo = JSONObject.parseObject(response);
                AppResult appResult = jbo.toJavaObject(AppResult.class);
                if(StringUtils.equals(appResult.getAppcode(), ZeroToNineEnum.ONE.getCode())){
                    JSONArray page = jbo.getJSONArray("result");
                    returnTypeDO.setSuccess(true);
                    returnTypeDO.setResult(page);
                }
                returnTypeDO.setResultMessage(appResult.getDatabuffer());
            }else {
                returnTypeDO.setResultMessage("调用查询业务类型接口返回结果为空");
            }
        }catch (IOException e){
            String msg = String.format("调用查询业务类型接口网络超时,错误信息:%s", e.getMessage());
            log.error(msg, e);
            returnTypeDO.setResultMessage(msg);
        }catch (Exception e){
            String msg = String.format("调用查询业务类型接口异常,错误信息:%s", e.getMessage());
            log.error(msg, e);
            returnTypeDO.setResultMessage(msg);
        }
        return returnTypeDO;
    }

    @Override
    public ReturnTypeDO getJingDongArea() {
        ReturnTypeDO returnTypeDO = new ReturnTypeDO();
        returnTypeDO.setSuccess(false);
        String url = externalSupplierConfig.getScmExternalUrl()+externalSupplierConfig.getJdAddressUrl();
        log.debug("开始调用京东区域查询服务" + url + ", 参数：无. 开始时间" +
                DateUtils.dateToString(Calendar.getInstance().getTime(), DateUtils.DATETIME_FORMAT));
        String response = null;
        try{
            response = HttpClientUtil.httpGetRequest(url);
            if(StringUtils.isNotBlank(response)){
                JSONObject jbo = JSONObject.parseObject(response);
                if(StringUtils.equals(jbo.getString("appcode"), ZeroToNineEnum.ONE.getCode())){
                    returnTypeDO.setSuccess(true);
                    returnTypeDO.setResult(jbo.getString("result"));
                }
                returnTypeDO.setResultMessage(jbo.getString("databuffer"));
                returnTypeDO.setResultCode(jbo.getString("resultCode"));
            }else {
                returnTypeDO.setResultMessage("调用京东区域查询服务返回结果为空");
            }
        }catch (Exception e){
            String msg = String.format("调用京东区域查询服务异常,错误信息:%s", e.getMessage());
            log.error(msg, e);
            returnTypeDO.setResultMessage(msg);
        }
        log.debug("结束调用京东区域查询服务" + url + ", 返回结果：" + JSONObject.toJSON(returnTypeDO) + ". 结束时间" +
                DateUtils.dateToString(Calendar.getInstance().getTime(), DateUtils.DATETIME_FORMAT));
        return returnTypeDO;
    }

    @Override
    public ReturnTypeDO queryBalanceInfo() {
        ReturnTypeDO<JSONObject> returnTypeDO = new ReturnTypeDO<JSONObject>();
        returnTypeDO.setSuccess(false);
        String response = null;
        try{
            String url = externalSupplierConfig.getScmExternalUrl()+externalSupplierConfig.getJdBalanceInfoUrl();
            response = HttpClientUtil.httpGetRequest(url);
            log.info("调用external余额信息接口："+response);
            if(StringUtils.isNotBlank(response)){
                JSONObject jbo = JSONObject.parseObject(response);
                AppResult appResult = jbo.toJavaObject(AppResult.class);
                if(StringUtils.equals(appResult.getAppcode(), ZeroToNineEnum.ONE.getCode())){
                    JSONObject page = jbo.getJSONObject("result");
                    returnTypeDO.setSuccess(true);
                    returnTypeDO.setResult(page);
                }
                returnTypeDO.setResultMessage(appResult.getDatabuffer());
            }else {
                returnTypeDO.setResultMessage("调用查询账户余额信息接口返回结果为空");
            }
        }catch (Exception e){
            String msg = String.format("调用查询账户余额信息接口异常,错误信息:%s", e.getMessage());
            log.error(msg, e);
            returnTypeDO.setResultMessage(msg);
        }
        return returnTypeDO;
    }

    @Override
    public ReturnTypeDO orderDetailByPage(OrderDetailForm queryModel, Pagenation<OrderDetail> page) {
        ReturnTypeDO<JSONObject> returnTypeDO = new ReturnTypeDO<JSONObject>();
        returnTypeDO.setSuccess(false);
        Map<String, Object> map = BeanToMapUtil.convertBeanToMap(page);
        map.putAll(BeanToMapUtil.convertBeanToMap(queryModel));
        String response = null;
        try{
            String url = externalSupplierConfig.getScmExternalUrl()+externalSupplierConfig.getJdOrderDetailPageUrl();
            response = HttpClientUtil.httpGetRequest(url,map);
            log.info("调用external订单明细分页查询结果："+response);
            if(StringUtils.isNotBlank(response)){
                JSONObject jbo = JSONObject.parseObject(response);
                AppResult appResult = jbo.toJavaObject(AppResult.class);
                if(StringUtils.equals(appResult.getAppcode(), ZeroToNineEnum.ONE.getCode())){
                    JSONObject json = jbo.getJSONObject("result");
                    returnTypeDO.setSuccess(true);
                    returnTypeDO.setResult(json);
                }
                returnTypeDO.setResultMessage(appResult.getDatabuffer());
            }else {
                returnTypeDO.setResultMessage("调用订单对比明细分页查询接口返回结果为空");
            }
        }catch (Exception e){
            String msg = String.format("调用订单对比明细分页查询接口异常,错误信息:%s", e.getMessage());
            log.error(msg, e);
            returnTypeDO.setResultMessage(msg);
        }
        return returnTypeDO;
    }

    @Override
    public ReturnTypeDO balanceDetailByPage(BalanceDetailDO queryModel, Pagenation<JdBalanceDetail> page) {
        ReturnTypeDO<JSONObject> returnTypeDO = new ReturnTypeDO<JSONObject>();
        returnTypeDO.setSuccess(false);
        Map<String, Object> map = BeanToMapUtil.convertBeanToMap(page);
        map.putAll(BeanToMapUtil.convertBeanToMap(queryModel));
        String response = null;
        try{
            String url = externalSupplierConfig.getScmExternalUrl()+externalSupplierConfig.getJdBalanceDetailPageUrl();
            response = HttpClientUtil.httpGetRequest(url,map);
            log.info("调用external余额明细分页查询结果："+response);
            if(StringUtils.isNotBlank(response)){
                JSONObject jbo = JSONObject.parseObject(response);
                AppResult appResult = jbo.toJavaObject(AppResult.class);
                if(StringUtils.equals(appResult.getAppcode(), ZeroToNineEnum.ONE.getCode())){
                    JSONObject json = jbo.getJSONObject("result");
                    returnTypeDO.setSuccess(true);
                    returnTypeDO.setResult(json);
                }
                returnTypeDO.setResultMessage(appResult.getDatabuffer());
            }else {
                returnTypeDO.setResultMessage("调用余额明细分页查询接口返回结果为空");
            }
        }catch (Exception e){
            String msg = String.format("调用余额明细分页查询接口异常,错误信息:%s", e.getMessage());
            log.error(msg, e);
            returnTypeDO.setResultMessage(msg);
        }
        return returnTypeDO;
    }

    @Override
    public List<BalanceDetailDTO> exportBalanceDetail(BalanceDetailDO queryModel) {
        Map<String, Object> map = BeanToMapUtil.convertBeanToMap(queryModel);
        String response = null;
        try{
            String url = externalSupplierConfig.getScmExternalUrl()+externalSupplierConfig.getJdExportBalanceUrl();
            response = HttpClientUtil.httpGetRequest(url,map);
            if(StringUtils.isNotBlank(response)){
                JSONObject jbo = JSONObject.parseObject(response);
                AppResult appResult = jbo.toJavaObject(AppResult.class);
                if(StringUtils.equals(appResult.getAppcode(), ZeroToNineEnum.ONE.getCode())){
                    JSONArray json = jbo.getJSONArray("result");
                    return json.toJavaList(BalanceDetailDTO.class);
                }
            }
            return null;
        }catch (Exception e){
            String msg = String.format("调用余额明细导出接口异常,错误信息:%s", e.getMessage());
            log.error(msg, e);
            return null;
        }
    }

    @Override
    public List<OrderDetailDTO> exportOrderDetail(OrderDetailForm queryModel) {
        ReturnTypeDO<List> returnTypeDO = new ReturnTypeDO<List>();
        Map<String, Object> map = BeanToMapUtil.convertBeanToMap(queryModel);
        returnTypeDO.setSuccess(false);
        String response = null;
        try{
            String url = externalSupplierConfig.getScmExternalUrl()+externalSupplierConfig.getJdExportOrderUrl();
            response = HttpClientUtil.httpGetRequest(url,map);
            if(StringUtils.isNotBlank(response)){
                JSONObject jbo = JSONObject.parseObject(response);
                AppResult appResult = jbo.toJavaObject(AppResult.class);
                if(StringUtils.equals(appResult.getAppcode(), ZeroToNineEnum.ONE.getCode())){
                    JSONArray json = jbo.getJSONArray("result");
                    return json.toJavaList(OrderDetailDTO.class);
                }
            }
            return null;
        }catch (Exception e){
            String msg = String.format("调用订单明细导出接口异常,错误信息:%s", e.getMessage());
            log.error(msg, e);
            return null;
        }
    }

    @Override
    public ReturnTypeDO operateRecord(OperateForm orderDetail) {
        ReturnTypeDO<Boolean> returnTypeDO = new ReturnTypeDO<Boolean>();
        Map<String, Object> map = BeanToMapUtil.convertBeanToMap(orderDetail);
        returnTypeDO.setSuccess(false);
        String response = null;
        try{
            String url = externalSupplierConfig.getScmExternalUrl()+externalSupplierConfig.getJdOrderOperateUrl();
            response = HttpClientUtil.httpPutRequest(url,map,5000);
            if(StringUtils.isNotBlank(response)){
                JSONObject jbo = JSONObject.parseObject(response);
                AppResult appResult = jbo.toJavaObject(AppResult.class);
                if(StringUtils.equals(appResult.getAppcode(), ZeroToNineEnum.ONE.getCode())){
                    Boolean json = jbo.getBoolean("result");
                    returnTypeDO.setSuccess(true);
                    returnTypeDO.setResult(json);
                }
                returnTypeDO.setResultMessage(appResult.getDatabuffer());
            }else {
                returnTypeDO.setResultMessage("调用订单明操作接口返回结果为空");
            }
        }catch (Exception e){
            String msg = String.format("调用订单明操作接口异常,错误信息:%s", e.getMessage());
            log.error(msg, e);
            returnTypeDO.setResultMessage(msg);
        }
        return returnTypeDO;
    }

    public ReturnTypeDO getOperateState(Long id){
        ReturnTypeDO<JSONObject> returnTypeDO = new ReturnTypeDO<JSONObject>();
        Map<String, Object> map = new HashedMap();
        map.put("id",id);
        returnTypeDO.setSuccess(false);
        String response = null;
        try{
            String url = externalSupplierConfig.getScmExternalUrl()+externalSupplierConfig.getJdOperateStateUrl()+"/"+id;
            response = HttpClientUtil.httpGetRequest(url);
            //response = HttpClientUtil.httpGetRequest(url,map);
            if(StringUtils.isNotBlank(response)){
                JSONObject jbo = JSONObject.parseObject(response);
                AppResult appResult = jbo.toJavaObject(AppResult.class);
                if(StringUtils.equals(appResult.getAppcode(), ZeroToNineEnum.ONE.getCode())){
                    JSONObject json = jbo.getJSONObject("result");
                    returnTypeDO.setSuccess(true);
                    returnTypeDO.setResult(json);
                }
                returnTypeDO.setResultMessage(appResult.getDatabuffer());
            }else {
                returnTypeDO.setResultMessage("调用订单明操作查询接口返回结果为空");
            }
        }catch (Exception e){
            String msg = String.format("调用订单明操作查询接口异常,错误信息:%s", e.getMessage());
            log.error(msg, e);
            returnTypeDO.setResultMessage(msg);
        }
        return returnTypeDO;
    }


    public ReturnTypeDO statisticsRecord(BalanceDetailDO queryModel){
        ReturnTypeDO<JSONObject> returnTypeDO = new ReturnTypeDO<JSONObject>();
        returnTypeDO.setSuccess(false);
        Map<String, Object> map = BeanToMapUtil.convertBeanToMap(queryModel);
        String response = null;
        try{
            String url = externalSupplierConfig.getScmExternalUrl()+externalSupplierConfig.getBalancestatisticsUrl();
            response = HttpClientUtil.httpGetRequest(url,map);
            if(StringUtils.isNotBlank(response)){
                JSONObject jbo = JSONObject.parseObject(response);
                AppResult appResult = jbo.toJavaObject(AppResult.class);
                if(StringUtils.equals(appResult.getAppcode(), ZeroToNineEnum.ONE.getCode())){
                    JSONObject json = jbo.getJSONObject("result");
                    returnTypeDO.setSuccess(true);
                    returnTypeDO.setResult(json);
                }
                returnTypeDO.setResultMessage(appResult.getDatabuffer());
            }else {
                returnTypeDO.setResultMessage("调用余额明细统计接口返回结果为空");
            }
        }catch (Exception e){
            String msg = String.format("调用余额明细统计接口异常,错误信息:%s", e.getMessage());
            log.error(msg, e);
            returnTypeDO.setResultMessage(msg);
        }
        return returnTypeDO;
    }
}
