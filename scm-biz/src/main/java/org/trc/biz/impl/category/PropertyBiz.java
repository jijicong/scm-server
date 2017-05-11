package org.trc.biz.impl.category;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.trc.biz.category.IPropertyBiz;
import org.trc.domain.category.Property;
import org.trc.domain.category.PropertyValue;
import org.trc.enums.CommonExceptionEnum;
import org.trc.enums.ExceptionEnum;
import org.trc.exception.CategoryException;
import org.trc.exception.ParamValidException;
import org.trc.form.category.PropertyForm;
import org.trc.service.category.IPropertyService;
import org.trc.service.category.IPropertyValueService;
import org.trc.util.CommonUtil;
import org.trc.util.DateUtils;
import org.trc.util.Pagenation;
import org.trc.util.ParamsUtil;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

/**
 * Created by hzqph on 2017/5/5.
 */
@Service("propertyBiz")
public class PropertyBiz implements IPropertyBiz {
    private final static Logger log = LoggerFactory.getLogger(PropertyBiz.class);

    @Autowired
    private IPropertyService propertyService;
    @Autowired
    private IPropertyValueService propertyValueService;

    @Override
    public Pagenation<Property> propertyPage(PropertyForm queryModel, Pagenation<Property> page) throws Exception {
        Example example=new Example(Property.class);
        Example.Criteria criteria=example.createCriteria();
        if (!StringUtils.isBlank(queryModel.getName())){
            criteria.andLike("name","%"+ queryModel.getName()+"%");
        }
        if (!StringUtils.isBlank(queryModel.getTypeCode())){
            criteria.andEqualTo("typeCode",queryModel.getTypeCode());
        }
        if(!StringUtils.isBlank(queryModel.getIsValid())){
            criteria.andEqualTo("isValid", queryModel.getIsValid());
        }
        example.orderBy("isValid").desc();
        example.orderBy("updateTime").desc();
        return propertyService.pagination(example,page,queryModel);
    }

    @Override
    public boolean saveProperty(Property property) throws Exception {
        //验证property是否为空
        validateBeanNotNull(property,ExceptionEnum.CATEGORY_PROPERTY_SAVE_EXCEPTION,"属性管理模块保存属性信息失败，属性信息为空");
        ParamsUtil.setBaseDO(property);
        int count=propertyService.insert(property);
        if (count <1){
            String str=CommonUtil.joinStr("保存属性"+JSON.toJSONString(property)+"到数据库失败").toString();
            log.error(str);
            throw new CategoryException(ExceptionEnum.CATEGORY_PROPERTY_SAVE_EXCEPTION,str);
        }
        List<PropertyValue> propertyValueList = JSONArray.parseArray(property.getGridValue(),PropertyValue.class);
        //验证属性值信息
        validateBeanNotNull(propertyValueList,ExceptionEnum.CATEGORY_PROPERTY_SAVE_EXCEPTION,"属性管理模块保存属性信息失败，属性值信息为空");
        for (int i = 0; i <propertyValueList.size() ; i++) {
            PropertyValue propertyValue=propertyValueList.get(i);
            propertyValue.setSort(i);
            propertyValue.setPropertyId(property.getId());
            ParamsUtil.setBaseDO(propertyValue);
            propertyValue.setCreateTime(property.getCreateTime());
            propertyValue.setUpdateTime(property.getUpdateTime());
            //插入属性值信息
            int propertyValueCount =propertyValueService.insertSelective(propertyValue);
            if(propertyValueCount<1){
                String str= CommonUtil.joinStr("属性值类型"+JSON.toJSONString(propertyValue)+"保存失败").toString();
                log.error(str);
                throw new CategoryException(ExceptionEnum.CATEGORY_PROPERTY_VALUE_SAVE_EXCEPTION,str);
            }
        }
        return true;
    }

