package org.trc.service.impl.tairan;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.trc.domain.category.Category;
import org.trc.domain.category.CategoryBrand;
import org.trc.domain.category.CategoryProperty;
import org.trc.service.impl.tairan.util.CommomUtils;
import org.trc.service.tairan.TCategoryService;
import org.trc.service.tairan.model.CategoryToTrc;
import org.trc.service.tairan.model.ResultModel;
import org.trc.util.GuidUtil;
import org.trc.util.HttpClientUtil;
import org.trc.util.MD5;

import java.util.List;

/**
 * 泰然城渠道分类回调
 * Created by hzdzf on 2017/5/25.
 */
@Service("tCategoryService")
public class TCategoryServiceImpl implements TCategoryService {

    private static final Logger logger = LoggerFactory.getLogger(TCategoryServiceImpl.class);

    private static final String or = "|";

    private static final String underLine = "_";

    @Transactional
    @Override
    public ResultModel sendCategoryNotice(String action, Category oldCategory, Category category,
                                          List<CategoryBrand> categoryBrandList, List<CategoryProperty> categoryPropertyList, long operateTime) throws Exception {

        Assert.notNull(category.getIsValid(), "是否停用不能为空");
        Assert.notNull(category.getName(), "分类名称不能为空");
        Assert.notNull(category.getClassifyDescribe(), "分类描述不能为空");
        Assert.notNull(category.getSort(), "分类排序不能为空");

        CategoryToTrc categoryToTrc = new CategoryToTrc();
        categoryToTrc.setIsValid(category.getIsValid());
        categoryToTrc.setName(category.getName());
        categoryToTrc.setClassifyDescribe(category.getClassifyDescribe());
        categoryToTrc.setSort(category.getSort());
        if (category.getParentId() != null) {
            categoryToTrc.setParentId(category.getParentId());
        }

        //传值处理
        String noticeNum = GuidUtil.getNextUid(action + underLine);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(CommomUtils.getKey()).append(or).append(action).append(or).append(noticeNum).append(or).append(operateTime).append(or).
                append(categoryToTrc.getClassifyDescribe()).append(or).append(categoryToTrc.getIsValid()).append(or).append(categoryToTrc.getName()).append(or).
                append(categoryToTrc.getParentId()).append(or).append(categoryToTrc.getSort());
        //MD5加密
        String sign = MD5.encryption(stringBuilder.toString()).toLowerCase();
        JSONObject params = new JSONObject();
        params.put("action", action);
        params.put("changeTime", operateTime);
        params.put("noticeNum", noticeNum);
        params.put("sign", sign);
        params.put("categoryToTrc", categoryToTrc);
        params.put("categoryBrandList", categoryBrandList);
        params.put("categoryPropertyList", categoryPropertyList);
        logger.info(params.toJSONString());
        String result = HttpClientUtil.httpPostJsonRequest(CommomUtils.getCategoryUrl(), params.toJSONString(), 10000);
        ResultModel resultModel = JSONObject.parseObject(result, ResultModel.class);
        return resultModel;
    }
}
