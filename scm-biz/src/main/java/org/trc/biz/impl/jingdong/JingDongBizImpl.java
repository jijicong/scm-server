package org.trc.biz.impl.jingdong;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.stereotype.Service;
import org.trc.enums.ZeroToNineEnum;
import org.trc.form.JDModel.ReturnTypeDO;
import org.trc.util.JingDongUtil;
import org.trc.form.jingdong.AddressDO;
import org.trc.form.jingdong.NewStockDO;
import org.trc.biz.jingdong.IJingDongBiz;
import org.trc.domain.config.Common;
import org.trc.domain.config.InputRecordDO;
import org.trc.domain.config.OutputRecordDO;
import org.trc.enums.JingDongEnum;
import org.trc.form.JDModel.OrderDO;
import org.trc.form.JDModel.SellPriceDO;
import org.trc.form.JDModel.StockDO;
import org.trc.service.IJDService;
import org.trc.service.jingdong.ICommonService;
import org.trc.service.jingdong.IJingDongInputRecordService;
import org.trc.service.jingdong.IJingDongOutputRecordService;
import org.trc.service.jingdong.ITableMappingService;
import org.trc.util.AssertUtil;
import org.trc.util.BeanToMapUtil;
import org.trc.util.RedisUtil;

import java.util.*;


/**
 * Created by hzwyz on 2017/5/19 0019.
 */
@Service("iJingDongBiz")
public class JingDongBizImpl implements IJingDongBiz {
    private Logger  log = LoggerFactory.getLogger(JingDongBizImpl.class);
    @Autowired
    IJDService ijdService;

    @Autowired
    ICommonService commonService;

    @Autowired
    ITableMappingService tableMappingService;

    @Autowired
    JingDongUtil jingDongUtil;

    @Autowired
    IJingDongInputRecordService inputRecordService;

    @Autowired
    IJingDongOutputRecordService outputRecordService;

    @Override
    public String getAccessToken() throws Exception {
        try {
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
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new Exception(JingDongEnum.ERROR_GET_TOKEN.getName());
        }
    }

    @Override
    public String billOrder(OrderDO orderDO) {
        try {
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
            String token = getAccessToken();
            AssertUtil.notBlank(token, "token不能为空");
            Map map = BeanToMapUtil.convertBeanToMap(orderDO);
            String inputParam = map.toString();
            log.info("输入参数："+inputParam);
            ReturnTypeDO orderResult = ijdService.submitOrder(token, orderDO);
            log.info("调用结果："+JSONObject.toJSONString(orderResult));
            Boolean state = saveRecord(inputParam, "统一下单接口billOrder", JSONObject.toJSONString(orderResult.getResult()), orderResult.getSuccess());
            if (!state){
                log.info("添加记录到数据库失败！");
            }
            return returnValue(orderResult.getResultCode(), JSONObject.toJSONString(orderResult.getResult()), orderResult.getResultMessage(), orderResult.getSuccess());
        } catch (Exception e) {
            log.error(e.getMessage());
            return returnValue(JingDongEnum.ERROR_ORDER_BILL.getCode(), null, e.getMessage(), false);
        }

    }

    @Override
    public String confirmOrder(String jdOrderId) {
        try {
            String token = getAccessToken();
            AssertUtil.notBlank(token, "token不能为空");
            AssertUtil.notBlank(jdOrderId, "jdOrderId不能为空");
            String inputParam = token + "&" + jdOrderId;
            log.info("输入参数："+inputParam);
            ReturnTypeDO orderResult = ijdService.confirmOrder(token, jdOrderId);
            log.info("调用结果："+JSONObject.toJSONString(orderResult));
            Boolean state = saveRecord(inputParam, "确认预占库存订单接口confirmOrder", Boolean.valueOf((boolean)orderResult.getResult()).toString(), orderResult.getSuccess());
            if (!state){
                log.info("添加记录到数据库失败！");
            }
            return returnValue(orderResult.getResultCode(), Boolean.valueOf((boolean)orderResult.getResult()).toString(), orderResult.getResultMessage(), orderResult.getSuccess());
        } catch (Exception e) {
            log.error(e.getMessage());
            return returnValue(JingDongEnum.ERROR_ORDER_CONFIRM.getCode(), null, e.getMessage(), false);
        }

    }

