package org.trc.biz.impl.jingdong;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.stereotype.Service;
import org.trc.constant.RequestFlowConstant;
import org.trc.domain.config.RequestFlow;
import org.trc.enums.JingDongEnum;
import org.trc.enums.ZeroToNineEnum;
import org.trc.exception.JingDongException;
import org.trc.form.JDModel.*;
import org.trc.service.config.IRequestFlowService;
import org.trc.util.*;
import org.trc.form.jingdong.AddressDO;
import org.trc.form.jingdong.NewStockDO;
import org.trc.biz.jingdong.IJingDongBiz;
import org.trc.domain.config.Common;
import org.trc.service.IJDService;
import org.trc.service.jingdong.ICommonService;
import org.trc.service.jingdong.ITableMappingService;

import java.util.*;


/**
 * Created by hzwyz on 2017/5/19 0019.
 */
@Service("iJingDongBiz")
public class JingDongBizImpl implements IJingDongBiz {
    private Logger log = LoggerFactory.getLogger(JingDongBizImpl.class);
    @Autowired
    IJDService ijdService;

    @Autowired
    ICommonService commonService;

    @Autowired
    ITableMappingService tableMappingService;

    @Autowired
    JingDongUtil jingDongUtil;

    @Autowired
    IRequestFlowService requestFlowService;

    @Override
    public String getAccessToken() throws Exception {
        String token = null;
        Common acc = new Common();
        try {
            //查询redis中是否有accessToken
            token = (String) RedisUtil.getObject("accessToken");
        } catch (RedisConnectionFailureException e) {
            //当redis无法连接从数据库中去accessToken
            acc.setCode("accessToken");
            acc = commonService.selectOne(acc);
            if (null != acc) {
                //验证accessToken是否失效，失效则刷新，返回accessToken
                String time = acc.getDeadTime();
                if (jingDongUtil.validatToken(time)) {
                    return acc.getValue();
                }
                acc.setCode("refreshToken");
                acc = commonService.selectOne(acc);
                return refreshToken(acc.getValue());
            }
            token = createToken();
            return token;
        }
        //redis中查询到accessToken则返回
        if (StringUtils.isNotBlank(token)) {
            return token;
        }
        //如果accessToken失效，查询refreshToken,如果有效则刷新
        String refreshToken = (String) RedisUtil.getObject("refreshToken");
        if (StringUtils.isNotBlank(refreshToken)) {
            return refreshToken(refreshToken);
        }
        //创建accessToken,并保存到数据库和缓存中
        token = createToken();
        return token;
    }

