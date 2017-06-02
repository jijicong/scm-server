package org.trc.biz.impl.jingdong;


import com.alibaba.fastjson.JSONArray;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.stereotype.Service;
import org.trc.biz.impl.jingdong.util.JingDongUtil;
import org.trc.biz.impl.jingdong.util.Model.AddressDO;
import org.trc.biz.jingdong.IJingDongBiz;
import org.trc.domain.config.Common;
import org.trc.domain.config.InputRecordDO;
import org.trc.form.JDModel.OrderDO;
import org.trc.form.JDModel.SellPriceDO;
import org.trc.form.JDModel.StockDO;
import org.trc.enums.ZeroToNineEnum;
import org.trc.jingdong.JingDongSkuList;
import org.trc.mapper.config.ICommonMapper;
import org.trc.mapper.config.ITableMappingMapper;
import org.trc.mapper.jingdong.IJingDongMapper;
import org.trc.mapper.jingdong.InputRecordMapper;
import org.trc.mapper.jingdong.OutputRecordMapper;
import org.trc.service.IJDService;
import org.trc.util.AssertUtil;
import org.trc.util.DateUtils;
import org.trc.util.RedisUtil;

import java.util.Date;
import java.util.List;
import java.util.Calendar;
import java.util.Map;


/**
 * Created by hzwyz on 2017/5/19 0019.
 */
@Service("iJingDongBiz")
public class JingDongBizImpl implements IJingDongBiz {

    @Autowired
    IJDService ijdService;

    @Autowired
    ICommonMapper commonMapper;

    @Autowired
    ITableMappingMapper iTableMappingMapper;

    @Autowired
    JingDongUtil jingDongUtil;

