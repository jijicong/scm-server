package org.trc.service;

import com.alibaba.fastjson.JSON;
import com.qiniu.util.Json;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.trc.domain.category.*;
import org.trc.domain.supplier.Supplier;
import org.trc.domain.trcDomain.*;
import org.trc.enums.ExceptionEnum;
import org.trc.enums.SourceEnum;
import org.trc.enums.ValidEnum;
import org.trc.enums.ZeroToNineEnum;
import org.trc.service.category.ICategoryBrandService;
import org.trc.service.category.ICategoryPropertyService;
import org.trc.service.category.ICategoryService;
import org.trc.service.impl.category.BrandService;
import org.trc.service.impl.category.PropertyService;
import org.trc.service.impl.category.PropertyValueService;
import org.trc.service.impl.trcCategory.TrcBrandsService;
import org.trc.service.impl.util.SerialUtilService;
import org.trc.service.trcCategory.*;
import org.trc.util.AssertUtil;
import org.trc.util.DateUtils;
import org.trc.util.Pagenation;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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

    @Autowired
    private ITrcCategoriesService trcCategoriesService;
    @Autowired
    private ICategoryService categoryService;

    @Autowired
    private ITrcCategoriesBrandsService trcCategoriesBrandsService;
    @Autowired
    private ICategoryBrandService categoryBrandService;

    @Autowired
    private ITrcCategoriesPropertiesService trcCategoriesPropertiesService;
    @Autowired
    private ICategoryPropertyService categoryPropertyService;

    private final static String BRAND_CODE_EX_NAME = "PP";
    private final static int BRAND_CODE_LENGTH = 5;
    private final static String CHANNEL_TRC = "QD001";

    private final static String FL_SERIALNAME = "FL";
    private final static Integer FL_LENGTH = 3;



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

                brandService.insert(brandScm);
            }
//            brandService.insertList(brandListScm);
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
                propertyService.insert(property);
            }
//            propertyService.insertList(propertyList);
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
                propertyValueService.insert(propertyValueScm);

            }
