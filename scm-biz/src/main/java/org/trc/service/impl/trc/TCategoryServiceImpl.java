package org.trc.service.impl.trc;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.trc.domain.category.Category;
import org.trc.service.trc.TCategoryService;
import org.trc.service.trc.model.CategoryToTrc;
import org.trc.util.GuidUtil;
import org.trc.util.HttpClientUtil;
import org.trc.util.MD5;

/**
 * 泰然城渠道分类回调
 * Created by hzdzf on 2017/5/25.
 */
@Service("tCategoryService")
public class TCategoryServiceImpl implements TCategoryService {

    private static final Logger logger = LoggerFactory.getLogger(TCategoryServiceImpl.class);

    @Override
    public String sendCategoryNotice(String action, Category category, long operateTime) throws Exception {

        CategoryToTrc categoryToTrc = new CategoryToTrc();
        categoryToTrc.setIsValid(category.getIsValid()==null?"":category.getIsValid());
        categoryToTrc.setName(category.getName()==null?"":category.getName());
        categoryToTrc.setClassifyDescribe(category.getClassifyDescribe()==null?"":category.getClassifyDescribe());
        if (category.getParentId()!=null){
            categoryToTrc.setParentId(category.getParentId());
        }
        if (category.getSort()!=null){
            categoryToTrc.setSort(category.getSort());
        }

        //传值处理
        String noticeNum = GuidUtil.getNextUid(action + "_");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(action).append("|").append(noticeNum).append("|").append(operateTime).append("|").
                append(categoryToTrc.getClassifyDescribe()).append("|").append(categoryToTrc.getIsValid()).append("|").append(categoryToTrc.getName()).append("|").
                append(categoryToTrc.getParentId()).append("|").append(categoryToTrc.getSort());
        //MD5加密
        String sign = MD5.encryption(stringBuilder.toString()).toLowerCase();
        JSONObject params = new JSONObject();
        params.put("action", action);
        params.put("changeTime", operateTime);
        params.put("noticeNum", noticeNum);
        params.put("sign", sign);
        params.put("categoryToTrc", categoryToTrc);
        //TODO URL和返回处理
        return HttpClientUtil.httpPostJsonRequest("url", params.toJSONString(), 1000);
    }
}
