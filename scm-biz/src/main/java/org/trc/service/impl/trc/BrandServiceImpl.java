package org.trc.service.impl.trc;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
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
@Service("brandService")
public class BrandServiceImpl implements BrandService {

    private static final Logger logger = LoggerFactory.getLogger(BrandServiceImpl.class);

    @Override
    public String sendBrandNotice(String action, long operateTime, Brand brand,String status) throws Exception {
        BrandToTrc brandToTrc = new BrandToTrc();
        brandToTrc.setAlise(brand.getAlise() == null ? "" : brand.getAlise());
        brandToTrc.setBrandCode(brand.getBrandCode() == null ? "" : brand.getBrandCode());
        brandToTrc.setIsValid(brand.getIsValid() == null ? "" : brand.getIsValid());
        brandToTrc.setLogo(brand.getLogo() == null ? "" : brand.getLogo());
        brandToTrc.setName(brand.getName() == null ? "" : brand.getName());
        brandToTrc.setWebUrl(brand.getWebUrl() == null ? "" : brand.getWebUrl());

        //传值处理
        String noticeNum = GuidUtil.getNextUid(action + "_");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(action).append("|").append(noticeNum).append("|").append(operateTime).append("|").
                append(brandToTrc.getAlise()).append("|").append(brandToTrc.getBrandCode()).append("|").append(brandToTrc.getIsValid()).append("|").
                append(brandToTrc.getLogo()).append("|").append(brandToTrc.getName()).append("|").append(brandToTrc.getWebUrl());
        //MD5加密
        String sign = MD5.encryption(stringBuilder.toString()).toLowerCase();
        JSONObject params = new JSONObject();
        params.put("action", action);
        params.put("changeTime", operateTime);
        params.put("noticeNum", noticeNum);
        params.put("sign", sign);
        params.put("brandToTrc", brandToTrc);
        return HttpClientUtil.httpPostJsonRequest("url", params.toJSONString(), 1000);

    }

    public static void main(String[] args) {
        String action ="delete";
        String noticeNum = GuidUtil.getNextUid(action + "_");
        BrandToTrc brandToTrc = new BrandToTrc();
        long operateTime=System.currentTimeMillis();
        //model中字段以字典序排序

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("action").append("|").append(operateTime).append("|").append("noticeNum").append("|").
                append(brandToTrc.getAlise()).append("|").append(brandToTrc.getBrandCode()).append("|").append(brandToTrc.getIsValid()).append("|").
                append(brandToTrc.getLogo()).append("|").append(brandToTrc.getName()).append("|").append(brandToTrc.getWebUrl());
        //MD5加密
        String sign = MD5.encryption(stringBuilder.toString()).toLowerCase();
        JSONObject params = new JSONObject();
        params.put("action", action);
        params.put("changeTime", operateTime);
        params.put("noticeNum", noticeNum);
        params.put("sign", sign);
        params.put("brandToTrc", brandToTrc);
        try {
            //TODO URL
             String result = HttpClientUtil.httpPostJsonRequest("localhost:8080/scm/tairan/"+action, params.toJSONString(), 1000);
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage(), e);
            JSONObject jsonObject = new JSONObject();
            //TODO status
            jsonObject.put("status", "");
            jsonObject.put("msg", "请求渠道方出错");

        }
    }
}
