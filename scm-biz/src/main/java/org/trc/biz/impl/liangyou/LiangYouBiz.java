package org.trc.biz.impl.liangyou;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.stereotype.Service;
import org.trc.biz.liangyou.ILiangYouBiz;
import org.trc.constant.LiangYouConstant;
import org.trc.domain.config.Common;
import org.trc.domain.config.SkuListForm;
import org.trc.form.liangyou.*;
import org.trc.service.ILiangYouService;
import org.trc.service.jingdong.ICommonService;
import org.trc.util.AssertUtil;
import org.trc.util.JingDongUtil;
import org.trc.util.RedisUtil;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by hzwyz on 2017/6/13 0013.
 */
@Service("liangYouBiz")
public class LiangYouBiz implements ILiangYouBiz {

    @Resource(name = "LiangYouService")
    private ILiangYouService liangYouService;

    @Autowired
    ICommonService commonService;

    @Autowired
    JingDongUtil jingDongUtil;

    @Override
    public String getAccessToken() throws Exception {

        String token = null;
        Common acc = new Common();
        try{
            //查询redis中是否有accessToken
            token = (String) RedisUtil.getObject("Ly_access_token");
            if (StringUtils.isBlank(token)){
                return createToken();
            }
            return token;
        }catch(RedisConnectionFailureException e){
            //当redis无法连接从数据库中去accessToken
            acc.setCode("accessToken");
            acc.setType(LiangYouConstant.LIANGYOU_TYPE);
            acc = commonService.selectOne(acc);
            if (null != acc) {
                //验证accessToken是否失效，失效创建新的token
                String time = acc.getDeadTime();
                if (jingDongUtil.validatToken(time)) {
                    return createToken();
                }
                return acc.getValue();
            }
            return createToken();
        }
    }

    @Override
    public SkuListForm getSkuList() throws Exception {

        return null;
    }

    @Override
    public List<CheckStockDO> checkStock(String sku) throws Exception {
        AssertUtil.notBlank(sku, "sku不能为空");
        String token = getAccessToken();
        AssertUtil.notBlank(token, "token不能为空");
        ResultType<JSONArray> result = liangYouService.checkStock(token,sku);
        JSONArray object = (JSONArray)result.getData();
        List<CheckStockDO> list = object.toJavaList(CheckStockDO.class);
        return list;
    }

    @Override
    public String addOutOrder(LiangYouOrderDO orderDO) throws Exception {
        AssertUtil.notBlank(orderDO.getConsignee(), "Consignee不能为空");
        AssertUtil.notBlank(orderDO.getOrderSn(), "orderSn不能为空");
        AssertUtil.notBlank(orderDO.getOutOrderSn(), "outOrderSn不能为空");
        AssertUtil.notBlank(orderDO.getRealName(), "realName不能为空");
        AssertUtil.notBlank(orderDO.getImId(), "imId不能为空");
        AssertUtil.notBlank(orderDO.getPhoneMob(), "phoneMob不能为空");
        AssertUtil.notBlank(orderDO.getAddress(), "address不能为空");
        AssertUtil.notBlank(orderDO.getProvince(),"province不能为空");
        AssertUtil.notBlank(orderDO.getCity(),"city不能为空");
        AssertUtil.notBlank(orderDO.getCounty(),"county不能为空");
        AssertUtil.notNull(orderDO.getShippingId(),"shippingId不能为空");
        List<OutOrderGoods> list=orderDO.getOutOrderGoods();
        for (OutOrderGoods goods:list){
            AssertUtil.notBlank(goods.getGoodsName(),"goodsName不能为空");
            AssertUtil.notBlank(goods.getOnlySku(),"onlySku不能为空");
            AssertUtil.notBlank(goods.getQuantity(),"quantity不能为空");
        }
        ResultType<JSONObject> result = liangYouService.addOutOrder(orderDO);
        return JSONObject.toJSONString(result.getData());
    }

