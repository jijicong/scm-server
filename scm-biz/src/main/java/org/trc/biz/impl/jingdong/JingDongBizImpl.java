package org.trc.biz.impl.jingdong;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.stereotype.Service;
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
    private final static Logger log = LoggerFactory.getLogger(JingDongBizImpl.class);
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
            return "获取Token失败";
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
            if ("2".equals(String.valueOf(orderDO.getInvoiceType())) && "1".equals(String.valueOf(orderDO.getInvoiceState()))) {
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
            String orderResult = ijdService.submitOrder(token, orderDO);
            log.info("调用结果："+orderResult);
            JSONObject json = JSONObject.parseObject(orderResult);
            Boolean state = (Boolean) json.get("success");
            String message = (String) json.get("resultMessage");
            saveRecord(inputParam, "billOrder(OrderDO orderDO)", orderResult, state);
            JSONObject result = json.getJSONObject("result");
            if (!state) {
                return returnValue(JingDongEnum.ORDER_FALSE.getCode(), result, message, false);
            }
            return returnValue(JingDongEnum.ORDER_SUCCESS.getCode(), result, message, true);
        } catch (Exception e) {
            log.error(e.getMessage());
            return returnValue(JingDongEnum.ORDER_ERROR.getCode(), null, e.getMessage(), false);
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
            String orderResult = ijdService.confirmOrder(token, jdOrderId);
            JSONObject json = JSONObject.parseObject(ijdService.confirmOrder(token, jdOrderId));
            Boolean state = (Boolean) json.get("success");
            String message = (String) json.get("resultMessage");
            saveRecord(inputParam, "confirmOrder(String jdOrderId)", orderResult, state);
            boolean result = (boolean) json.get("result");
            if (!state) {
                return returnValue(JingDongEnum.ORDER_FALSE.getCode(), result, message, false);
            }
            return returnValue(JingDongEnum.ORDER_SUCCESS.getCode(), result, message, true);
        } catch (Exception e) {
            log.error(e.getMessage());
            return returnValue(JingDongEnum.ORDER_ERROR.getCode(), null, e.getMessage(), false);
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
            String orderResult = ijdService.cancel(token, jdOrderId);
            JSONObject json = JSONObject.parseObject(orderResult);
            Boolean state = (Boolean) json.get("success");
            String message = (String) json.get("resultMessage");
            saveRecord(inputParam, "cancel(String jdOrderId)", orderResult, state);
            boolean result = (boolean) json.get("result");
            if (!state) {
                return returnValue(JingDongEnum.ORDER_FALSE.getCode(), result, message, false);
            }
            return returnValue(JingDongEnum.ORDER_SUCCESS.getCode(), result, message, true);
        } catch (Exception e) {
            log.error(e.getMessage());
            return returnValue(JingDongEnum.ORDER_ERROR.getCode(), null, e.getMessage(), false);
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
            String orderResult = ijdService.doPay(token, jdOrderId);
            JSONObject json = JSONObject.parseObject(orderResult);
            Boolean state = (Boolean) json.get("success");
            String message = (String) json.get("resultMessage");
            saveRecord(inputParam, "doPay(String jdOrderId)", orderResult, state);
            if (!state) {
                return returnValue(JingDongEnum.ORDER_FALSE.getCode(), null, message, false);
            }
            return returnValue(JingDongEnum.ORDER_SUCCESS.getCode(), null, message, true);
        } catch (Exception e) {
            log.error(e.getMessage());
            return returnValue(JingDongEnum.ORDER_ERROR.getCode(), null, e.getMessage(), false);
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
            String orderResult = ijdService.selectJdOrderIdByThirdOrder(token, jdOrderId);
            log.info("调用结果："+orderResult);
            JSONObject json = JSONObject.parseObject(orderResult);
            Boolean state = (Boolean) json.get("success");
            saveRecord(inputParam, "selectJdOrderIdByThirdOrder(String jdOrderId)", orderResult, state);
            String result = (String) json.get("result");
            String message = (String) json.get("resultMessage");
            if (!state) {
                return returnValue(JingDongEnum.ORDER_FALSE.getCode(), result, message, false);
            }
            return returnValue(JingDongEnum.ORDER_SUCCESS.getCode(), result, message, true);
        } catch (Exception e) {
            log.error(e.getMessage());
            return returnValue(JingDongEnum.ORDER_ERROR.getCode(), null, e.getMessage(), false);
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
            String orderResult = ijdService.selectJdOrder(token, jdOrderId);
            log.info("调用结果："+orderResult);
            JSONObject json = JSONObject.parseObject(orderResult);
            Boolean state = (Boolean) json.get("success");
            String message = (String) json.get("resultMessage");
            saveRecord(inputParam, "selectJdOrder(String jdOrderId)", orderResult, state);
            JSONObject result = (JSONObject) json.get("result");
            if (!state) {
                return returnValue(JingDongEnum.ORDER_FALSE.getCode(), result, message, false);
            }
            return returnValue(JingDongEnum.ORDER_SUCCESS.getCode(), result, message, true);
        } catch (Exception e) {
            log.error(e.getMessage());
            return returnValue(JingDongEnum.ORDER_ERROR.getCode(), null, e.getMessage(), false);
        }
    }

    @Override
    public String orderTrack(String jdOrderId) {
        try {
            String token = getAccessToken();
            AssertUtil.notBlank(token, "token不能为空");
            AssertUtil.notBlank(jdOrderId, "jdOrderId不能为空");
            String orderResult = ijdService.orderTrack(token, jdOrderId);
            String inputParam = token + "&" + jdOrderId;
            log.info("输入参数："+inputParam);
            log.info("调用结果："+orderResult);
            JSONObject json = JSONObject.parseObject(orderResult);
            Boolean state = (Boolean) json.get("success");
            String message = (String) json.get("resultMessage");
            saveRecord(inputParam, "orderTrack(String jdOrderId)", orderResult, state);
            JSONObject result = (JSONObject) json.get("result");
            if (!state) {
                return returnValue(JingDongEnum.ORDER_FALSE.getCode(), result, message, false);
            }
            return returnValue(JingDongEnum.ORDER_SUCCESS.getCode(), result, message, true);
        } catch (Exception e) {
            log.error(e.getMessage());
            return returnValue(JingDongEnum.ORDER_ERROR.getCode(), null, e.getMessage(), false);
        }
    }

    @Override
    public List<SellPriceDO> getSellPrice(String sku) throws Exception {
        AssertUtil.notBlank(sku, "sku不能为空");
        String token = getAccessToken();
        AssertUtil.notBlank(token, "token不能为空");
        String inputParam = token + "&" + sku;
        log.info("输入参数："+inputParam);
        String price = ijdService.getSellPrice(token, sku);
        log.info("调用结果："+price);
        JSONObject json = JSONObject.parseObject(price);
        boolean state = (boolean) json.get("success");
        saveRecord(inputParam, "getSellPrice(String sku)", price, state);
        if (!state) {
            return null;
        }
        String result = json.getString("result");
        List<SellPriceDO> list = JSONArray.parseArray(result,SellPriceDO.class);
        return list;
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
        String stock = ijdService.getStockById(token, sku, address);
        log.info("调用结果："+stock);
        JSONObject json = JSONObject.parseObject(stock);
        Boolean state = (Boolean) json.get("success");
        saveRecord(inputParam, "getStockById(String sku, AddressDO area)", stock, state);
        if (!state) {
            return null;
        }
        String result = json.getString("result");
        List<StockDO> stockState = JSONArray.parseArray(result,StockDO.class);
        /*List<StockDO> stockState = getStockState(json);*/
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
        String stock = ijdService.getNewStockById(token, skuNums.toJSONString(), address);
        log.info("调用结果："+stock);
        JSONObject json = JSONObject.parseObject(stock);
        Boolean state = (Boolean) json.get("success");
        saveRecord(inputParam, "getNewStockById(JSONArray skuNums, AddressDO area)", stock, state);
        if (!state) {
            return null;
        }
        String result = json.getString("result");
        List<NewStockDO> stockState = JSONArray.parseArray(result,NewStockDO.class);
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
            throw new Exception("查询数据库无法找到该编码方式，请检查后重试！");
        }
    }

    @Override
    public void getSkuList() throws Exception {
    }

    private String createToken() throws Exception {
        String token;
        Common acc;
        token = ijdService.createToken();
        Map<String, Common> map = jingDongUtil.buildCommon(token);
        acc = map.get("accessToken");
        token = acc.getValue();
        putToken(acc, map);
        acc = map.get("refreshToken");
        putToken(acc, map);
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
        String token;
        Common acc;
        token = ijdService.freshAccessTokenByRefreshToken(refreshToken);
        Map<String, Common> map = jingDongUtil.buildCommon(token);
        acc = map.get("accessToken");
        Common ref = map.get("refreshToken");
        token = acc.getValue();
        putToken(acc, map);
        putToken(ref, map);
        return token;
    }

    /**
     * 将Token保存到数据库和redis中
     *
     * @param acc
     * @param map
     * @return
     */
    private Boolean putToken(Common acc, Map<String, Common> map) {
        try {
            Boolean result = RedisUtil.setObject(acc.getCode(), acc.getValue(), Integer.parseInt(acc.getDeadTime()));
            Common tmp = commonService.selectByCode(acc.getCode());
            Common token = map.get("time");
            acc.setDeadTime(token.getDeadTime());
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

    private void saveRecord(String inputParam, String type, String outputParam, Boolean state) {
        InputRecordDO inputRecordDO = new InputRecordDO();
        OutputRecordDO outputRecordDO = new OutputRecordDO();
        inputRecordDO.setInputParam("输入参数：" + inputParam);
        inputRecordDO.setType("调用方法:" + type);
        inputRecordDO.setState(String.valueOf(state));
        outputRecordDO.setOutputParam("返回值：" + outputParam);
        outputRecordDO.setType("调用方法:" + type);
        outputRecordDO.setState(String.valueOf(state));
        inputRecordService.insert(inputRecordDO);
        outputRecordService.insert(outputRecordDO);
    }


}
