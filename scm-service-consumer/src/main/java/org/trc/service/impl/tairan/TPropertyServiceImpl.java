package org.trc.service.impl.tairan;


import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.trc.domain.category.Property;
import org.trc.domain.category.PropertyValue;
import org.trc.service.impl.tairan.util.CommomUtils;
import org.trc.service.tairan.TPropertyService;
import org.trc.service.tairan.model.PropertyToTrc;
import org.trc.service.tairan.model.ResultModel;
import org.trc.util.GuidUtil;
import org.trc.util.HttpClientUtil;
import org.trc.util.MD5;

import java.util.List;

/**
 * 泰然城分类回调
 * Created by hzdzf on 2017/5/24.
 */
@Service("tPropertyService")
public class TPropertyServiceImpl implements TPropertyService {

    private static final Logger logger = LoggerFactory.getLogger(TPropertyServiceImpl.class);

    private static final String or = "|";

    private static final String underLine = "_";

    @Override
    public ResultModel sendPropertyNotice(String action, Property oldProperty, Property property, List<PropertyValue> valueList, long operateTime) throws Exception {

        Assert.notNull(property.getSort(), "属性排序不能为空");
        Assert.notNull(property.getName(), "属性名称不能为空");
        Assert.notNull(property.getIsValid(), "属性是否停用不能为空");
        Assert.notNull(property.getDescription(), "属性描述不能为空");
        Assert.notNull(property.getTypeCode(), "属性类型编码不能为空");
        Assert.notNull(property.getValueType(), "属性值类型不能为空");

        PropertyToTrc propertyToTrc = new PropertyToTrc();
        propertyToTrc.setSort(property.getSort());
        propertyToTrc.setName(property.getName());
        propertyToTrc.setIsValid(property.getIsValid());
        propertyToTrc.setDescription(property.getDescription());
        propertyToTrc.setTypeCode(property.getTypeCode());
        propertyToTrc.setValueType(property.getValueType());

        //传值处理
        String noticeNum = GuidUtil.getNextUid(action + underLine);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(CommomUtils.getKey()).append(or).append(action).append(or).append(noticeNum).append(or).append(operateTime).append(or).
                append(propertyToTrc.getDescription()).append(or).append(propertyToTrc.getIsValid()).append(or).
                append(propertyToTrc.getName()).append(or).append(propertyToTrc.getSort()).append(or).append(propertyToTrc.getTypeCode()).
                append(or).append(propertyToTrc.getValueType());
        //MD5加密
        String sign = MD5.encryption(stringBuilder.toString()).toLowerCase();
        JSONObject params = new JSONObject();
        params.put("action", action);
        params.put("changeTime", operateTime);
        params.put("noticeNum", noticeNum);
        params.put("sign", sign);
        params.put("propertyToTrc", propertyToTrc);
        params.put("valueList", valueList);
        logger.info(params.toJSONString());
        String result = HttpClientUtil.httpPostJsonRequest(CommomUtils.getPropertyUrl(), params.toJSONString(), 10000);
        ResultModel resultModel = JSONObject.parseObject(result, ResultModel.class);
        return resultModel;
    }
}
