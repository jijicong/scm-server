package org.trc.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.sun.deploy.net.URLEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.trc.form.JDModel.JdBaseDO;
import org.trc.service.IJDService;
import org.trc.util.DateUtils;
import org.trc.util.HttpRequestUtil;
import org.trc.util.MD5;

import java.util.Date;

/**
 * Created by hzwyz on 2017/5/18 0018.
 */
@Service("jDService")
public class JDServiceImpl implements IJDService{

    @Autowired
    private JdBaseDO jdBaseDO;

    @Override
    public String createToken(){
        try {
            String timestamp = DateUtils.formatDateTime(new Date());
            String sign = jdBaseDO.getClient_secret() + timestamp + jdBaseDO.getClient_id()
                    + jdBaseDO.getUsername() + jdBaseDO.getPassword() + jdBaseDO.getGrant_type() + jdBaseDO.getClient_secret();
            sign = MD5.encryption(sign).toUpperCase();
            String url = jdBaseDO.getJdurl()+"/oauth2/accessToken";
            String data =
                    "grant_type=access_token" +
                            "&client_id=" +jdBaseDO.getClient_id()+
                            "&username=" + URLEncoder.encode(jdBaseDO.getUsername(), "utf-8") +
                            "&password=" + jdBaseDO.getPassword() +
                            "&timestamp=" + timestamp +
                            "&sign="+sign;
            String rev = HttpRequestUtil.sendHttpsPost(url, data, "utf-8");
            /*JSONObject json=JSONObject.parseObject(rev);
            JSONObject list=json.getJSONObject("result");
            String accessToken= (String) list.get("access_token");
            String refreshToken= (String) list.get("refresh_token");*/
            return rev;
        }catch (Exception e){
            return "创建Token出错";
        }
    }



    /**
     * 使用Refresh Token刷新Access Token
     * @param refreshToken
     * @return
     * @throws Exception
     */
    @Override
    public String freshAccessTokenByRefreshToken(String refreshToken)throws Exception  {
        try{
            String url = jdBaseDO.getJdurl()+"/oauth2/refreshToken";
            String data ="refresh_token="+refreshToken +
                    "&client_id=" +jdBaseDO.getClient_id()+
                    "&client_secret=" + jdBaseDO.getClient_secret();
            String rev = HttpRequestUtil.sendHttpsPost(url, data, "utf-8");
            JSONObject json=JSONObject.parseObject(rev);
            Boolean result = (Boolean) json.get("success");
            if (result){
                return "刷新成功";
            }
            return "刷新失败";
        }catch (Exception e){
            return "刷新Access Token出错";
        }
    }
}
