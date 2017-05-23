package org.trc.biz.impl.jingdong.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.trc.biz.impl.jingdong.util.Model.JingDongDO;
import org.trc.domain.config.Common;
import org.trc.util.DateUtils;

import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hzwyz on 2017/5/19 0019.
 */
public class JingDongUtil {
    /**
     * 验证AccessToken是否失效
     * @return
     */
    public static String expireToken(long oldDate,String expire){
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
    public static Map buildCommon(String jsonStr){
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
    public static Boolean validatToken(String oldDate){
        Calendar nowTime=Calendar.getInstance(),oldTime= Calendar.getInstance();
        nowTime.setTime(new Date());//设置为当前系统时间
        oldTime.setTime(DateUtils.parseDateTime(oldDate));
        long timeNow=nowTime.getTimeInMillis();
        long timeOld=oldTime.getTimeInMillis();
        long time=(timeNow-timeOld);
        if (time>= 0){
            return false;
        }
        return true;
    }

}
