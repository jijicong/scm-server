package org.trc.biz.impl.jingdong;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.trc.biz.impl.jingdong.util.JingDongUtil;
import org.trc.biz.jingdong.IJingDongBiz;
import org.trc.domain.config.Common;
import org.trc.mapper.config.ICommonMapper;
import org.trc.service.IJDService;

import java.util.Date;
import java.util.Map;


/**
 * Created by hzwyz on 2017/5/19 0019.
 */
@Service("iJingDongBiz")
public class JingDongBizImpl implements IJingDongBiz {

    @Autowired
    IJDService ijdService;

    @Autowired
    ICommonMapper commonDao;

    @Override
    public String getAccessToken() throws Exception {
        try{
           /*如果是第一次访问，创建新的Token*/
            String token =null;
            Common acc = new Common();
            acc.setCode("accessToken");
            Common accessToken = commonDao.selectOne(acc);
            if (null==accessToken){
                token = ijdService.createToken();
                Map<String,Common> map = JingDongUtil.buildCommon(token);
                acc = map.get("accessToken");
                token=acc.getCode();
                /*putToken(acc, map);*/
                return token;
            }
            Date oldDate = accessToken.getDeadTime();
            if (JingDongUtil.validatToken(oldDate)){
                return accessToken.getValue();
            }
            Common ref = new Common();
            ref.setCode("refreshToken");
            Common refreshToken = commonDao.selectOne(ref);
            token = ijdService.freshAccessTokenByRefreshToken(refreshToken.getValue());
            Map<String,Common> map = JingDongUtil.buildCommon(token);
            acc = map.get("accessToken");
            token=acc.getCode();
            /*putToken(acc, map);*/
            return token;
        /*将获取到的accessToken和refreshToken更新到redis和数据库中*/
        }catch (Exception e){
            return "获取Token失败";
        }
    }

    /*private Boolean putToken(Common acc, Map<String, Common> map) {
    *//*将获取到的accessToken和refreshToken保存到redis和数据库中*//*
        try{
            commonDao.insert(acc);
            cacheManager.set("accessToken",acc.getCode());
            cacheManager.set("accessToken",acc.getDeadTime());
            acc = map.get("refreshToken");
            commonDao.insert(acc);
            cacheManager.set("refreshToken",acc.getCode());
            cacheManager.set("refTokenDeadTime",acc.getDeadTime());
            return true;
        }catch (Exception e){
            return false;
        }
    }*/


}
