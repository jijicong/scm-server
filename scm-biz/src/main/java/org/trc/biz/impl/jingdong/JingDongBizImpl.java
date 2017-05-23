package org.trc.biz.impl.jingdong;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.stereotype.Service;
import org.trc.biz.impl.jingdong.util.JingDongUtil;
import org.trc.biz.jingdong.IJingDongBiz;
import org.trc.domain.config.Common;
import org.trc.mapper.config.ICommonMapper;
import org.trc.service.IJDService;
import org.trc.util.RedisUtil;

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
                    if(JingDongUtil.validatToken(time)){
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

    private String createToken() throws Exception {
        String token;
        Common acc;
        token = ijdService.createToken();
        Map<String,Common> map = JingDongUtil.buildCommon(token);
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
        Map<String,Common> map = JingDongUtil.buildCommon(token);
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
