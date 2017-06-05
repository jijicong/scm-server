package org.trc.service.tairan;


import org.trc.domain.category.Category;
import org.trc.domain.category.CategoryBrand;
import org.trc.domain.category.CategoryProperty;
import org.trc.service.tairan.model.ResultModel;

import java.util.List;

/**
 * 泰然城渠道分类回调
 * Created by hzdzf on 2017/5/25.
 */
public interface TCategoryService {

    /**
     * @param action      行为
     * @param oldCategory 旧分类信息
     * @param category    分类信息
     * @param  categoryBrandList 分类品牌列表信息
     * @param  categoryPropertyList 分类属性列表信息
     * @param operateTime 时间戳
     * @return 渠道调回信息
     * @throws Exception
     */
    ResultModel sendCategoryNotice(String action, Category oldCategory, Category category,
                                   List<CategoryBrand> categoryBrandList, List<CategoryProperty> categoryPropertyList, long operateTime) throws Exception;
}
