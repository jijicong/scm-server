package org.trc.biz.impl.category;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.trc.biz.category.IPropertyBiz;
import org.trc.biz.qinniu.IQinniuBiz;
import org.trc.domain.category.Property;
import org.trc.domain.category.PropertyValue;
import org.trc.enums.*;
import org.trc.exception.CategoryException;
import org.trc.form.FileUrl;
import org.trc.form.category.PropertyForm;
import org.trc.service.category.IPropertyService;
import org.trc.service.category.IPropertyValueService;
import org.trc.util.*;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

/**
 * Created by hzqph on 2017/5/5.
 */
@Service("propertyBiz")
public class PropertyBiz implements IPropertyBiz {

    private Logger  log = LoggerFactory.getLogger(PropertyBiz.class);

    //多个属性ID分隔符
    public final static String MULTI_PRRPERTY_ID_SPLIT = ",";

    @Autowired
    private IPropertyService propertyService;
    @Autowired
    private IPropertyValueService propertyValueService;
    @Autowired
    private IQinniuBiz qinniuBiz;

    @Override
    public Pagenation<Property> propertyPage(PropertyForm queryModel, Pagenation<Property> page) throws Exception {
        Example example = new Example(Property.class);
        Example.Criteria criteria = example.createCriteria();
        if (!StringUtils.isBlank(queryModel.getName())) {
            criteria.andLike("name", "%" + queryModel.getName() + "%");
        }
        if (!StringUtils.isBlank(queryModel.getTypeCode())) {
            criteria.andEqualTo("typeCode", queryModel.getTypeCode());
        }
        if (!StringUtils.isBlank(queryModel.getSort())) {
            criteria.andEqualTo("sort", queryModel.getSort());
        }
        if (!StringUtils.isBlank(queryModel.getIsValid())) {
            criteria.andEqualTo("isValid", queryModel.getIsValid());
        }
        example.orderBy("sort").asc();
        example.orderBy("updateTime").desc();
        return propertyService.pagination(example, page, queryModel);
    }


    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void saveProperty(Property property) throws Exception {
        AssertUtil.notNull(property, "属性管理模块保存属性信息失败，属性信息为空");
        ParamsUtil.setBaseDO(property);
        property.setLastEditOperator("小明");//TODO 后期用户信息引入之后需要修改
        //保存属性数据
        try {
            propertyService.insert(property);
        } catch (Exception e) {
            String str = CommonUtil.joinStr("保存属性" + JSON.toJSONString(property) + "到数据库失败").toString();
            log.error(str);
            throw new CategoryException(ExceptionEnum.CATEGORY_PROPERTY_SAVE_EXCEPTION, str);
        }
        List<PropertyValue> propertyValueList = JSONArray.parseArray(property.getGridValue(), PropertyValue.class);
        //验证属性值信息
        AssertUtil.notNull(propertyValueList, "属性管理模块保存属性值信息失败，属性值信息为空");
        for (int i = 0; i < propertyValueList.size(); i++) {
            PropertyValue propertyValue = propertyValueList.get(i);
            propertyValue.setSort(i);
            propertyValue.setPropertyId(property.getId());
            ParamsUtil.setBaseDO(propertyValue);
            propertyValue.setCreateTime(property.getCreateTime());
            propertyValue.setUpdateTime(property.getUpdateTime());
            //保存属性值信息
            try {
                propertyValueService.insertSelective(propertyValue);
            } catch (Exception e) {
                String str = CommonUtil.joinStr("属性值类型" + JSON.toJSONString(propertyValue) + "保存失败").toString();
                log.error(str);
                throw new CategoryException(ExceptionEnum.CATEGORY_PROPERTY_VALUE_SAVE_EXCEPTION, str);
            }
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void updateProperty(Property property) throws Exception {
        AssertUtil.notNull(property.getId(), "根据ID更新属性信息参数ID为空");
        property.setUpdateTime(Calendar.getInstance().getTime());
        int count = propertyService.updateByPrimaryKeySelective(property);
        if (count < 1) {
            String msg = CommonUtil.joinStr("根据主键ID[id=", property.getId().toString(), "]更新属性明细失败").toString();
            log.error(msg);
            throw new CategoryException(ExceptionEnum.CATEGORY_PROPERTY_UPDATE_EXCEPTION, msg);
        }
        List<PropertyValue> propertyValueList = JSONArray.parseArray(property.getGridValue(), PropertyValue.class);
        //验证属性值信息
        AssertUtil.notNull(propertyValueList, "属性管理模块更新属性值信息失败，属性值信息为空");
        Integer sort = 0;
        for (int i = 0; i < propertyValueList.size(); i++) {
            PropertyValue propertyValue = propertyValueList.get(i);
            //属性值操作标志
            int propertyValueCount = 0;
            if (propertyValue.getStatus().equals(RecordStatusEnum.DEFAULT.getCode())) {
                propertyValue.setSort(sort);
                propertyValue.setUpdateTime(property.getUpdateTime());
                propertyValueCount = propertyValueService.updateByPrimaryKeySelective(propertyValue);
            }
            //新增属性值数据操作
            if (propertyValue.getStatus().equals(RecordStatusEnum.ADD.getCode())) {
                propertyValue.setSort(sort);
                propertyValue.setPropertyId(property.getId());
                ParamsUtil.setBaseDO(propertyValue);
                propertyValue.setCreateTime(property.getUpdateTime());
                propertyValue.setUpdateTime(property.getUpdateTime());
                propertyValueCount = propertyValueService.insert(propertyValue);
            }
            //更新属性操作需要下发通知
            if (propertyValue.getStatus().equals(RecordStatusEnum.UPDATE.getCode())) {
                propertyValue.setSort(sort);
                propertyValue.setUpdateTime(property.getUpdateTime());
                propertyValueCount = propertyValueService.updateByPrimaryKeySelective(propertyValue);
            }
            //删除属性操作,需要下发通知
            if (propertyValue.getStatus().equals(RecordStatusEnum.DELETE.getCode())) {
                PropertyValue del = new PropertyValue();
                del.setId(propertyValue.getId());
                del.setIsDeleted(ZeroToNineEnum.ONE.getCode());
                del.setUpdateTime(property.getUpdateTime());
                propertyValueCount = propertyValueService.updateByPrimaryKeySelective(del);
            }
            if (!propertyValue.getStatus().equals(RecordStatusEnum.DELETE.getCode())) {
                sort += 1;
            }
            //更新属性值信息
            if (propertyValueCount < 1) {
                String str = CommonUtil.joinStr("属性值类型" + JSON.toJSONString(propertyValue) + "更新失败").toString();
                log.error(str);
                throw new CategoryException(ExceptionEnum.CATEGORY_PROPERTY_VALUE_UPDATE_EXCEPTION, str);
            }
        }
    }

    @Override
    public List<PropertyValue> queryListByPropertyId(Long propertyId) throws Exception {
        AssertUtil.notNull(propertyId, "属性管理模块属性值查询失败propertyId：" + propertyId);
        Example example = new Example(PropertyValue.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("propertyId", propertyId);
        criteria.andEqualTo("isDeleted", ZeroToNineEnum.ZERO.getCode());
        example.orderBy("isValid").desc();
        example.orderBy("sort").asc();
        return propertyValueService.selectByExample(example);
    }

    @Override
    public Property findPropertyById(Long id) throws Exception {
        AssertUtil.notNull(id, "根据ID查询属性明细参数ID为空");
        Property property = new Property();
        property.setId(id);
        property = propertyService.selectOne(property);
        if (null == property) {
            String msg = CommonUtil.joinStr("根据主键ID[id=", id.toString(), "]查询属性明细为空").toString();
            log.error(msg);
            throw new CategoryException(ExceptionEnum.CATEGORY_PROPERTY_QUERY_EXCEPTION, msg);
        }
        return property;
    }

    @Override
    public void updatePropertyStatus(Property property) throws Exception {
        AssertUtil.notNull(property.getId(), "根据属性ID更新属性状态，属性信息为空");
        Property updateProperty = new Property();
        updateProperty.setId(property.getId());
        if (property.getIsValid().equals(ValidEnum.VALID.getCode())) {
            updateProperty.setIsValid(ValidEnum.NOVALID.getCode());
        } else {
            updateProperty.setIsValid(ValidEnum.VALID.getCode());
        }
        int count = propertyService.updateByPrimaryKeySelective(updateProperty);
        if (count < 1) {
            String msg = CommonUtil.joinStr("根据主键ID[id=", updateProperty.getId().toString(), "]更新属性状态失败").toString();
            log.error(msg);
            throw new CategoryException(ExceptionEnum.CATEGORY_BRAND_QUERY_EXCEPTION, msg);
        }
    }

    @Override
    public List<PropertyValue> queryPropertyValueByPropertyIds(String propertyIds) throws Exception {
        AssertUtil.notBlank(propertyIds, "根据属性ID批量查询属性值参数属性ID不能为空");
        String[] tmpIds = propertyIds.split(MULTI_PRRPERTY_ID_SPLIT);
        Example example = new Example(PropertyValue.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("propertyId", Arrays.asList(tmpIds));
        criteria.andEqualTo("isValid", ZeroToNineEnum.ONE.getCode());
        criteria.andEqualTo("isDeleted", ZeroToNineEnum.ZERO.getCode());
        example.orderBy("sort").asc();
        List<PropertyValue> propertyValues = propertyValueService.selectByExample(example);
        AssertUtil.notEmpty(propertyValues, String.format("根据多个属性ID[%s]批量查询属性值为空", propertyIds));
        setPicPropertyUrl(propertyValues);
        return propertyValues;
    }

    /**
     * 根据输入文本查找属性或者查询所有
     *
     * @return
     * @throws Exception
     */
    @Override
    public List<Property> searchProperty(String queryString) throws Exception {
        Example example = new Example(Property.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("isValid", 1);
        criteria.andEqualTo("isDeleted", ZeroToNineEnum.ZERO.getCode());
        if (!StringUtils.isBlank(queryString)){
            criteria.andLike("name", "%" + queryString + "%");
        }
        example.orderBy("isValid").desc();
        example.orderBy("sort").asc();
        return propertyService.selectByExample(example);
    }

    /**
     * 设置图片属性的图片缩略图url访问路径
     *
     * @param propertyValues
     * @throws Exception
     */
    private void setPicPropertyUrl(List<PropertyValue> propertyValues) throws Exception {
        List<String> fileNames = new ArrayList<String>();
        for (PropertyValue val : propertyValues) {
            if (StringUtils.isNotBlank(val.getPicture())) {
                fileNames.add(val.getPicture());
            }
        }
        if (fileNames.size() > 0) {
            List<FileUrl> files = qinniuBiz.batchGetFileUrl(fileNames.toArray(new String[fileNames.size()]), "1");
            for (PropertyValue val : propertyValues) {
                for (FileUrl f : files) {
                    if (StringUtils.equals(f.getFileKey(), val.getPicture())) {
                        val.setPicUrl(f.getUrl());
                    }
                }
            }
        }
    }


}