    @Override
    public String billOrder(OrderDO orderDO) throws Exception {
        AssertUtil.notBlank(orderDO.getThirdOrder(), "第三方的订单单号不能为空");
        AssertUtil.notBlank(orderDO.getSku(), "商品信息不能为空");
        AssertUtil.notBlank(orderDO.getName(), "收货人姓名不能为空");
        AssertUtil.notNull(orderDO.getProvince(), "一级地址不能为空");
        AssertUtil.notNull(orderDO.getCity(), "二级地址不能为空");
        AssertUtil.notNull(orderDO.getCounty(), "三级地址不能为空");
        AssertUtil.notNull(orderDO.getTown(), "四级地址不能为空");
        AssertUtil.notBlank(orderDO.getAddress(), "详细地址不能为空");
        AssertUtil.notBlank(orderDO.getMobile(), "手机号不能为空");
        AssertUtil.notBlank(orderDO.getEmail(), "邮箱不能为空");
        AssertUtil.notNull(orderDO.getInvoiceState(), "开票方式不能为空");
        AssertUtil.notNull(orderDO.getInvoiceType(), "开票类型不能为空");
        AssertUtil.notNull(orderDO.getSelectedInvoiceTitle(), "发票类型不能为空");
        AssertUtil.notBlank(orderDO.getCompanyName(), "发票抬头不能为空");
        AssertUtil.notNull(orderDO.getInvoiceContent(), "开票内容不能为空");
        AssertUtil.notNull(orderDO.getPaymentType(), "支付方式不能为空");
        AssertUtil.notNull(orderDO.getIsUseBalance(), "是否使用余额不能为空");
        AssertUtil.notNull(orderDO.getSubmitState(), "是否使用预占库存不能为空");
        AssertUtil.notNull(orderDO.getSubmitState(), "是否使用预占库存不能为空");
        if (ZeroToNineEnum.TWO.getCode().equals(String.valueOf(orderDO.getInvoiceType())) && ZeroToNineEnum.ONE.getCode().equals(String.valueOf(orderDO.getInvoiceState()))) {
            AssertUtil.notBlank(orderDO.getInvoiceName(), "增值票收票人姓名不能为空");
            AssertUtil.notBlank(orderDO.getInvoicePhone(), "增值票收票人电话不能为空");
            AssertUtil.notNull(orderDO.getInvoiceProvice(), "增值票收票人所在省不能为空");
            AssertUtil.notNull(orderDO.getInvoiceCity(), "增值票收票人所在市不能为空");
            AssertUtil.notNull(orderDO.getInvoiceCounty(), "增值票收票人所在区/县不能为空");
            AssertUtil.notBlank(orderDO.getInvoiceAddress(), "增值票收票人所在地址不能为空");
            AssertUtil.notBlank(orderDO.getOrderPriceSnap(), "客户端订单价格快照不能为空");
        }
        ReturnTypeDO orderResult = null;
        String token = getAccessToken();
        AssertUtil.notBlank(token, "token不能为空");
        Map map = BeanToMapUtil.convertBeanToMap(orderDO);
        String inputParam = map.toString();
        log.info("输入参数：" + inputParam);
        try {
            orderResult = ijdService.submitOrder(token, orderDO);
            if (!orderResult.getSuccess()) {
                throw new Exception(orderResult.getResultMessage());
            }
        } catch (Exception e) {
            log.info("调用结果：" + JSONObject.toJSONString(orderResult));
            saveRecord(inputParam, "统一下单接口billOrder", JSONObject.toJSONString(orderResult), orderResult.getSuccess());
            throw new Exception(e.getMessage());
        }
        log.info("调用结果：" + JSONObject.toJSONString(orderResult));
        Boolean state = saveRecord(inputParam, "统一下单接口billOrder", JSONObject.toJSONString(orderResult), orderResult.getSuccess());
        if (!state) {
            log.info("添加记录到数据库失败！");
        }
        return JSONObject.toJSONString(orderResult.getResult());
    }

    @Override
    public String confirmOrder(String jdOrderId) throws Exception {
        ReturnTypeDO orderResult = null;
        String token = getAccessToken();
        AssertUtil.notBlank(token, "token不能为空");
        AssertUtil.notBlank(jdOrderId, "jdOrderId不能为空");
        JSONObject inputParam = new JSONObject();
        inputParam.put("token", token);
        inputParam.put("jdOrderId", jdOrderId);
        log.info("输入参数：" + inputParam.toJSONString());
        try {
            orderResult = ijdService.confirmOrder(token, jdOrderId);
            if (!orderResult.getSuccess()) {
                throw new Exception(orderResult.getResultMessage());
            }
        } catch (Exception e) {
            log.info("调用结果：" + JSONObject.toJSONString(orderResult));
            saveRecord(inputParam.toJSONString(), "确认预占库存订单接口confirmOrder", JSONObject.toJSONString(orderResult.getResult()), orderResult.getSuccess());
            throw new Exception(e.getMessage());
        }
        log.info("调用结果：" + JSONObject.toJSONString(orderResult));
        Boolean state = saveRecord(inputParam.toJSONString(), "确认预占库存订单接口confirmOrder", JSONObject.toJSONString(orderResult.getResult()), orderResult.getSuccess());
        if (!state) {
            log.info("添加记录到数据库失败！");
        }
        return JSONObject.toJSONString(orderResult.getResult());
    }