    @Override
    public String addToutOrder(LiangYouTorderDO orderDO) throws Exception {

        AssertUtil.notBlank(orderDO.getConsignee(), "Consignee不能为空");
        AssertUtil.notBlank(orderDO.getOrderSn(), "orderSn不能为空");
        //AssertUtil.notBlank(orderDO.getRealName(), "realName不能为空");

        if (StringUtils.isEquals(orderDO.getPaymentId(),LiangYouConstant.WEIXIN)){
            AssertUtil.notBlank(orderDO.getAccountId(), "accountId不能为空");
        }
        AssertUtil.notBlank(orderDO.getImId(), "imId不能为空");
        AssertUtil.notBlank(orderDO.getPaymentId(), "paymentId不能为空");
        AssertUtil.notBlank(orderDO.getTradeNum(), "tradeNum不能为空");
        AssertUtil.notBlank(orderDO.getOutOrderSn(), "outOrderSn不能为空");
        AssertUtil.notBlank(orderDO.getOrderAmount(), "orderAmount不能为空");
        AssertUtil.notBlank(orderDO.getPhoneMob(), "phoneMob不能为空");
        AssertUtil.notBlank(orderDO.getAddress(), "address不能为空");
        AssertUtil.notBlank(orderDO.getProvince(),"province不能为空");
        AssertUtil.notBlank(orderDO.getCity(),"city不能为空");
        AssertUtil.notBlank(orderDO.getCounty(),"county不能为空");
        AssertUtil.notNull(orderDO.getShippingId(),"shippingId不能为空");
        AssertUtil.notNull(orderDO.getShippingFee(),"shippingFee不能为空");
        List<OutTorderGoods> list=orderDO.getOutTorderGoods();
        for (OutTorderGoods goods:list){
            AssertUtil.notBlank(goods.getGoodsName(),"goodsName不能为空");
            AssertUtil.notBlank(goods.getOnlySku(),"onlySku不能为空");
            AssertUtil.notBlank(goods.getQuantity(),"quantity不能为空");
            AssertUtil.notNull(goods.getPrice(),"price不能为空");
        }
        ResultType<JSONObject> result = liangYouService.addToutOrder(orderDO);
        return JSONObject.toJSONString(result.getData());
    }

    @Override
    public String getOrderStatus(String orderSn) throws Exception {
        AssertUtil.notBlank(orderSn, "orderSn不能为空");
        ResultType<JSONObject> result = liangYouService.getOrderStatus(orderSn);
        return JSONObject.toJSONString(result.getData());
    }

    @Override
    public GoodsInfoDO getGoodsInfo(String sku) throws Exception {
        AssertUtil.notBlank(sku, "sku不能为空");
        String token = getAccessToken();
        AssertUtil.notBlank(token, "token不能为空");
        ResultType<JSONObject> result = liangYouService.getGoodsInfo(token,sku);
        JSONObject object = (JSONObject)result.getData();
        GoodsInfoDO goodsInfo = object.toJavaObject(GoodsInfoDO.class);
        return goodsInfo;
    }

    private String createToken() throws Exception{
        ResultType<JSONObject> result = liangYouService.getToken();
        JSONObject object = (JSONObject)result.getData();
        if (StringUtils.isEquals(result.getMessage(),"ok")){
            String token = (String)object.get("access_token");
            Integer expires = (Integer)object.get("expires_in");
            //保存到数据库和redis中，并返回token
            RedisUtil.setObject("Ly_access_token", token, expires-60);
            Common tem = new Common();
            tem.setCode(LiangYouConstant.LIANGYOU_CODE);
            tem.setType(LiangYouConstant.LIANGYOU_TYPE);
            Common obj =commonService.selectOne(tem);
            Common common = new Common();
            common.setCode(LiangYouConstant.LIANGYOU_CODE);
            common.setValue(token);
            common.setType(LiangYouConstant.LIANGYOU_TYPE);
            String deadTime = jingDongUtil.expireToken(Calendar.getInstance().getTimeInMillis(),Integer.toString(expires-60));
            common.setDeadTime(deadTime);
            common.setDescription(LiangYouConstant.LIANGYOU_DESC);
            if (null!=obj){
                common.setId(obj.getId());
                common.setUpdateTime(new Date());
                commonService.updateByPrimaryKey(common);
                return token;
            }
            commonService.insert(common);
            return token;
        }
        return object.toJSONString();
    }


}
