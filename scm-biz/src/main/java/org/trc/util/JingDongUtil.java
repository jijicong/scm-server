package org.trc.util;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.trc.biz.jingdong.IJingDongBiz;
import org.trc.domain.config.Common;
import org.trc.enums.JingDongEnum;
import org.trc.form.JDModel.JingDongConstant;
import org.trc.service.IJDService;

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
     *
     * @return
     */
    public String expireToken(long oldDate, String expire) {
        String time = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//小写的mm表示的是分钟
            long oldtime = oldDate;
            long exp = Long.parseLong(expire) * 1000;
            long dead = oldtime + exp;
            time = sdf.format(new Date(dead));//设置为当前系统时间
        } catch (Exception e) {
            e.printStackTrace();
        }
        return time;
    }

    /**
     * 将json字符串分装到Common对象中
     *
     * @param list
     * @return
     */
    public Map buildCommon(JSONObject list) throws Exception {
        try {
            Map array = new HashMap();
            Common common = new Common();
            String accessToken = (String) list.get("access_token");
            String refreshToken = (String) list.get("refresh_token");
            long time = (long) list.get("time");
            int expires = 86300;
            int refreshExpires = 21474835;
            String tmp = expireToken(time, String.valueOf(expires));
            common.setCode("accessToken");
            common.setValue(accessToken);
            common.setType(JingDongConstant.JD_TYPE);
            common.setDeadTime(tmp);
            common.setDescription("京东AccessToken");
            array.put("accessToken", common);
            tmp = expireToken(time, String.valueOf(refreshExpires));
            common = new Common();
            common.setCode("refreshToken");
            common.setValue(refreshToken);
            common.setType(JingDongConstant.JD_TYPE);
            common.setDeadTime(tmp);
            common.setDescription("京东RefreshToken");
            array.put("refreshToken", common);
            return array;
        } catch (Exception e) {
            throw new Exception(JingDongEnum.ERROR_GET_TOKEN.getMessage());
        }

    }

    /**
     * 验证AccessToken是否失效
     *
     * @return
     */
    public Boolean validatToken(String oldDate) {
        Calendar nowTime = Calendar.getInstance(), oldTime = Calendar.getInstance();
        nowTime.setTime(new Date());//设置为当前系统时间
        oldTime.setTime(DateUtils.parseDateTime(oldDate));
        long timeNow = nowTime.getTimeInMillis();
        long timeOld = oldTime.getTimeInMillis();
        long time = (timeNow - timeOld);
        return time < 0;
    }

    /**
     * @param address 查询条件
     * @param str     京东返回的jason地址
     * @return
     */
    private String getMessage(String address, String str) {
        JSONObject json = JSONObject.parseObject(str);
        JSONObject list = json.getJSONObject("result");
        int ad = (int) list.get(address);
        return String.valueOf(ad);
    }

}
