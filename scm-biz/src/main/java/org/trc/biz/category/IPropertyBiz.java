package org.trc.biz.category;

import org.trc.domain.category.Property;
import org.trc.domain.category.PropertyValue;
import org.trc.form.category.PropertyForm;
import org.trc.util.Pagenation;

import java.util.List;

/**
 * Created by hzqph on 2017/5/5.
 */
public interface IPropertyBiz {

    /**
     * 属性管理页面分页查询
     * @param queryModel
     * @param page
     * @return
     * @throws Exception
     */
    public Pagenation<Property> propertyPage(PropertyForm queryModel, Pagenation<Property> page) throws Exception;

    /**
     * 保存属性信息
     * @param property
     * @return
     * @throws Exception
     */
    public boolean saveProperty(Property property)throws Exception;

    /**
     * 更新属性信息
     * @param property
     * @param id
     * @return
     * @throws Exception
     */
    public boolean updateProperty(Property property,Long id)throws Exception;

    /**
     * 根据propertyId查询属性值列表
     * @param propertyId
     * @return
     */
    public List<PropertyValue> queryListByPropertyId(Long propertyId)throws Exception;

    public Property findPropertyById(Long id)throws Exception;

    public int updatePropertyStatus(Property property)throws Exception;
}