    @Override
    public String cancel(String jdOrderId) {
        try {
            String token = getAccessToken();
            AssertUtil.notBlank(token, "token不能为空");
            AssertUtil.notBlank(jdOrderId, "jdOrderId不能为空");
            String inputParam = token + "&" + jdOrderId;
            log.info("输入参数："+inputParam);
            ReturnTypeDO orderResult = ijdService.cancel(token, jdOrderId);
            log.info("调用结果："+JSONObject.toJSONString(orderResult));
            Boolean state = saveRecord(inputParam, "取消未确认订单接口cancel", Boolean.valueOf((boolean)orderResult.getResult()).toString(), orderResult.getSuccess());
            if (!state){
                log.info("添加记录到数据库失败！");
            }
            return returnValue(orderResult.getResultCode(), Boolean.valueOf((boolean)orderResult.getResult()).toString(), orderResult.getResultMessage(), orderResult.getSuccess());
        } catch (Exception e) {
            log.error(e.getMessage());
            return returnValue(JingDongEnum.ERROR_ORDER_CANCEL.getCode(), null, e.getMessage(), false);
        }
    }

    @Override
    public String doPay(String jdOrderId) {
        try {
            String token = getAccessToken();
            AssertUtil.notBlank(token, "token不能为空");
            AssertUtil.notBlank(jdOrderId, "jdOrderId不能为空");
            String inputParam = token + "&" + jdOrderId;
            log.info("输入参数："+inputParam);
            ReturnTypeDO orderResult = ijdService.doPay(token, jdOrderId);
            log.info("调用结果："+JSONObject.toJSONString(orderResult));
            Boolean state = saveRecord(inputParam, "发起支付接口doPay", Boolean.valueOf((boolean)orderResult.getResult()).toString(), orderResult.getSuccess());
            if (!state){
                log.info("添加记录到数据库失败！");
            }
            return returnValue(orderResult.getResultCode(), orderResult.getResult(), orderResult.getResultMessage(), orderResult.getSuccess());
        } catch (Exception e) {
            log.error(e.getMessage());
            return returnValue(JingDongEnum.ERROR_DO_PAY.getCode(), null, e.getMessage(), false);
        }
    }

    @Override
    public String selectJdOrderIdByThirdOrder(String jdOrderId) {
        try {
            String token = getAccessToken();
            AssertUtil.notBlank(token, "token不能为空");
            AssertUtil.notBlank(jdOrderId, "jdOrderId不能为空");
            String inputParam = token + "&" + jdOrderId;
            log.info("输入参数："+inputParam);
            ReturnTypeDO orderResult = ijdService.selectJdOrderIdByThirdOrder(token, jdOrderId);
            log.info("调用结果："+JSONObject.toJSONString(orderResult));
            Boolean state = saveRecord(inputParam, "订单反查接口selectJdOrderIdByThirdOrder", (String)orderResult.getResult(), orderResult.getSuccess());
            if (!state){
                log.info("添加记录到数据库失败！");
            }
            return returnValue(orderResult.getResultCode(), (String)orderResult.getResult(), orderResult.getResultMessage(), orderResult.getSuccess());
        } catch (Exception e) {
            log.error(e.getMessage());
            return returnValue(JingDongEnum.ERROR_SELECT_JDORDERID_BY_THIRDORDER.getCode(), null, e.getMessage(), false);
        }
    }

