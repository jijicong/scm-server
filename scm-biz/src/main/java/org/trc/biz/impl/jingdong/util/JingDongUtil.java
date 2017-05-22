package org.trc.biz.impl.jingdong.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.trc.biz.impl.jingdong.util.Model.JingDongDO;
import org.trc.domain.config.Common;
import org.trc.util.DateUtils;

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
    public static Boolean validatToken(Date oldDate){
        Calendar nowTime=Calendar.getInstance(),oldTime= Calendar.getInstance();
        nowTime.setTime(new Date());//设置为当前系统时间
        oldTime.setTime(oldDate);
        long timeNow=nowTime.getTimeInMillis();
        long timeOld=oldTime.getTimeInMillis();
        long time=(timeNow-timeOld);
        if (time> 86400){
            return false;
        }
        return true;
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
        common.setCode(accessToken);
        common.setDeadTime(new Date(time));
        array.put("accessToken",common);
        common =new Common();
        common.setCode(refreshToken);
        common.setDeadTime(new Date(time));
        array.put("refreshToken",common);
        return array;
    }
}
