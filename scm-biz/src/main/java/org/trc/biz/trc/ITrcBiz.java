package org.trc.biz.trc;

import org.trc.domain.category.*;
import org.trc.enums.CategoryActionTypeEnum;
import org.trc.model.ResultModel;

import java.util.List;

/**
 * 泰然城交互
 * Created by hzdzf on 2017/6/7.
 */
public interface ITrcBiz {

    /**
     * @param action      行为
     * @param oldBrand    旧品牌信息
     * @param brand       品牌信息
     * @param operateTime 时间戳
     * @return 渠道调回信息
     */
    ResultModel sendBrand(String action, Brand oldBrand, Brand brand, long operateTime) throws Exception;

    /**
     * @param action      行为
     * @param oldProperty 旧属性信息
     * @param property    属性信息
     * @param valueList   修改后属性值信息
     * @param operateTime 时间戳
     * @return 渠道调回信息
     * @throws Exception
     */
    ResultModel sendProperty(String action, Property oldProperty, Property property, List<PropertyValue> valueList, long operateTime) throws Exception;

    /**
     * @param action               行为
     * @param oldCategory          旧分类信息
     * @param category             分类信息
     * @param categoryBrandList    分类品牌列表信息
     * @param categoryPropertyList 分类属性列表信息
     * @param operateTime          时间戳
     * @return 渠道调回信息
     * @throws Exception
     */
    ResultModel sendCategory(CategoryActionTypeEnum action, Category oldCategory, Category category,
                             List<CategoryBrand> categoryBrandList, List<CategoryProperty> categoryPropertyList, long operateTime) throws Exception;
}