    @Override
    public String selectJdOrder(String jdOrderId) {
        try {
            String token = getAccessToken();
            AssertUtil.notBlank(token, "token不能为空");
            AssertUtil.notBlank(jdOrderId, "jdOrderId不能为空");
            String inputParam = token + "&" + jdOrderId;
            log.info("输入参数："+inputParam);
            ReturnTypeDO orderResult = ijdService.selectJdOrder(token, jdOrderId);
            log.info("调用结果："+JSONObject.toJSONString(orderResult));
            Boolean state = saveRecord(inputParam, "查询京东订单信息接口selectJdOrder", JSONObject.toJSONString(orderResult.getResult()), orderResult.getSuccess());
            if (!state){
                log.info("添加记录到数据库失败！");
            }
            return returnValue(orderResult.getResultCode(), JSONObject.toJSONString(orderResult.getResult()), orderResult.getResultMessage(), orderResult.getSuccess());
        } catch (Exception e) {
            log.error(e.getMessage());
            return returnValue(JingDongEnum.ERROR_SELECT_JDORDER.getCode(), null, e.getMessage(), false);
        }
    }

    @Override
    public String orderTrack(String jdOrderId) {
        try {
            String token = getAccessToken();
            AssertUtil.notBlank(token, "token不能为空");
            AssertUtil.notBlank(jdOrderId, "jdOrderId不能为空");
            ReturnTypeDO orderResult = ijdService.orderTrack(token, jdOrderId);
            String inputParam = token + "&" + jdOrderId;
            log.info("输入参数："+inputParam);
            log.info("调用结果："+JSONObject.toJSONString(orderResult));
            Boolean state = saveRecord(inputParam, "orderTrack(String jdOrderId)", JSONObject.toJSONString(orderResult.getResult()), orderResult.getSuccess());
            if (!state){
                log.info("添加记录到数据库失败！");
            }
            return returnValue(orderResult.getResultCode(), JSONObject.toJSONString(orderResult.getResult()), orderResult.getResultMessage(), orderResult.getSuccess());
        } catch (Exception e) {
            log.error(e.getMessage());
            return returnValue(JingDongEnum.ERROR_ORDER_TRACK.getCode(), null, e.getMessage(), false);
        }
    }

    @Override
    public List<SellPriceDO> getSellPrice(String sku) throws Exception {
        try{
            AssertUtil.notBlank(sku, "sku不能为空");
            String token = getAccessToken();
            AssertUtil.notBlank(token, "token不能为空");
            String inputParam = token + "&" + sku;
            log.info("输入参数："+inputParam);
            ReturnTypeDO price = ijdService.getSellPrice(token, sku);
            log.info("调用结果："+JSONObject.toJSONString(price));
            Boolean state = saveRecord(inputParam, "查询商品价格getSellPrice", JSONArray.toJSONString(price.getResult()), price.getSuccess());
            if (!state){
                log.info("添加记录到数据库失败！");
            }
            if (!price.getSuccess()) {
                return null;
            }
            List<SellPriceDO> list = JSONArray.parseArray(JSONArray.toJSONString(price.getResult()),SellPriceDO.class);
            return list;
        }catch (Exception e){
            log.error(e.getMessage());
            throw new Exception(JingDongEnum.ERROR_GET_SELL_PRICE.getName());
        }

    }

    @Override
    public List<StockDO> getStockById(String sku, AddressDO area) throws Exception {
        AssertUtil.notBlank(sku, "sku不能为空");
        AssertUtil.notBlank(area.getProvince(), "province不能为空");
        AssertUtil.notBlank(area.getCity(), "city不能为空");
        AssertUtil.notBlank(area.getCounty(), "county不能为空");
        String token = getAccessToken();
        AssertUtil.notBlank(token, "token不能为空");
        String address = getAddress(area.getProvince(), area.getCity(), area.getCounty());
        String inputParam = token + "&" + sku + "&" + address;
        log.info("输入参数："+inputParam);
        ReturnTypeDO stock = ijdService.getStockById(token, sku, address);
        log.info("调用结果："+JSONObject.toJSONString(stock));
        Boolean state = saveRecord(inputParam, "获取库存接口getStockById", JSONArray.toJSONString(stock.getResult()), stock.getSuccess());
        if (!state){
            log.info("添加记录到数据库失败！");
        }
        if (!stock.getSuccess()) {
            return null;
        }
        List<StockDO> stockState = JSONArray.parseArray(JSONArray.toJSONString(stock.getResult()),StockDO.class);
        return stockState;
    }

