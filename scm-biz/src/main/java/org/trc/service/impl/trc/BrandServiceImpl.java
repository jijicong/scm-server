package org.trc.service.impl.trc;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trc.domain.category.Brand;
import org.trc.service.trc.BrandService;
import org.trc.service.trc.model.BrandToTrc;
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
    public String sendBrandNotice(String action, long changeTime, Brand brand) {
        String noticeNum = GuidUtil.getNextUid(action + "_");
        BrandToTrc brandToTrc = new BrandToTrc();
        brandToTrc.setAlise(brand.getAlise());
        brandToTrc.setBrandCode(brand.getBrandCode());
        brandToTrc.setCreateOperator(brand.getCreateOperator());
        brandToTrc.setIsDeleted(brand.getIsDeleted());
        brandToTrc.setIsValid(brand.getIsValid());
        brandToTrc.setLastEditOperator(brand.getLastEditOperator());
        brandToTrc.setLogo(brand.getLogo());
        brandToTrc.setName(brand.getName());
        brandToTrc.setSort(String.valueOf(brand.getSort()));
        brandToTrc.setSource(brand.getSource());
        brandToTrc.setWebUrl(brand.getWebUrl());
        //model中字段以字典序排序
        Field[] field = brandToTrc.getClass().getDeclaredFields(); // 获取实体类的所有属性，返回Field数组
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(action).append("|").append(changeTime).append("|").append(noticeNum).append("|");
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
            stringBuilder.append(value).append("|");
        }
        //MD5加密
        String sign = MD5.encryption(stringBuilder.toString()).toLowerCase();
        JSONObject params = new JSONObject();
        params.put("action", action);
        params.put("changeTime", changeTime);
        params.put("noticeNum", noticeNum);
        params.put("sign", sign);
        params.put("brand", brandToTrc);
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
