package org.trc.service.impl;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.trc.constant.RequestFlowConstant;
import org.trc.domain.category.*;
import org.trc.domain.config.RequestFlow;
import org.trc.enums.CategoryActionTypeEnum;
import org.trc.mapper.config.IRequestFlowMapper;
import org.trc.model.BrandToTrc;
import org.trc.model.CategoryToTrc;
import org.trc.model.PropertyToTrc;
import org.trc.model.ResultModel;
import org.trc.service.ITrcService;
import org.trc.util.GuidUtil;
import org.trc.util.HttpClientUtil;
import org.trc.util.MD5;

import java.util.Calendar;
import java.util.List;

/**
 * 通知泰然城
 * Created by hzdzf on 2017/6/6.
 */
@Service("taiRanService")
public class TrcService implements ITrcService {


    @Override
    public String sendBrandNotice(String brandUrl,String params) throws Exception {
        return HttpClientUtil.httpPostJsonRequest(brandUrl, params, 10000);
    }

    @Override
    public String sendPropertyNotice(String propertyUrl, String params) throws Exception {
       return HttpClientUtil.httpPostJsonRequest(propertyUrl, params, 10000);
    }

    //发送分类属性改动
    @Override
    public String sendCategoryPropertyList(String categoryPropertyUrl, String params) throws Exception {
        return HttpClientUtil.httpPostJsonRequest(categoryPropertyUrl, params, 10000);
    }

    //发送分类品牌改动
    @Override
    public String sendCategoryBrandList(String categoryBrandUrl, String params) throws Exception {
        return HttpClientUtil.httpPostJsonRequest(categoryBrandUrl, params, 10000);
    }

    //发送分类改动
    @Override
    public String sendCategoryToTrc(String categoryUrl, String params) throws Exception {
        return HttpClientUtil.httpPostJsonRequest(categoryUrl, params, 10000);
    }

}