    @Override
    public List<NewStockDO> getNewStockById(JSONArray skuNums, AddressDO area) throws Exception {
        AssertUtil.notNull(skuNums, "商品和数量不能为空");
        AssertUtil.notBlank(area.getProvince(), "province不能为空");
        AssertUtil.notBlank(area.getCity(), "city不能为空");
        AssertUtil.notBlank(area.getCounty(), "county不能为空");
        String token = getAccessToken();
        AssertUtil.notBlank(token, "token不能为空");
        String address = getAddress(area.getProvince(), area.getCity(), area.getCounty());
        String inputParam = token + "&" + skuNums + "&" + address;
        log.info("输入参数："+inputParam);
        ReturnTypeDO stock = ijdService.getNewStockById(token, skuNums.toJSONString(), address);
        log.info("调用结果："+JSONObject.toJSONString(stock));
        Boolean state = saveRecord(inputParam, "获取库存接口getNewStockById", JSONArray.toJSONString(stock.getResult()), stock.getSuccess());
        if (!state){
            log.info("添加记录到数据库失败！");
        }
        if (!stock.getSuccess()) {
            return null;
        }
        List<NewStockDO> stockState = JSONArray.parseArray(JSONArray.toJSONString(stock.getResult()),NewStockDO.class);
        return stockState;
    }

    @Override
    public String getAddress(String pro, String ci, String cou) throws Exception {
        try {
            AssertUtil.notBlank(pro, "province不能为空");
            AssertUtil.notBlank(ci, "city不能为空");
            AssertUtil.notBlank(cou, "county不能为空");
            String province = tableMappingService.selectByCode(pro);
            String city = tableMappingService.selectByCode(ci);
            String county = tableMappingService.selectByCode(cou);
            return province + "_" + city + "_" + county;
        } catch (Exception e) {
            throw new Exception(JingDongEnum.ERROR_GET_ADDRESS.getName());
        }
    }

    @Override
    public void getSkuList() throws Exception {

    }

    private String createToken() throws Exception {
            ReturnTypeDO rev = ijdService.createToken();
            JSONObject json = (JSONObject)rev.getResult();
            if (null == json){
                throw new Exception(JingDongEnum.ERROR_GET_TOKEN.getName());
            }
            Map<String, Common> map = jingDongUtil.buildCommon(json);
            if (null == map){
                return JingDongEnum.ERROR_GET_ADDRESS.getName();
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
            if ("accessToken".equals(acc.getCode())){
                RedisUtil.setObject(acc.getCode(), acc.getValue(), 86300);
            }else {
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


    private String returnValue(String code, Object data, String message, Boolean success) {
        JSONObject obj = new JSONObject();
        obj.put("code", code);
        obj.put("data", data);
        obj.put("message", message);
        obj.put("success", success);
        return obj.toJSONString();
    }

    private Boolean saveRecord(String inputParam, String type, String outputParam, Boolean state) {
        try {
            InputRecordDO inputRecordDO = new InputRecordDO();
            OutputRecordDO outputRecordDO = new OutputRecordDO();
            inputRecordDO.setInputParam("输入参数：" + inputParam);
            inputRecordDO.setType("调用方法:JingDongBizImpl类中" + "["+type+"]");
            inputRecordDO.setState(String.valueOf(state));
            outputRecordDO.setOutputParam("返回值：" + outputParam);
            outputRecordDO.setType("调用方法:" + type);
            outputRecordDO.setState(String.valueOf(state));
            if (inputRecordService.insert(inputRecordDO)>0 && outputRecordService.insert(outputRecordDO)>0){
                return true;
            }
            return false;
        }catch (Exception e){
            return false;
        }
    }


}