    @Autowired
    private IJingDongMapper jingDongMapper;//商品sku

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
                acc = commonMapper.selectOne(acc);
                if (null != acc) {
                    //验证accessToken是否失效，失效则刷新，返回accessToken
                    String time = acc.getDeadTime();
                    if (jingDongUtil.validatToken(time)) {
                        return acc.getValue();
                    }
                    acc.setCode("refreshToken");
                    acc = commonMapper.selectOne(acc);
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
            return "获取Token失败";
        }
    }

    @Override
    public String billOrder(OrderDO orderDO) throws Exception {
        AssertUtil.notBlank(orderDO.getThirdOrder(),"第三方的订单单号不能为空");
        AssertUtil.notBlank(orderDO.getSku(),"商品信息不能为空");
        AssertUtil.notBlank(orderDO.getName(),"收货人姓名不能为空");
        AssertUtil.notNull(orderDO.getProvince(),"一级地址不能为空");
        AssertUtil.notNull(orderDO.getCity(),"二级地址不能为空");
        AssertUtil.notNull(orderDO.getCounty(),"三级地址不能为空");
        AssertUtil.notNull(orderDO.getTown(),"四级地址不能为空");
        AssertUtil.notBlank(orderDO.getAddress(),"详细地址不能为空");
        AssertUtil.notBlank(orderDO.getMobile(),"手机号不能为空");
        AssertUtil.notBlank(orderDO.getEmail(),"邮箱不能为空");
        AssertUtil.notNull(orderDO.getInvoiceState(),"开票方式不能为空");
        AssertUtil.notNull(orderDO.getInvoiceType(),"开票类型不能为空");
        AssertUtil.notNull(orderDO.getSelectedInvoiceTitle(),"发票类型不能为空");
        AssertUtil.notBlank(orderDO.getCompanyName(),"发票抬头不能为空");
        AssertUtil.notNull(orderDO.getInvoiceContent(),"开票内容不能为空");
        AssertUtil.notNull(orderDO.getPaymentType(),"支付方式不能为空");
        AssertUtil.notNull(orderDO.getIsUseBalance(),"是否使用余额不能为空");
        AssertUtil.notNull(orderDO.getSubmitState(),"是否使用预占库存不能为空");
        AssertUtil.notNull(orderDO.getSubmitState(),"是否使用预占库存不能为空");
        if ("2".equals(String.valueOf(orderDO.getInvoiceType())) && "1".equals(String.valueOf(orderDO.getInvoiceState()))){
            AssertUtil.notBlank(orderDO.getInvoiceName(),"增值票收票人姓名不能为空");
            AssertUtil.notBlank(orderDO.getInvoicePhone(),"增值票收票人电话不能为空");
            AssertUtil.notNull(orderDO.getInvoiceProvice(),"增值票收票人所在省不能为空");
            AssertUtil.notNull(orderDO.getInvoiceCity(),"增值票收票人所在市不能为空");
            AssertUtil.notNull(orderDO.getInvoiceCounty(),"增值票收票人所在区/县不能为空");
            AssertUtil.notBlank(orderDO.getInvoiceAddress(),"增值票收票人所在地址不能为空");
            AssertUtil.notBlank(orderDO.getOrderPriceSnap(),"客户端订单价格快照不能为空");
        }
        String token = getAccessToken();
        AssertUtil.notBlank(token,"token不能为空");
        String orderResult = ijdService.submitOrder(token,orderDO);
        return orderResult;
    }

    @Override
    public String confirmOrder(String jdOrderId) throws Exception {
        String token =getAccessToken();
        AssertUtil.notBlank(token,"token不能为空");
        AssertUtil.notBlank(jdOrderId,"jdOrderId不能为空");
        String data = ijdService.confirmOrder(token,jdOrderId);
        return data;
    }

    @Override
    public String cancel(String jdOrderId) throws Exception {
        String token =getAccessToken();
        AssertUtil.notBlank(token,"token不能为空");
        AssertUtil.notBlank(jdOrderId,"jdOrderId不能为空");
        String data = ijdService.cancel(token,jdOrderId);
        return data;
    }

    @Override
    public String doPay(String jdOrderId) throws Exception {
        String token =getAccessToken();
        AssertUtil.notBlank(token,"token不能为空");
        AssertUtil.notBlank(jdOrderId,"jdOrderId不能为空");
        String data = ijdService.doPay(token,jdOrderId);
        return data;
    }

    @Override
    public String selectJdOrderIdByThirdOrder(String jdOrderId) throws Exception {
        String token =getAccessToken();
        AssertUtil.notBlank(token,"token不能为空");
        AssertUtil.notBlank(jdOrderId,"jdOrderId不能为空");
        String data = ijdService.selectJdOrderIdByThirdOrder(token,jdOrderId);
        return data;
    }

    @Override
    public String selectJdOrder(String jdOrderId) throws Exception {
        String token =getAccessToken();
        AssertUtil.notBlank(token,"token不能为空");
        AssertUtil.notBlank(jdOrderId,"jdOrderId不能为空");
        String data = ijdService.selectJdOrder(token,jdOrderId);
        return data;
    }

    @Override
    public String orderTrack(String jdOrderId) throws Exception {
        String token =getAccessToken();
        AssertUtil.notBlank(token,"token不能为空");
        AssertUtil.notBlank(jdOrderId,"jdOrderId不能为空");
        String data = ijdService.orderTrack(token,jdOrderId);
        return data;
    }

    @Override
    public List<SellPriceDO> getSellPrice(String sku) throws Exception {
        AssertUtil.notBlank(sku,"sku不能为空");
        String token = getAccessToken();
        AssertUtil.notBlank(token,"token不能为空");
        List<SellPriceDO> price = ijdService.getSellPrice(token, sku);
        return price;
    }

    @Override
    public List<StockDO> getStockById(String sku, AddressDO area) throws Exception {
        AssertUtil.notBlank(sku,"sku不能为空");
        AssertUtil.notBlank(area.getProvince(),"province不能为空");
        AssertUtil.notBlank(area.getCity(),"city不能为空");
        AssertUtil.notBlank(area.getCounty(),"county不能为空");
        String token = getAccessToken();
        AssertUtil.notBlank(token,"token不能为空");
        String address = getAddress(area.getProvince(), area.getCity(), area.getCounty());
        List<StockDO> stock = ijdService.getStockById(token, sku, address);
        return stock;
    }

    @Override
    public List<StockDO> getNewStockById(JSONArray skuNums, AddressDO area) throws Exception {
        AssertUtil.notNull(skuNums,"商品和数量不能为空");
        AssertUtil.notBlank(area.getProvince(),"province不能为空");
        AssertUtil.notBlank(area.getCity(),"city不能为空");
        AssertUtil.notBlank(area.getCounty(),"county不能为空");
        String token = getAccessToken();
        AssertUtil.notBlank(token,"token不能为空");
        String address = getAddress(area.getProvince(), area.getCity(), area.getCounty());
        List<StockDO> stock = ijdService.getNewStockById(token, skuNums.toJSONString(), address);
        return stock;
    }

    @Override
    public String getAddress(String pro, String ci, String cou) throws Exception {
        try {
            AssertUtil.notBlank(pro,"province不能为空");
            AssertUtil.notBlank(ci,"city不能为空");
            AssertUtil.notBlank(cou,"county不能为空");
            String province = iTableMappingMapper.selectByCode(pro);
            String city = iTableMappingMapper.selectByCode(ci);
            String county = iTableMappingMapper.selectByCode(cou);
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
            Common tmp = commonMapper.selectByCode(acc.getCode());
            Common token = map.get("time");
            acc.setDeadTime(token.getDeadTime());
            if (null == tmp) {
                commonMapper.insert(acc);
                return true;
            }
            acc.setId(tmp.getId());
            commonMapper.updateByPrimaryKey(acc);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


}