    @Override
    public boolean updateProperty(Property property, Long id) throws Exception {
        if (null==id){
            String msg=CommonUtil.joinStr("根据ID更新属性信息参数ID为空").toString();
            log.error(msg);
            throw new CategoryException(ExceptionEnum.CATEGORY_PROPERTY_UPDATE_EXCEPTION,msg);
        }
        validateBeanNotNull(property,ExceptionEnum.CATEGORY_PROPERTY_UPDATE_EXCEPTION,"属性管理模块更新属性信息失败，属性信息为空");
        property.setId(id);
        property.setUpdateTime(DateUtils.dateFormat(new Date(),DateUtils.DATETIME_FORMAT));
        int count=propertyService.updateByPrimaryKeySelective(property);
        if (count<1){
            String msg=CommonUtil.joinStr("根据主键ID[id=",id.toString(),"]更新属性明细失败").toString();
            log.error(msg);
            throw new CategoryException(ExceptionEnum.CATEGORY_PROPERTY_UPDATE_EXCEPTION,msg);
        }
        List<PropertyValue> propertyValueList = JSONArray.parseArray(property.getGridValue(),PropertyValue.class);
        //验证属性值信息
        validateBeanNotNull(propertyValueList,ExceptionEnum.CATEGORY_PROPERTY_UPDATE_EXCEPTION,"属性管理模块更新属性值信息失败，属性值信息为空");
        for (int i = 0; i <propertyValueList.size() ; i++) {
            PropertyValue propertyValue=propertyValueList.get(i);
            propertyValue.setSort(i);
            propertyValue.setPropertyId(property.getId());
            ParamsUtil.setBaseDO(propertyValue);
            int propertyValueCount=0;
            if(null==propertyValue.getId()){
                propertyValueCount=propertyValueService.insert(propertyValue);
            }else{
                propertyValue.setUpdateTime(property.getUpdateTime());
                propertyValueCount =propertyValueService.updateByPrimaryKeySelective(propertyValue);
            }
            //更新属性值信息
            if(propertyValueCount<1){
                String str= CommonUtil.joinStr("属性值类型"+JSON.toJSONString(propertyValue)+"更新失败").toString();
                log.error(str);
                throw new CategoryException(ExceptionEnum.CATEGORY_PROPERTY_VALUE_UPDATE_EXCEPTION,str);
            }
        }
        return true;
    }

    @Override
    public List<PropertyValue> queryListByPropertyId(Long propertyId)throws Exception{
        validateBeanNotNull(propertyId,ExceptionEnum.CATEGORY_PROPERTY_VALUE_QUERY_EXCEPTION,"");
        Example example=new Example(PropertyValue.class);
        Example.Criteria criteria=example.createCriteria();
        criteria.andEqualTo("propertyId",propertyId);
        example.orderBy("isValid").desc();
        example.orderBy("sort").asc();
        return propertyValueService.selectByExample(example);
    }

    @Override
    public Property findPropertyById(Long id) throws Exception {
        if(null==id){
            String msg=CommonUtil.joinStr("根据ID查询属性明细参数ID为空").toString();
            log.error(msg);
            throw new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, msg);
        }
        Property property=new Property();
        property.setId(id);
        property=propertyService.selectOne(property);
        if (null==property){
            String msg=CommonUtil.joinStr("根据主键ID[id=",id.toString(),"]查询属性明细为空").toString();
            log.error(msg);
            throw new CategoryException(ExceptionEnum.CATEGORY_PROPERTY_QUERY_EXCEPTION,msg);
        }
        return property;
    }

    /**
     * 验证实体类是否为空
     * @param object
     * @param exceptionEnum
     * @param errorMessage
     * @throws Exception
     */
    private void validateBeanNotNull(Object object, ExceptionEnum exceptionEnum, String errorMessage)throws Exception{
        try{
            Assert.notNull(object,errorMessage);
        }catch (IllegalArgumentException e ){
            log.error(errorMessage);
            throw new CategoryException(exceptionEnum,errorMessage);
        }
    }
}