    @Override
    public String cancel(String jdOrderId) throws Exception {
        ReturnTypeDO orderResult = null;
        String token = getAccessToken();
        AssertUtil.notBlank(token, "token不能为空");
        AssertUtil.notBlank(jdOrderId, "jdOrderId不能为空");
        JSONObject inputParam = new JSONObject();
        inputParam.put("token", token);
        inputParam.put("jdOrderId", jdOrderId);
        log.info("输入参数：" + inputParam.toJSONString());
        try {
            orderResult = ijdService.cancel(token, jdOrderId);
            if (!orderResult.getSuccess()) {
                throw new Exception(orderResult.getResultMessage());
            }

        } catch (Exception e) {
            log.info("调用结果：" + JSONObject.toJSONString(orderResult));
            saveRecord(inputParam.toJSONString(), "取消未确认订单接口cancel", JSONObject.toJSONString(orderResult.getResult()), orderResult.getSuccess());
            throw new Exception(e.getMessage());
        }
        log.info("调用结果：" + JSONObject.toJSONString(orderResult));
        Boolean state = saveRecord(inputParam.toJSONString(), "取消未确认订单接口cancel", JSONObject.toJSONString(orderResult.getResult()), orderResult.getSuccess());
        if (!state) {
            log.info("添加记录到数据库失败！");
        }
        return JSONObject.toJSONString(orderResult.getResult());
    }

    @Override
    public ReturnTypeDO doPay(String jdOrderId) throws Exception {
        ReturnTypeDO orderResult = null;
        String token = getAccessToken();
        AssertUtil.notBlank(token, "token不能为空");
        AssertUtil.notBlank(jdOrderId, "jdOrderId不能为空");
        JSONObject inputParam = new JSONObject();
        inputParam.put("token", token);
        inputParam.put("jdOrderId", jdOrderId);
        log.info("输入参数：" + inputParam.toJSONString());
        try {
            orderResult = ijdService.doPay(token, jdOrderId);
            if (!orderResult.getSuccess()) {
                throw new Exception(orderResult.getResultMessage());
            }
        } catch (Exception e) {
            log.info("调用结果：" + JSONObject.toJSONString(orderResult));
            saveRecord(inputParam.toJSONString(), "发起支付接口doPay", JSONObject.toJSONString(orderResult), orderResult.getSuccess());
            //throw new Exception(JingDongConstant.ERROR_DO_PAY);
            throw new JingDongException(JingDongEnum.ERROR_DO_PAY,e.getMessage());
        }
        orderResult = ijdService.doPay(token, jdOrderId);
        log.info("调用结果：" + JSONObject.toJSONString(orderResult));
        Boolean state = saveRecord(inputParam.toJSONString(), "发起支付接口doPay", JSONObject.toJSONString(orderResult), orderResult.getSuccess());
        if (!state) {
            log.info("添加记录到数据库失败！");
        }
        return orderResult;
    }

    @Override
    public String selectJdOrderIdByThirdOrder(String jdOrderId) throws Exception {
        ReturnTypeDO orderResult = null;
        String token = getAccessToken();
        AssertUtil.notBlank(token, "token不能为空");
        AssertUtil.notBlank(jdOrderId, "jdOrderId不能为空");
        JSONObject inputParam = new JSONObject();
        inputParam.put("token", token);
        inputParam.put("jdOrderId", jdOrderId);
        log.info("输入参数：" + inputParam.toJSONString());
        try {
            orderResult = ijdService.selectJdOrderIdByThirdOrder(token, jdOrderId);
            if (!orderResult.getSuccess()) {
                throw new Exception(orderResult.getResultMessage());
            }
        } catch (Exception e) {
            log.info("调用结果：" + JSONObject.toJSONString(orderResult));
            saveRecord(inputParam.toJSONString(), "订单反查接口selectJdOrderIdByThirdOrder", JSONObject.toJSONString(orderResult), orderResult.getSuccess());
            throw new Exception(e.getMessage());
        }
        log.info("调用结果：" + JSONObject.toJSONString(orderResult));
        Boolean state = saveRecord(inputParam.toJSONString(), "订单反查接口selectJdOrderIdByThirdOrder", JSONObject.toJSONString(orderResult), orderResult.getSuccess());
        if (!state) {
            log.info("添加记录到数据库失败！");
        }
        return JSONObject.toJSONString(orderResult.getResult());
    }

