package org.trc.biz.category;

import org.trc.domain.category.Property;
import org.trc.domain.category.PropertyValue;
import org.trc.form.category.PropertyForm;
import org.trc.util.Pagenation;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * Created by hzqph on 2017/5/5.
 */
public interface IPropertyBiz {

    /**
     * 属性管理页面分页查询
     *
     * @param queryModel
     * @param page
     * @return
     * @throws Exception
     */
    Pagenation<Property> propertyPage(PropertyForm queryModel, Pagenation<Property> page) throws Exception;

    /**
     * 保存属性信息
     *
     * @param property
     * @return
     * @throws Exception
     */
    void saveProperty(Property property) throws Exception;

    /**
     * 更新属性信息
     *
     * @param property
     * @param id
     * @return
     * @throws Exception
     */
    void updateProperty(Property property) throws Exception;

    /**
     * 根据propertyId查询属性值列表
     *
     * @param propertyId
     * @return
     */
    List<PropertyValue> queryListByPropertyId(Long propertyId) throws Exception;

    /**
     * 根据属性id查询属性信息
     * @param id
     * @return
     * @throws Exception
     */
    Property findPropertyById(Long id) throws Exception;

    /**
     * 根据属性id修改属性信息
     * @param property
     * @throws Exception
     */
    void updatePropertyStatus(Property property) throws Exception;

    /**
     * 查询所有属性
     */
    List<Property> queryAllProperty() throws Exception;

    /**
     * 根据多个属性ID批量查询属性值
     * @param propertyIds 属性ID,多个用逗号","分隔
     * @return
     * @throws Exception
     */
    List<PropertyValue> queryPropertyValueByPropertyIds(String propertyIds) throws Exception;
}
