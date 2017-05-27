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
import org.trc.form.JDModel.SellPriceDO;
import org.trc.form.JDModel.StockDO;
import org.trc.mapper.config.ICommonMapper;
import org.trc.mapper.config.ITableMappingMapper;
import org.trc.service.IJDService;
import org.trc.util.RedisUtil;

import java.util.List;
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

    @Override
    public String getAccessToken() throws Exception {
        try{
            String token = null;
            Common acc = new Common();
            try{
                //查询redis中是否有accessToken
                token = (String) RedisUtil.getObject("accessToken");
            }catch (RedisConnectionFailureException e){
                //当redis无法连接从数据库中去accessToken
                acc.setCode("accessToken");
                acc = commonMapper.selectOne(acc);
                if (null != acc){
                    //验证accessToken是否失效，失效则刷新，返回accessToken
                    String time = acc.getDeadTime();
                    if(jingDongUtil.validatToken(time)){
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
            if (StringUtils.isNotBlank(token)){
                return token;
            }
            //如果accessToken失效，查询refreshToken,如果有效则刷新
            String refreshToken = (String) RedisUtil.getObject("refreshToken");
            if (StringUtils.isNotBlank(refreshToken)){
                return refreshToken(refreshToken);
            }
            //创建accessToken,并保存到数据库和缓存中
            token = createToken();
            return token;
        }catch (Exception e){
            return "获取Token失败";
        }
    }

    @Override
    public String billOrder() throws Exception {
        return null;
    }

    @Override
    public List<SellPriceDO> getSellPrice(String sku) throws Exception {
        String token = getAccessToken();
        List<SellPriceDO> price = ijdService.getSellPrice(token,sku);
        return price;
    }

    @Override
    public List<StockDO> getStockById(String sku, AddressDO area) throws Exception {
        String token = getAccessToken();
        String address = getAddress(area.getProvince(), area.getCity(), area.getCounty());
        List<StockDO> stock = ijdService.getStockById(token,sku,address);
        return stock;
    }

    @Override
    public List<StockDO> getNewStockById(JSONArray skuNums, AddressDO area) throws Exception {
        String token = getAccessToken();
        String address = getAddress(area.getProvince(), area.getCity(), area.getCounty());
        List<StockDO> stock = ijdService.getNewStockById(token,skuNums.toJSONString(),address);
        return stock;
    }

    @Override
    public String getAddress(String pro, String ci, String cou) throws Exception {
        try {
            String province = iTableMappingMapper.selectByCode(pro);
            String city = iTableMappingMapper.selectByCode(ci);
            String county = iTableMappingMapper.selectByCode(cou);
            return province+"_"+city+"_"+county;
        }catch (Exception e){
            throw new Exception("查询数据库无法找到该编码方式，请检查后重试！");
        }
    }

    private String createToken() throws Exception {
        String token;
        Common acc;
        token = ijdService.createToken();
        Map<String,Common> map = jingDongUtil.buildCommon(token);
        acc = map.get("accessToken");
        token=acc.getValue();
        putToken(acc, map);
        acc = map.get("refreshToken");
        putToken(acc, map);
        return token;
    }

    /**
     * 刷新Token
     * @param refreshToken
     * @return
     * @throws Exception
     */
    private String refreshToken(String refreshToken) throws Exception {
        String token;
        Common acc;
        token = ijdService.freshAccessTokenByRefreshToken(refreshToken);
        Map<String,Common> map = jingDongUtil.buildCommon(token);
        acc = map.get("accessToken");
        Common ref= map.get("refreshToken");
        token=acc.getValue();
        putToken(acc, map);
        putToken(ref, map);
        return token;
    }

    /**
     * 将Token保存到数据库和redis中
     * @param acc
     * @param map
     * @return
     */
    private Boolean putToken(Common acc, Map<String, Common> map) {
        try{
            Boolean result = RedisUtil.setObject(acc.getCode(),acc.getValue(), Integer.parseInt(acc.getDeadTime()));
            Common tmp = commonMapper.selectByCode(acc.getCode());
            Common token = map.get("time");
            acc.setDeadTime(token.getDeadTime());
            if (null == tmp){
                commonMapper.insert(acc);
                return true;
            }
            acc.setId(tmp.getId());
            commonMapper.updateByPrimaryKey(acc);
            return true;
        }catch (Exception e){
            return false;
        }
    }


}