    @Override
    public String selectJdOrder(String jdOrderId) throws Exception {
        ReturnTypeDO orderResult = null;
        String token = getAccessToken();
        AssertUtil.notBlank(token, "token不能为空");
        AssertUtil.notBlank(jdOrderId, "jdOrderId不能为空");
        JSONObject inputParam = new JSONObject();
        inputParam.put("token", token);
        inputParam.put("jdOrderId", jdOrderId);
        log.info("输入参数：" + inputParam.toJSONString());
        try {
            orderResult = ijdService.selectJdOrder(token, jdOrderId);
            if (!orderResult.getSuccess()) {
                throw new Exception(orderResult.getResultMessage());
            }
        } catch (Exception e) {
            log.info("调用结果：" + JSONObject.toJSONString(orderResult));
            saveRecord(inputParam.toJSONString(), "查询京东订单信息接口selectJdOrder", JSONObject.toJSONString(orderResult), orderResult.getSuccess());
            throw new Exception(e.getMessage());
        }
        log.info("调用结果：" + JSONObject.toJSONString(orderResult));
        Boolean state = saveRecord(inputParam.toJSONString(), "查询京东订单信息接口selectJdOrder", JSONObject.toJSONString(orderResult), orderResult.getSuccess());
        if (!state) {
            log.info("添加记录到数据库失败！");
        }
        return JSONObject.toJSONString(orderResult.getResult());
    }

    @Override
    public String orderTrack(String jdOrderId) throws Exception {
        ReturnTypeDO orderResult = null;
        String token = getAccessToken();
        AssertUtil.notBlank(token, "token不能为空");
        AssertUtil.notBlank(jdOrderId, "jdOrderId不能为空");
        JSONObject inputParam = new JSONObject();
        inputParam.put("token", token);
        inputParam.put("jdOrderId", jdOrderId);
        log.info("输入参数：" + inputParam.toJSONString());
        try {
            orderResult = ijdService.orderTrack(token, jdOrderId);
            if (!orderResult.getSuccess()) {
                throw new Exception(orderResult.getResultMessage());
            }
        } catch (Exception e) {
            log.info("调用结果：" + JSONObject.toJSONString(orderResult));
            saveRecord(inputParam.toJSONString(), "orderTrack(String jdOrderId)", JSONObject.toJSONString(orderResult), orderResult.getSuccess());
            throw new Exception(e.getMessage());
        }
        log.info("调用结果：" + JSONObject.toJSONString(orderResult));
        Boolean state = saveRecord(inputParam.toJSONString(), "orderTrack(String jdOrderId)", JSONObject.toJSONString(orderResult), orderResult.getSuccess());
        if (!state) {
            log.info("添加记录到数据库失败！");
        }
        return JSONObject.toJSONString(orderResult.getResult());
    }

