package org.trc.service.impl;


import org.springframework.stereotype.Service;
import org.trc.service.ITrcService;
import org.trc.util.HttpClientUtil;

import java.util.HashMap;
import java.util.Map;


/**
 * 通知泰然城
 * Created by hzdzf on 2017/6/6.
 */
@Service("trcService")
public class TrcService implements ITrcService {


    @Override
    public String sendBrandNotice(String brandUrl, String params) throws Exception {
        return HttpClientUtil.httpPostJsonRequest(brandUrl, params, 10000);
    }

    @Override
    public String sendPropertyNotice(String propertyUrl, String params) throws Exception {
        return HttpClientUtil.httpPostJsonRequest(propertyUrl, params, 10000);
    }

    //发送商品改动
    @Override
    public String sendItemsNotice(String itemsUrl, String params) throws Exception {
        return HttpClientUtil.httpPostJsonRequest(itemsUrl, params, 10000);
    }

    @Override
    public String getJDLogistic(String getJDLogisticUrl) throws Exception {
        return HttpClientUtil.httpGetRequest(getJDLogisticUrl);
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
