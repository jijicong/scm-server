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
import org.trc.biz.trc.ITrcBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.category.Property;
import org.trc.domain.category.PropertyValue;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.enums.*;
import org.trc.exception.CategoryException;
import org.trc.form.FileUrl;
import org.trc.form.category.PropertyForm;
import org.trc.service.category.ICategoryPropertyService;
import org.trc.service.category.IPropertyService;
import org.trc.service.category.IPropertyValueService;
import org.trc.service.config.ILogInfoService;
import org.trc.service.goods.IItemNatureProperyService;
import org.trc.service.goods.IItemSalesProperyService;
import org.trc.service.impl.impower.AclUserAccreditInfoService;
import org.trc.util.*;
import tk.mybatis.mapper.entity.Example;

import javax.ws.rs.container.ContainerRequestContext;
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
    @Autowired
    private ICategoryPropertyService categoryPropertyService;
    @Autowired
    private IItemNatureProperyService itemNatureProperyService;
    @Autowired
    private IItemSalesProperyService itemSalesProperyService;
    @Autowired
    private AclUserAccreditInfoService userAccreditInfoService;
    @Autowired
    private ILogInfoService logInfoService;
    @Autowired
    private ITrcBiz trcBiz;

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
        example.orderBy("updateTime").desc();
        page=propertyService.pagination(example, page, queryModel);
        List<Property> list=page.getResult();
        Map<String, AclUserAccreditInfo> userAccreditInfoMap=constructUserAccreditInfoMap(list);
        for (Property property : list) {
            if(!StringUtils.isBlank(property.getLastEditOperator())){
                if(userAccreditInfoMap!=null){
                    AclUserAccreditInfo aclUserAccreditInfo =userAccreditInfoMap.get(property.getLastEditOperator());
                    if(aclUserAccreditInfo !=null){
                        property.setLastEditOperator(aclUserAccreditInfo.getName());
                    }
                }
            }
        }
        page.setResult(list);
        return page;
    }


    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void saveProperty(Property property, ContainerRequestContext requestContext) throws Exception {
        AssertUtil.notNull(property, "属性管理模块保存属性信息失败，属性信息为空");
        ParamsUtil.setBaseDO(property);
        String userId= (String) requestContext.getProperty(SupplyConstants.Authorization.USER_ID);
        AclUserAccreditInfo aclUserAccreditInfo= (AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO);
        if(!StringUtils.isBlank(userId)){
            property.setLastEditOperator(userId);
            property.setCreateOperator(userId);
        }
        //保存属性数据
        try {
            propertyService.insert(property);
        } catch (Exception e) {
            log.error(e.getMessage());
            String str = CommonUtil.joinStr("保存属性" + JSON.toJSONString(property) + "到数据库失败").toString();
            throw new CategoryException(ExceptionEnum.CATEGORY_PROPERTY_SAVE_EXCEPTION, str);
        }
        List<PropertyValue> propertyValueList = JSONArray.parseArray(property.getGridValue(), PropertyValue.class);
        //验证属性值信息
        AssertUtil.notNull(propertyValueList, "属性管理模块保存属性值信息失败，属性值信息为空");
        List<PropertyValue> newPropertyValueList = new ArrayList<>();
        for (int i = 0; i < propertyValueList.size(); i++) {
            PropertyValue propertyValue = propertyValueList.get(i);
            propertyValue.setSort(i);
            propertyValue.setPropertyId(property.getId());
            propertyValue.setCreateTime(property.getCreateTime());
            propertyValue.setUpdateTime(property.getUpdateTime());
            if(!StringUtils.isBlank(userId)){
                propertyValue.setCreateOperator(userId);
            }
            //保存属性值信息
            try {
                propertyValueService.insertSelective(propertyValue);
                newPropertyValueList.add(propertyValue);
            } catch (Exception e) {
                log.error(e.getMessage());
                String str = CommonUtil.joinStr("属性值类型" + JSON.toJSONString(propertyValue) + "保存失败").toString();
                throw new CategoryException(ExceptionEnum.CATEGORY_PROPERTY_VALUE_SAVE_EXCEPTION, str);
            }
        }
        try{
            trcBiz.sendProperty(TrcActionTypeEnum.ADD_PROPERTY,null,property,null,newPropertyValueList,System.currentTimeMillis());
        }catch (Exception e){
            log.error("属性新增渠道通知调用出现异常,message:{}",e.getMessage(),e);
        }
        logInfoService.recordLog(property,property.getId().toString(),aclUserAccreditInfo,LogOperationEnum.ADD,null);
    }

    /**
     * 1.判断用户是否有改变属性值类型，图片/文字，如果改变了需将原先的属性值改为不可用，并改变相关关联关系表
     * 2.判断用户是否有启停用属性，如果改变了，需要改变相关关联关系表
     * 3.更新属性
     * 4.更新属性值属性值，也需要更新相关关联关系表
     * @param property
     * @throws Exception
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void updateProperty(Property property, ContainerRequestContext requestContext) throws Exception {
        AssertUtil.notNull(property.getId(), "根据ID更新属性信息参数ID为空");
        //先判断用户更新信息时是否有改变属性值类型如：图片--->文字，并删除之前的数据
        Property selectProperty=propertyService.selectOneById(property.getId());
        //查询还未变更之前的属性值列表
        Example example=new Example(PropertyValue.class);
        Example.Criteria criteria=example.createCriteria();
        criteria.andEqualTo("isValid",ZeroToNineEnum.ONE.getCode());
        criteria.andEqualTo("isDeleted",ZeroToNineEnum.ZERO.getCode());
        criteria.andEqualTo("propertyId",property.getId());
        List<PropertyValue> selectPropertyValues=propertyValueService.selectByExample(example);
        property.setUpdateTime(Calendar.getInstance().getTime());
        String userId= (String) requestContext.getProperty(SupplyConstants.Authorization.USER_ID);
        AclUserAccreditInfo aclUserAccreditInfo= (AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO);
        if(!StringUtils.isBlank(userId)){
            property.setLastEditOperator(userId);
        }
        //需要判断用户是否有修改了属性值类型
        if(!property.getValueType().equals(selectProperty.getValueType())){
            //用户修改属性值类型，先删除原先的属性值
            int count=propertyValueService.updateIsValidByPropertyId(property.getId());
            if (count <1){
                String str=CommonUtil.joinStr("属性值类型更新失败propertyId:"+property.getId()).toString();
                log.error(str);
                throw new CategoryException(ExceptionEnum.CATEGORY_PROPERTY_VALUE_UPDATE_EXCEPTION,str);
            }
            //更新自然属性表中关联记录状态,不用判断返回值因为不确定是否有关联关系
            if(PropertyTypeEnum.NATURE_PROPERTY.getCode().equals(property.getTypeCode())){
                itemNatureProperyService.updateIsValidByPropertyId(ZeroToNineEnum.ZERO.getCode(),property.getId());
            }
            //更新销售属性表中关联记录状态,不用判断返回值因为不确定是否有关联关系
            if(PropertyTypeEnum.PURCHASE_PROPERTY.getCode().equals(property.getTypeCode())){
                itemSalesProperyService.updateIsValidByPropertyId(ZeroToNineEnum.ZERO.getCode(),property.getId());
            }
        }
        int count = propertyService.updateByPrimaryKeySelective(property);
        if (count < 1) {
            String msg = CommonUtil.joinStr("根据主键ID[id=", property.getId().toString(), "]更新属性明细失败").toString();
            log.error(msg);
            throw new CategoryException(ExceptionEnum.CATEGORY_PROPERTY_UPDATE_EXCEPTION, msg);
        }
        //需判断用户是否有改变属性启停用状态如果有变更需要更改关联关系表
        if(!property.getIsValid().equals(selectProperty.getIsValid())){
            //属性状态更新时需要更新属性分类关系表的is_valid字段，但可能此时该属性还未使用，故不对返回值进行判断
            categoryPropertyService.updateCategoryPropertyIsValid(property.getIsValid(),property.getId());
            //更新自然属性表中关联记录状态,不用判断返回值因为不确定是否有关联关系
            if(PropertyTypeEnum.NATURE_PROPERTY.getCode().equals(property.getTypeCode())){
                itemNatureProperyService.updateIsValidByPropertyId(property.getIsValid(),property.getId());
            }
            //更新销售属性表中关联记录状态,不用判断返回值因为不确定是否有关联关系
            if(PropertyTypeEnum.PURCHASE_PROPERTY.getCode().equals(property.getTypeCode())){
                itemSalesProperyService.updateIsValidByPropertyId(property.getIsValid(),property.getId());
            }
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
            //更新属性值操作需要下发通知
            if (propertyValue.getStatus().equals(RecordStatusEnum.UPDATE.getCode())) {
                propertyValue.setSort(sort);
                propertyValue.setUpdateTime(property.getUpdateTime());
                propertyValueCount = propertyValueService.updateByPrimaryKeySelective(propertyValue);
            }
            //删除属性值操作(其实为属性禁用),需要下发通知
            if (propertyValue.getStatus().equals(RecordStatusEnum.DELETE.getCode())) {
                PropertyValue del = new PropertyValue();
                del.setId(propertyValue.getId());
                del.setIsValid(ZeroToNineEnum.ZERO.getCode());
                del.setUpdateTime(property.getUpdateTime());
                propertyValueCount = propertyValueService.updateByPrimaryKeySelective(del);
                //更新自然属性表中关联记录状态,不用判断返回值因为不确定是否有关联关系
                if(PropertyTypeEnum.NATURE_PROPERTY.getCode().equals(property.getTypeCode())){
                    itemNatureProperyService.updateIsValidByPropertyValueId(ZeroToNineEnum.ZERO.getCode(),propertyValue.getId());
                }
                //更新销售属性表中关联记录状态,不用判断返回值因为不确定是否有关联关系
                if(PropertyTypeEnum.PURCHASE_PROPERTY.getCode().equals(property.getTypeCode())){
                    itemSalesProperyService.updateIsValidByPropertyValueId(ZeroToNineEnum.ZERO.getCode(),propertyValue.getId());
                }
            }
            //用户进行删除操作是不占用sort字段的
            if (!propertyValue.getStatus().equals(RecordStatusEnum.DELETE.getCode())) {
                sort += 1;
            }
            //更新属性值信息结果判断
            if (propertyValueCount < 1) {
                String str = CommonUtil.joinStr("属性值类型" + JSON.toJSONString(propertyValue) + "更新失败").toString();
                log.error(str);
                throw new CategoryException(ExceptionEnum.CATEGORY_PROPERTY_VALUE_UPDATE_EXCEPTION, str);
            }
        }
        //通知渠道方
        //查询变更后的属性信息
        Property newProperty=propertyService.selectOneById(property.getId());
        //查询变更后的属性值信息
        Example newPropertyValueExample=new Example(PropertyValue.class);
        Example.Criteria newPropertyValueCriteria=example.createCriteria();
        criteria.andEqualTo("isValid",ZeroToNineEnum.ONE.getCode());
        criteria.andEqualTo("isDeleted",ZeroToNineEnum.ZERO.getCode());
        criteria.andEqualTo("propertyId",property.getId());
        List<PropertyValue> newPropertyValueList=propertyValueService.selectByExample(example);
        try{
            trcBiz.sendProperty(TrcActionTypeEnum.EDIT_PROPERTY,selectProperty,newProperty,selectPropertyValues,newPropertyValueList,System.currentTimeMillis());
        }catch (Exception e){
            log.error("属性新增渠道通知调用出现异常,message:{}",e.getMessage(),e);
        }
        //记录日志
        String remark=null;
        if(!property.getIsValid().equals(selectProperty.getIsValid())){
            if(property.getIsValid().equals(ValidEnum.VALID.getCode())){
                 remark=remarkEnum.VALID_OFF.getMessage();
            }else{
                 remark=remarkEnum.VALID_ON.getMessage();
            }
        }
        logInfoService.recordLog(property,property.getId().toString(),aclUserAccreditInfo,LogOperationEnum.UPDATE,remark);
    }

    @Override
    public List<PropertyValue> queryListByPropertyId(Long propertyId) throws Exception {
        AssertUtil.notNull(propertyId, "属性管理模块属性值查询失败propertyId：" + propertyId);
        Example example = new Example(PropertyValue.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("propertyId", propertyId);
        criteria.andEqualTo("isValid", ZeroToNineEnum.ONE.getCode());
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
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void updatePropertyStatus(Property property, ContainerRequestContext requestContext) throws Exception {
        AssertUtil.notNull(property.getId(), "根据属性ID更新属性状态，属性信息为空");
        //查询变更属性
        Property selectProperty=propertyService.selectOneById(property.getId());
        //查询需要通知给渠道的属性值信息
        Example example=new Example(PropertyValue.class);
        Example.Criteria criteria=example.createCriteria();
        criteria.andEqualTo("isValid",ZeroToNineEnum.ONE.getCode());
        criteria.andEqualTo("isDeleted",ZeroToNineEnum.ZERO.getCode());
        criteria.andEqualTo("propertyId",property.getId());
        List<PropertyValue> selectPropertyValues=propertyValueService.selectByExample(example);
        String remark=null;
        Property updateProperty = new Property();
        updateProperty.setId(property.getId());
        String userId= (String) requestContext.getProperty(SupplyConstants.Authorization.USER_ID);
        AclUserAccreditInfo aclUserAccreditInfo= (AclUserAccreditInfo)requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO);
        if(!StringUtils.isBlank(userId)){
            property.setLastEditOperator(userId);
        }
        if (property.getIsValid().equals(ValidEnum.VALID.getCode())) {
            updateProperty.setIsValid(ValidEnum.NOVALID.getCode());
            remark=remarkEnum.VALID_OFF.getMessage();
        } else {
            updateProperty.setIsValid(ValidEnum.VALID.getCode());
            remark=remarkEnum.VALID_ON.getMessage();
        }
        int count = propertyService.updateByPrimaryKeySelective(updateProperty);
        if (count < 1) {
            String msg = CommonUtil.joinStr("根据主键ID[id=", updateProperty.getId().toString(), "]更新属性状态失败").toString();
            log.error(msg);
            throw new CategoryException(ExceptionEnum.CATEGORY_PROPERTY_UPDATE_EXCEPTION, msg);
        }
        //属性状态更新时需要更新属性分类关系表的is_valid字段，但可能此时该属性还未使用，故不对返回值进行判断
        categoryPropertyService.updateCategoryPropertyIsValid(updateProperty.getIsValid(),updateProperty.getId());
        //更新自然属性表中关联记录状态,不用判断返回值因为不确定是否有关联关系
        if(PropertyTypeEnum.NATURE_PROPERTY.getCode().equals(property.getTypeCode())){
            itemNatureProperyService.updateIsValidByPropertyId(updateProperty.getIsValid(),updateProperty.getId());
        }
        //更新销售属性表中关联记录状态,不用判断返回值因为不确定是否有关联关系
        if(PropertyTypeEnum.PURCHASE_PROPERTY.getCode().equals(property.getTypeCode())){
            itemSalesProperyService.updateIsValidByPropertyId(updateProperty.getIsValid(),updateProperty.getId());
        }
        //通知渠道属性变更通知
        Property newProperty=propertyService.selectOneById(property.getId());
        try{
            trcBiz.sendProperty(TrcActionTypeEnum.EDIT_PROPERTY,selectProperty,newProperty,selectPropertyValues,selectPropertyValues,System.currentTimeMillis());
        }catch (Exception e){
            log.error("属性新增渠道通知调用出现异常,message:{}",e.getMessage(),e);
        }
        logInfoService.recordLog(property,property.getId().toString(),aclUserAccreditInfo,LogOperationEnum.UPDATE,remark);
    }

    @Override
    public List<PropertyValue> queryPropertyValueByPropertyIds(String propertyIds) throws Exception {
        AssertUtil.notBlank(propertyIds, "根据属性ID批量查询属性值参数属性ID不能为空");
        String[] tmpIds = propertyIds.split(MULTI_PRRPERTY_ID_SPLIT);
        Example example = new Example(PropertyValue.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("propertyId", Arrays.asList(tmpIds));
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

    private Map<String,AclUserAccreditInfo> constructUserAccreditInfoMap(List<Property> propertyList){
        if(AssertUtil.collectionIsEmpty(propertyList)){
            return null;
        }
        Set<String> userIdsSet=new HashSet<>();
        for (Property property:propertyList) {
            userIdsSet.add(property.getLastEditOperator());
        }
        String[] userIdArr=new String[userIdsSet.size()];
        userIdsSet.toArray(userIdArr);
        return userAccreditInfoService.selectByIds(userIdArr);
    }
}