    @Override
    public List<SellPriceDO> getSellPrice(String sku) throws Exception {
        AssertUtil.notBlank(sku, "sku不能为空");
        ReturnTypeDO price = null;
        String token = getAccessToken();
        AssertUtil.notBlank(token, "token不能为空");
        JSONObject inputParam = new JSONObject();
        inputParam.put("token", token);
        inputParam.put("sku", sku);
        log.info("输入参数：" + inputParam.toJSONString());
        try {
            price = ijdService.getSellPrice(token, sku);
            if (!price.getSuccess()) {
                throw new Exception(price.getResultMessage());
            }
        } catch (Exception e) {
            log.info("调用结果：" + JSONObject.toJSONString(price));
            saveRecord(inputParam.toJSONString(), "查询商品价格getSellPrice", JSONArray.toJSONString(price), price.getSuccess());
            throw new Exception(e.getMessage());
        }
        log.info("调用结果：" + JSONObject.toJSONString(price));
        Boolean state = saveRecord(inputParam.toJSONString(), "查询商品价格getSellPrice", JSONArray.toJSONString(price), price.getSuccess());
        if (!state) {
            log.info("添加记录到数据库失败！");
        }
        if (!price.getSuccess()) {
            return null;
        }
        List<SellPriceDO> list = JSONArray.parseArray(JSONArray.toJSONString(price.getResult()), SellPriceDO.class);
        return list;
    }

    @Override
    public List<StockDO> getStockById(String sku, AddressDO area) throws Exception {
        AssertUtil.notBlank(sku, "sku不能为空");
        AssertUtil.notBlank(area.getProvince(), "province不能为空");
        AssertUtil.notBlank(area.getCity(), "city不能为空");
        AssertUtil.notBlank(area.getCounty(), "county不能为空");
        ReturnTypeDO stock = null;
        String token = getAccessToken();
        AssertUtil.notBlank(token, "token不能为空");
        String address = getAddress(area.getProvince(), area.getCity(), area.getCounty());
        JSONObject inputParam = new JSONObject();
        inputParam.put("token", token);
        inputParam.put("sku", sku);
        inputParam.put("address", address);
        log.info("输入参数：" + inputParam.toJSONString());
        try {
            stock = ijdService.getStockById(token, sku, address);
            if (!stock.getSuccess()) {
                throw new Exception(stock.getResultMessage());
            }
        } catch (Exception e) {
            log.info("调用结果：" + JSONObject.toJSONString(stock));
            saveRecord(inputParam.toJSONString(), "获取库存接口getStockById", JSONArray.toJSONString(stock), stock.getSuccess());
            throw new Exception(e.getMessage());
        }
        log.info("调用结果：" + JSONObject.toJSONString(stock));
        Boolean state = saveRecord(inputParam.toJSONString(), "获取库存接口getStockById", JSONArray.toJSONString(stock), stock.getSuccess());
        if (!state) {
            log.info("添加记录到数据库失败！");
        }
        if (!stock.getSuccess()) {
            return null;
        }
        List<StockDO> stockState = JSONArray.parseArray(JSONArray.toJSONString(stock.getResult()), StockDO.class);
        return stockState;
    }

    @Override
    public List<NewStockDO> getNewStockById(JSONArray skuNums, AddressDO area) throws Exception {
        AssertUtil.notNull(skuNums, "商品和数量不能为空");
        AssertUtil.notBlank(area.getProvince(), "province不能为空");
        AssertUtil.notBlank(area.getCity(), "city不能为空");
        AssertUtil.notBlank(area.getCounty(), "county不能为空");
        ReturnTypeDO stock = null;
        String token = getAccessToken();
        AssertUtil.notBlank(token, "token不能为空");
        String address = getAddress(area.getProvince(), area.getCity(), area.getCounty());
        JSONObject inputParam = new JSONObject();
        inputParam.put("token", token);
        inputParam.put("skuNums", skuNums);
        inputParam.put("address", address);
        log.info("输入参数：" + inputParam.toJSONString());
        try {
            stock = ijdService.getNewStockById(token, skuNums.toJSONString(), address);
            if (!stock.getSuccess()) {
                throw new Exception(stock.getResultMessage());
            }
        } catch (Exception e) {
            log.info("调用结果：" + JSONObject.toJSONString(stock));
            saveRecord(inputParam.toJSONString(), "获取库存接口getNewStockById", JSONArray.toJSONString(stock), stock.getSuccess());
            throw new JingDongException(JingDongEnum.ERROR_GET_NEW_STOCK_BY_ID,e.getMessage());
        }
        log.info("调用结果：" + JSONObject.toJSONString(stock));
        Boolean state = saveRecord(inputParam.toJSONString(), "获取库存接口getNewStockById", JSONArray.toJSONString(stock), stock.getSuccess());
        if (!state) {
            log.info("添加记录到数据库失败！");
        }
        if (!stock.getSuccess()) {
            return null;
        }
        List<NewStockDO> stockState = JSONArray.parseArray(JSONArray.toJSONString(stock.getResult()), NewStockDO.class);
        return stockState;
    }

