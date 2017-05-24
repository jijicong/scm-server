package org.trc.service.impl.trc;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.trc.domain.category.Property;
import org.trc.domain.category.PropertyValue;
import org.trc.service.trc.PropertyService;
import org.trc.service.trc.model.PropertyToTrc;
import org.trc.util.GuidUtil;
import org.trc.util.HttpClientUtil;
import org.trc.util.MD5;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * 泰然城分类回调
 * Created by hzdzf on 2017/5/24.
 */
@Service("propertyService")
public class PropertyServiceImpl implements PropertyService {

    private static final Logger logger = LoggerFactory.getLogger(PropertyServiceImpl.class);

    @Override
    public String sendPropertyNotice(Property property, String action, long operateTime, List<PropertyValue> valueList) throws Exception {
        PropertyToTrc propertyToTrc = new PropertyToTrc();
        propertyToTrc.setSort(String.valueOf(property.getSort()==null?"":property.getSort()));
        propertyToTrc.setName(property.getName()==null?"":property.getName());
        propertyToTrc.setIsValid(property.getIsValid()==null?"":property.getIsValid());
        propertyToTrc.setDescription(property.getDescription()==null?"":property.getDescription());
        propertyToTrc.setTypeCode(property.getTypeCode()==null?"":property.getTypeCode());
        propertyToTrc.setValueType(property.getValueType()==null?"":property.getValueType());

        //传值处理
        String noticeNum = GuidUtil.getNextUid(action + "_");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(action).append("|").append(noticeNum).append("|").append(operateTime).append("|").
                append(propertyToTrc.getDescription()).append("|").append(propertyToTrc.getIsValid()).append("|").
                append(propertyToTrc.getName()).append("|").append(propertyToTrc.getSort()).append("|").append(propertyToTrc.getTypeCode()).
                append("|").append(propertyToTrc.getValueType());
        //MD5加密
        String sign = MD5.encryption(stringBuilder.toString()).toLowerCase();
        JSONObject params = new JSONObject();
        params.put("action", action);
        params.put("changeTime", operateTime);
        params.put("noticeNum", noticeNum);
        params.put("sign", sign);
        params.put("propertyToTrc", propertyToTrc);
        //TODO URL和返回处理
        HttpClientUtil.httpPostJsonRequest("url", params.toJSONString(), 1000);
        //若有属性值改动
        if (valueList!=null){
            String result = JSON.toJSONString(valueList);
            HttpClientUtil.httpPostJsonRequest("url", result, 1000);
        }
        return null;
    }
}
