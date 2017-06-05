package org.trc.util;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.trc.form.jingdong.AddressDO;
import org.trc.biz.jingdong.IJingDongBiz;
import org.trc.domain.config.Common;
import org.trc.service.IJDService;
import org.trc.util.DateUtils;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hzwyz on 2017/5/19 0019.
 */
@Component
public class JingDongUtil {
    @Autowired
    IJDService ijdService;

    @Autowired
    IJingDongBiz iJingDongBiz;
    /**
     * 验证AccessToken是否失效
     * @return
     */
    public String expireToken(long oldDate,String expire){
        String time =null;
        try {
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//小写的mm表示的是分钟
            long oldtime= oldDate;
            long exp = Long.parseLong(expire)*1000;
            long dead = oldtime+exp;
            time = sdf.format(new Date(oldtime));//设置为当前系统时间
            time = sdf.format(new Date(dead));//设置为当前系统时间
        } catch (Exception e) {
            e.printStackTrace();
        }
        return time;
    }

    /**
     * 将json字符串分装到Common对象中
     * @param jsonStr
     * @return
     */
    public Map buildCommon(String jsonStr){
        Map array = new HashMap();
        Common common = new Common();
        JSONObject json = JSONObject.parseObject(jsonStr);
        JSONObject list=json.getJSONObject("result");
        String accessToken= (String) list.get("access_token");
        String refreshToken= (String) list.get("refresh_token");
        long time= (long) list.get("time");
        int expires= 86300;
        int refreshExpires = 205488147;
        String tmp = expireToken(time, String.valueOf(expires));
        common.setCode("accessToken");
        common.setValue(accessToken);
        common.setType("京东");
        common.setDeadTime(String.valueOf(expires));
        common.setDescription("京东AccessToken");
        array.put("accessToken",common);
        common =new Common();
        common.setCode("refreshToken");
        common.setValue(refreshToken);
        common.setType("京东");
        common.setDeadTime(String.valueOf(refreshExpires));
        common.setDescription("京东RefreshToken");
        array.put("refreshToken",common);
        common =new Common();
        common.setDeadTime(tmp);
        array.put("time",common);
        return array;
    }

    /**
     * 验证AccessToken是否失效
     * @return
     */
    public Boolean validatToken(String oldDate){
        Calendar nowTime=Calendar.getInstance(),oldTime= Calendar.getInstance();
        nowTime.setTime(new Date());//设置为当前系统时间
        oldTime.setTime(DateUtils.parseDateTime(oldDate));
        long timeNow=nowTime.getTimeInMillis();
        long timeOld=oldTime.getTimeInMillis();
        long time=(timeNow-timeOld);
        return time < 0;
    }


    /**
     * 获取京东地址编码
     * @param jsonStr 类似 {"province"："省份","city":"城市","county":"县/区","town":"街道"}
     * @return
     */
    public String getAddress(String jsonStr) throws Exception {
        //先将地址jsonStr字符串解析出来，并封装到addressDO;
        AddressDO addressDO = new AddressDO();
        String token = iJingDongBiz.getAccessToken();
        JSONObject json = JSONObject.parseObject(jsonStr);
        String province= (String) json.get("province");
        String city= (String) json.get("city");
        String county= (String) json.get("county");
        String town= (String) json.get("town");
        addressDO.setProvince(province);
        addressDO.setCity(city);
        addressDO.setCounty(county);
        addressDO.setTown(town);
        //然后调用解析后的字符去取code
        province = ijdService.getProvince(token);
        province = getMessage(addressDO.getProvince(),province);
        city = ijdService.getCity(token,province);
        city = getMessage(addressDO.getCity(),city);
        county = ijdService.getCounty(token,city);
        county = getMessage(addressDO.getCounty(),county);
        //最后拼接字符串编码，返回
        return province+"_"+city+"_"+county;

    }

    /**
     *
     * @param address 查询条件
     * @param str 京东返回的jason地址
     * @return
     */
    private String getMessage(String address,String str){
        JSONObject json = JSONObject.parseObject(str);
        JSONObject list=json.getJSONObject("result");
        int ad= (int) list.get(address);
        return String.valueOf(ad);
    }

}
