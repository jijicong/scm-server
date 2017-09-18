package org.trc.service;

import com.alibaba.fastjson.JSON;
import com.qiniu.util.Json;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.trc.domain.category.Brand;
import org.trc.domain.category.Property;
import org.trc.domain.category.PropertyValue;
import org.trc.domain.trcDomain.Brands;
import org.trc.domain.trcDomain.Properties;
import org.trc.domain.trcDomain.PropertyValues;
import org.trc.enums.SourceEnum;
import org.trc.enums.ZeroToNineEnum;
import org.trc.service.impl.category.BrandService;
import org.trc.service.impl.category.PropertyService;
import org.trc.service.impl.category.PropertyValueService;
import org.trc.service.impl.trcCategory.TrcBrandsService;
import org.trc.service.impl.util.SerialUtilService;
import org.trc.service.trcCategory.ITrcPropertiesService;
import org.trc.service.trcCategory.ITrcPropertiesValuesService;
import org.trc.util.AssertUtil;
import org.trc.util.DateUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)  //标记测试运行的环境
@ContextConfiguration(locations = {"classpath:config/resource-context.xml"}) //配合spring测试  可以引入多个配置文件
public class TrcToScmCategoryTest {
    @Autowired
    private TrcBrandsService trcBrandsService;
    @Autowired
    private BrandService brandService;
    @Autowired
    private SerialUtilService serialUtilService;
    @Autowired
    private PropertyService propertyService;
    @Autowired
    private ITrcPropertiesService trcPropertiesService;

    @Autowired
    private ITrcPropertiesValuesService trcPropertiesValuesService;
    @Autowired
    private PropertyValueService propertyValueService;


    private final static String BRAND_CODE_EX_NAME = "PP";
    private final static int BRAND_CODE_LENGTH = 5;


    @Test
    public void brandTest() {
        Example example = new Example(Brands.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("disabled", "0");

        List<Brands> brandsList = trcBrandsService.selectByExample(example);
        List<Brand> brandListScm = new ArrayList<>();
        if (!AssertUtil.collectionIsEmpty(brandsList)) {
            for (Brands brandTrc : brandsList) {
                Brand brandScm = new Brand();
                brandScm.setId(Long.valueOf(brandTrc.getBrandId()));
                brandScm.setAlise(brandTrc.getAlias());
                brandScm.setLogo(brandTrc.getLogo());
                brandScm.setName(brandTrc.getName());
                brandScm.setIsValid(ZeroToNineEnum.ONE.getCode());
                brandScm.setSource(SourceEnum.TRC.getCode());
                brandScm.setSort(brandTrc.getSortOrder());
                brandScm.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
                brandScm.setCreateOperator("admin");
                brandScm.setLastEditOperator("admin");
                brandScm.setWebUrl("");
                brandScm.setCreateTime(Calendar.getInstance().getTime());
                brandScm.setUpdateTime(brandScm.getCreateTime());
                brandScm.setBrandCode(serialUtilService.generateCode(BRAND_CODE_LENGTH, BRAND_CODE_EX_NAME, DateUtils.dateToCompactString(brandScm.getCreateTime())));

                brandListScm.add(brandScm);
            }
            brandService.insertList(brandListScm);
        }

    }
    @Test
    public void propertyTest() {
        Example example = new Example(Properties.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("disabled", "0");
        List<Properties> propertiesList = trcPropertiesService.selectByExample(example);
        List<Property> propertyList = new ArrayList<>();
        if (!AssertUtil.collectionIsEmpty(propertiesList)) {
            for (Properties properties:  propertiesList) {
                Property property = new Property();
                property.setId(Long.valueOf(properties.getPropertyId()));
                property.setDescription(properties.getDescription());
                property.setLastEditOperator("admin");
                property.setName(properties.getName());
                if (properties.getType().equals("Natural")){
                    property.setTypeCode("natureProperty");
                }else {
                    property.setTypeCode("purchaseProperty");
                }
                if (properties.getShowType().equals("Text")){
                    property.setValueType(ZeroToNineEnum.ZERO.getCode());
                }else {
                    property.setValueType(ZeroToNineEnum.ONE.getCode());
                }
                property.setIsValid(ZeroToNineEnum.ONE.getCode());
                property.setSort(properties.getSortOrder());
                property.setCreateOperator("admin");
                property.setLastEditOperator("admin");
                property.setCreateTime(Calendar.getInstance().getTime());
                property.setUpdateTime(property.getCreateTime());
                property.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
                propertyList.add(property);
            }
            propertyService.insertList(propertyList);
        }
    }


    @Test
    public void propertyValueTest() {
        Example example = new Example(PropertyValues.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIsNotNull("propertyValueId");
        List<PropertyValues> propertyValuesList = trcPropertiesValuesService.selectByExample(example);
        List<PropertyValue> propertyValueList = new ArrayList<>();
        if (!AssertUtil.collectionIsEmpty(propertyValuesList)) {
            for (PropertyValues propertyValues:  propertyValuesList) {
                PropertyValue propertyValueScm = new PropertyValue();
                propertyValueScm.setId(Long.valueOf(propertyValues.getPropertyValueId()));
                propertyValueScm.setPropertyId(Long.valueOf(propertyValues.getPropertyId()));
                propertyValueScm.setPicture(propertyValues.getImage());
                propertyValueScm.setValue(propertyValues.getText());
                propertyValueScm.setCreateTime(Calendar.getInstance().getTime());
                propertyValueScm.setUpdateTime(propertyValueScm.getCreateTime());
                propertyValueScm.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
                propertyValueScm.setIsValid(ZeroToNineEnum.ONE.getCode());
                propertyValueScm.setCreateOperator("admin");
                propertyValueScm.setSort(propertyValues.getSortOrder());
                propertyValueList.add(propertyValueScm);

            }
            propertyValueService.insertList(propertyValueList);
        }
    }

}
