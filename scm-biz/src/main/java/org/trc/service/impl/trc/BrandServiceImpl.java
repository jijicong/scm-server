package org.trc.service.impl.trc;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trc.domain.category.Brand;
import org.trc.service.impl.BaseService;
import org.trc.service.trc.BrandService;
import org.trc.util.GuidUtil;
import org.trc.util.HttpClientUtil;
import org.trc.util.MD5;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


/**
 * 泰然城渠道品牌交互
 * Created by hzdzf on 2017/5/22.
 */
public class BrandServiceImpl implements BrandService {

    private static final Logger logger = LoggerFactory.getLogger(BrandServiceImpl.class);

    @Override
    public String sendBrandNotice(String action, String timeStamp, Brand brand) {
        String noticeNum = GuidUtil.getNextUid(action + "_");
        Field[] field = brand.getClass().getDeclaredFields(); // 获取实体类的所有属性，返回Field数组
        String sign = action + noticeNum + timeStamp;
        for (int i = 0; i < field.length; i++) {
            String name = field[i].getName();
            name = name.substring(0, 1).toUpperCase() + name.substring(1);
            Method m = null;
            try {
                m = brand.getClass().getMethod("get" + name);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            String value = null;
            try {
                value = (String) m.invoke(brand);    //调用getter方法获取属性值
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            sign = sign + value;
        }
        //MD5加密
        sign = MD5.encryption(sign).toLowerCase();
        JSONObject params = new JSONObject();
        params.put("action", action);
        params.put("noticeNum", noticeNum);
        params.put("changeTime", timeStamp);
        params.put("sign", sign);
        params.put("brand", brand);
        try {
            //TODO URL
            return HttpClientUtil.httpPostJsonRequest("url", params.toJSONString(), 1000);
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage(), e);
            JSONObject jsonObject = new JSONObject();
            //TODO status
            jsonObject.put("status", "");
            jsonObject.put("msg", "请求渠道方出错");
            return jsonObject.toJSONString();
        }

    }
}