    @Override
    public String getAddress(String pro, String ci, String cou) throws Exception {
        AssertUtil.notBlank(pro, "province不能为空");
        AssertUtil.notBlank(ci, "city不能为空");
        AssertUtil.notBlank(cou, "county不能为空");
        String province = tableMappingService.selectByCode(pro);
        String city = tableMappingService.selectByCode(ci);
        String county = tableMappingService.selectByCode(cou);
        return province + "_" + city + "_" + county;
    }

    @Override
    public void getSkuList() throws Exception {

    }

    private String createToken() throws Exception {
        ReturnTypeDO rev = ijdService.createToken();
        JSONObject json = (JSONObject) rev.getResult();
        if (null == json) {
            throw new Exception(JingDongEnum.ERROR_GET_TOKEN.getMessage());
        }
        Map<String, Common> map = jingDongUtil.buildCommon(json);
        if (null == map) {
            return JingDongEnum.ERROR_GET_ADDRESS.getMessage();
        }
        Common acc = map.get("accessToken");
        String token = acc.getValue();
        putToken(acc);
        acc = map.get("refreshToken");
        putToken(acc);
        return token;
    }

    /**
     * 刷新Token
     *
     * @param refreshToken
     * @return
     * @throws Exception
     */
    private String refreshToken(String refreshToken) throws Exception {
        Common acc;
        ReturnTypeDO rev = ijdService.freshAccessTokenByRefreshToken(refreshToken);
        Map<String, Common> map = jingDongUtil.buildCommon((JSONObject) rev.getResult());
        acc = map.get("accessToken");
        Common ref = map.get("refreshToken");
        String token = acc.getValue();
        putToken(acc);
        putToken(ref);
        return token;
    }

    /**
     * 将Token保存到数据库和redis中
     *
     * @param acc
     * @return
     */
    private Boolean putToken(Common acc) {
        try {
            if ("accessToken".equals(acc.getCode())) {
                RedisUtil.setObject(acc.getCode(), acc.getValue(), 86300);
            } else {
                RedisUtil.setObject(acc.getCode(), acc.getValue(), 21474835);
            }
            Common tmp = commonService.selectByCode(acc.getCode());
            if (null == tmp) {
                commonService.insert(acc);
                return true;
            }
            acc.setId(tmp.getId());
            commonService.updateByPrimaryKey(acc);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Boolean saveRecord(String inputParam, String remark, String outputParam, Boolean state) {
        try {
            RequestFlow requestFlow = new RequestFlow();
            requestFlow.setType(RequestFlowConstant.JINGDONG);
            requestFlow.setRequester(RequestFlowConstant.GYL);
            requestFlow.setResponder(RequestFlowConstant.JINGDONG);
            requestFlow.setRequestParam(inputParam);
            requestFlow.setResponseParam(outputParam);
            requestFlow.setRequestNum(GuidUtil.getNextUid(RequestFlowConstant.JINGDONG));
            requestFlow.setRequestTime(Calendar.getInstance().getTime());
            if (state) {
                requestFlow.setStatus(ZeroToNineEnum.ONE.getCode());
            } else {
                requestFlow.setStatus(ZeroToNineEnum.ZERO.getCode());
            }
            requestFlow.setRemark("调用方法:JingDongBizImpl类中" + "[" + remark + "]");
            if (requestFlowService.insert(requestFlow) > 0) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}