//            propertyValueService.insertList(propertyValueList);
        }
    }

    @Test
    public void categoryTest() {
        Example example = new Example(Categories.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("disabled", "0");

        List<Categories> categoriesList = trcCategoriesService.selectByExample(example);
        List<Category> categoryScm = new ArrayList<>();
        if (!AssertUtil.collectionIsEmpty(categoriesList)) {
            for (Categories categories : categoriesList) {
                Category category = new Category();
                category.setId(Long.valueOf(categories.getCategoryId()));
                category.setName(categories.getName());
                if (Long.valueOf(categories.getParentId())==0){
                    category.setParentId(null);
                }else {
                    category.setParentId(Long.valueOf(categories.getParentId()));
                }
                category.setLevel(categories.getLevel());
                category.setSort(categories.getSortOrder());
                category.setClassifyDescribe(categories.getDescription());
                int disabled = categories.getDisabled();
                if(disabled == Integer.parseInt(ZeroToNineEnum.ZERO.getCode()))//正常
                    category.setIsValid(ValidEnum.VALID.getCode());
                else
                    category.setIsValid(ValidEnum.NOVALID.getCode());
                category.setSource(CHANNEL_TRC);
                category.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
                Date currentDate = Calendar.getInstance().getTime();
                category.setCreateTime(currentDate);
                category.setUpdateTime(currentDate);
                Example example2 = new Example(Categories.class);
                Example.Criteria criteria2 = example.createCriteria();
                criteria2.andEqualTo("parentId", categories.getCategoryId());
                List<Categories> categoriesList2 = trcCategoriesService.selectByExample(example2);
                if (!AssertUtil.collectionIsEmpty(categoriesList2)){
                    category.setIsLeaf(ZeroToNineEnum.ZERO.getCode());
                }else {
                    category.setIsLeaf(ZeroToNineEnum.ONE.getCode());
                }
                StringBuilder sb = new StringBuilder();
                if(categories.getPrimaryId() > 0)
                    sb.append(categories.getPrimaryId());
                if(categories.getSecondaryId() > 0)
                    sb.append("|").append(categories.getSecondaryId());
                category.setFullPathId(sb.toString());
                category.setCategoryCode(serialUtilService.generateCode(FL_LENGTH, FL_SERIALNAME));

                categoryService.insert(category);
            }
//            categoryService.insertList(categoryScm);
        }
    }

    ExecutorService threadPool = Executors.newFixedThreadPool(100);

    @Test
    public void categoryBrandTest() {
        CategoryBrandRels categoryBrandRels = new CategoryBrandRels();
        List<CategoryBrandRels> categoryBrandRelsList = trcCategoriesBrandsService.select(categoryBrandRels);
        List<List<CategoryBrandRels>> categoryBrandList = split(categoryBrandRelsList, 5000);
        final CountDownLatch begin = new CountDownLatch(1);
        final CountDownLatch end = new CountDownLatch(categoryBrandList.size());
        for(List<CategoryBrandRels> categoryBrandRels1: categoryBrandList){
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    try{
                        if (!AssertUtil.collectionIsEmpty(categoryBrandRels1)) {
                            List<CategoryBrand> categoryBrands = new ArrayList<>();
                            for (CategoryBrandRels categoryBrandRels2 : categoryBrandRels1) {
                                Category category = categoryService.selectByPrimaryKey(Long.valueOf(categoryBrandRels2.getCategoryId()));
                                Brand brand = brandService.selectByPrimaryKey(Long.valueOf(categoryBrandRels2.getBrandId()));
                                if(null != category && null != brand && category.getLevel() == 3){
                                    CategoryBrand categoryBrand = new CategoryBrand();
                                    categoryBrand.setCategoryId(Long.valueOf(categoryBrandRels2.getCategoryId()));
                                    categoryBrand.setBrandId(Long.valueOf(categoryBrandRels2.getBrandId()));
                                    categoryBrand.setCategoryCode(category.getCategoryCode());
                                    categoryBrand.setBrandCode(brand.getBrandCode());
                                    categoryBrand.setIsValid(ValidEnum.VALID.getCode());
                                    categoryBrand.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
                                    Date currentDate = Calendar.getInstance().getTime();
                                    categoryBrand.setCreateTime(currentDate);
                                    categoryBrand.setUpdateTime(currentDate);
                                    if(category.getLevel() == 3){
                                        categoryBrands.add(categoryBrand);
                                    }
                                }
                            }
                            if(categoryBrands.size() > 0)
                                categoryBrandService.insertList(categoryBrands);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }finally {
                        // 任务完成，end就减一
                        end.countDown();
                    }

                }
            };
            threadPool.submit(runnable);
        }
        begin.countDown();
        try {
            end.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        threadPool.shutdown();
    }



    @Test
    public void categoryPropertyTest() {
        CategoryPropertyRels categoryBrandRels = new CategoryPropertyRels();
        List<CategoryPropertyRels> categoryPropertyRelsList = trcCategoriesPropertiesService.select(categoryBrandRels);
        if (!AssertUtil.collectionIsEmpty(categoryPropertyRelsList)) {
            for (CategoryPropertyRels categoryPropertyRels : categoryPropertyRelsList) {
                Category category = categoryService.selectByPrimaryKey(Long.valueOf(categoryPropertyRels.getCategoryId()));
                Property property = propertyService.selectByPrimaryKey(Long.valueOf(categoryPropertyRels.getPropertyId()));
                if(null != category && null != property && category.getLevel() == 3){
                    CategoryProperty categoryProperty = new CategoryProperty();
                    categoryProperty.setCategoryId(Long.valueOf(categoryPropertyRels.getCategoryId()));
                    categoryProperty.setPropertyId(Long.valueOf(categoryPropertyRels.getPropertyId()));
                    categoryProperty.setIsValid(ValidEnum.VALID.getCode());
                    categoryProperty.setPropertySort(1);
                    categoryProperty.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
                    Date currentDate = Calendar.getInstance().getTime();
                    categoryProperty.setCreateTime(currentDate);
                    categoryPropertyService.insert(categoryProperty);
                }
            }
        }
    }

    /**
     * 拆分集合
     *
     * @param <T>
     * @param resList 要拆分的集合
     * @param count   每个集合的元素个数
     * @return 返回拆分后的各个集合
     */
    public static <T> List<List<T>> split(List<T> resList, int count) {

        if (resList == null || count < 1)
            return null;
        List<List<T>> ret = new ArrayList<List<T>>();
        int size = resList.size();
        if (size <= count) { //数据量不足count指定的大小
            ret.add(resList);
        } else {
            int pre = size / count;
            int last = size % count;
            //前面pre个集合，每个大小都是count个元素
            for (int i = 0; i < pre; i++) {
                List<T> itemList = new ArrayList<T>();
                for (int j = 0; j < count; j++) {
                    itemList.add(resList.get(i * count + j));
                }
                ret.add(itemList);
            }
            //last的进行处理
            if (last > 0) {
                List<T> itemList = new ArrayList<T>();
                for (int i = 0; i < last; i++) {
                    itemList.add(resList.get(pre * count + i));
                }
                ret.add(itemList);
            }
        }
        return ret;

    }

}
