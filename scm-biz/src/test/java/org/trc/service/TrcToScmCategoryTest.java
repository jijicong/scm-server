package org.trc.service;

import com.alibaba.fastjson.JSON;
import com.qiniu.util.Json;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.trc.constants.SupplyConstants;
import org.trc.domain.category.*;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.supplier.Supplier;
import org.trc.domain.trcDomain.*;
import org.trc.enums.*;
import org.trc.exception.CategoryException;
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
import org.trc.util.ParamsUtil;
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
//                brandScm.setId(Long.valueOf(brandTrc.getBrandId()));
                brandScm.setAlise(brandTrc.getAlias());
                brandScm.setLogo(brandTrc.getLogo());
                //brandScm.setTrcId(brandTrc.getBrandId());
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
//                brandService.insert(brandScm);
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
            for (Properties properties : propertiesList) {
                Property property = new Property();
                property.setDescription(properties.getDescription());
                property.setLastEditOperator("admin");
                //property.setTrcId(properties.getPropertyId());
                property.setName(properties.getName());
                if (properties.getType().equals("Natural")) {
                    property.setTypeCode("natureProperty");
                } else {
                    property.setTypeCode("purchaseProperty");
                }
                if (properties.getShowType().equals("Text")) {
                    property.setValueType(ZeroToNineEnum.ZERO.getCode());
                } else {
                    property.setValueType(ZeroToNineEnum.ONE.getCode());
                }
                property.setIsValid(ZeroToNineEnum.ONE.getCode());
                property.setSort(properties.getSortOrder());
                property.setCreateOperator("admin");
                property.setLastEditOperator("admin");
                property.setCreateTime(Calendar.getInstance().getTime());
                property.setUpdateTime(property.getCreateTime());
                property.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
                int trcId = properties.getPropertyId();
                propertyService.insert(property);
                insertPropertyValue(property, trcId);
//                propertyList.add(property);
            }
//            propertyService.insertList(propertyList);
        }
    }

    private void insertPropertyValue(Property property, int trcId) {
        Example example = new Example(PropertyValues.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("propertyId", trcId);
        List<PropertyValues> propertiesValueList = trcPropertiesValuesService.selectByExample(example);
        List<PropertyValue> propertyValueList = new ArrayList<>();
        for (PropertyValues propertyValue : propertiesValueList) {
            PropertyValue propertyValueScm = new PropertyValue();
//            propertyValueScm.setId(Long.valueOf(propertyValue.getPropertyValueId()));
            propertyValueScm.setPropertyId(Long.valueOf(property.getId()));
            //propertyValueScm.setTrcId(propertyValue.getPropertyValueId());
            propertyValueScm.setPicture(propertyValue.getImage());
            propertyValueScm.setValue(propertyValue.getText());
            propertyValueScm.setCreateTime(Calendar.getInstance().getTime());
            propertyValueScm.setUpdateTime(propertyValueScm.getCreateTime());
            propertyValueScm.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
            propertyValueScm.setIsValid(ZeroToNineEnum.ONE.getCode());
            propertyValueScm.setCreateOperator("admin");
            propertyValueScm.setSort(propertyValue.getSortOrder());

            propertyValueList.add(propertyValueScm);
        }
        propertyValueService.insertList(propertyValueList);
    }

    @Test
    public void propertyValueTest() {
       /* Example example = new Example(PropertyValues.class);
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
//                propertyValueService.insert(propertyValueScm);

            }
            propertyValueService.insertList(propertyValueList);
        }*/
    }

    @Test
    public void categoryTest() throws Exception {
        Example example = new Example(Categories.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("disabled", "0");
        criteria.andEqualTo("level", 3);
        List<Categories> categoriesList = trcCategoriesService.selectByExample(example);
        if (!AssertUtil.collectionIsEmpty(categoriesList)) {
            for (Categories categories : categoriesList) {
                Category category = new Category();
                category.setName(categories.getName());
                category.setLevel(categories.getLevel());
                if (category.getLevel()!=1){
                    Example example2 = new Example(Category.class);
                    Example.Criteria criteria2 = example2.createCriteria();
                    criteria2.andEqualTo("trcId", categories.getParentId());
                    List<Category> categoryList = categoryService.selectByExample(example2);
                    if (!AssertUtil.collectionIsEmpty(categoryList)){
                        Category categoryParent = categoryList.get(0);
                        category.setParentId(categoryParent.getId());
                    }
                }
                category.setSort(categories.getSortOrder());
                //category.setTrcId(categories.getCategoryId());
                category.setClassifyDescribe(categories.getDescription());
                int disabled = categories.getDisabled();
                if (disabled == Integer.parseInt(ZeroToNineEnum.ZERO.getCode()))//正常
                    category.setIsValid(ValidEnum.VALID.getCode());
                else
                    category.setIsValid(ValidEnum.NOVALID.getCode());
                category.setSource(CHANNEL_TRC);
                category.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
                Date currentDate = Calendar.getInstance().getTime();
                category.setCreateTime(currentDate);
                category.setUpdateTime(currentDate);
                category.setCategoryCode(serialUtilService.generateCode(FL_LENGTH, FL_SERIALNAME));
                category.setCreateOperator("admin");
                category.setIsLeaf(ZeroToNineEnum.ONE.getCode());
                saveCategory(category);
            }
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
        for (List<CategoryBrandRels> categoryBrandRels1 : categoryBrandList) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    try {
                        if (!AssertUtil.collectionIsEmpty(categoryBrandRels1)) {
                            List<CategoryBrand> categoryBrands = new ArrayList<>();
                            for (CategoryBrandRels categoryBrandRels2 : categoryBrandRels1) {
                                Category category = null;
                                Example example2 = new Example(Category.class);
                                Example.Criteria criteria2 = example2.createCriteria();
                                criteria2.andEqualTo("trcId", categoryBrandRels2.getCategoryId());
                                List<Category> categoryList = categoryService.selectByExample(example2);
                                if (!AssertUtil.collectionIsEmpty(categoryList)){
                                    category = categoryList.get(0);
                                }

                                Brand brand = null;
                                Example example3 = new Example(Brand.class);
                                Example.Criteria criteria3 = example3.createCriteria();
                                criteria3.andEqualTo("trcId", categoryBrandRels2.getCategoryId());
                                List<Brand> brandList = brandService.selectByExample(example3);
                                if (!AssertUtil.collectionIsEmpty(brandList)){
                                    brand = brandList.get(0);
                                }

                                if (null != category && null != brand && category.getLevel() == 3) {
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
                                    if (category.getLevel() == 3) {
                                        categoryBrands.add(categoryBrand);
                                    }
                                }
                            }
                            if (categoryBrands.size() > 0)
                                categoryBrandService.insertList(categoryBrands);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
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
        CategoryPropertyRels categoryPropertyRels = new CategoryPropertyRels();
        List<CategoryPropertyRels> categoryPropertyRelsList = trcCategoriesPropertiesService.select(categoryPropertyRels);
        if (!AssertUtil.collectionIsEmpty(categoryPropertyRelsList)) {
            for (CategoryPropertyRels categoryPropertyRel : categoryPropertyRelsList) {
                Category category = null;
                Example example2 = new Example(Category.class);
                Example.Criteria criteria2 = example2.createCriteria();
                criteria2.andEqualTo("trcId", categoryPropertyRel.getCategoryId());
                List<Category> categoryList = categoryService.selectByExample(example2);
                if (!AssertUtil.collectionIsEmpty(categoryList)){
                    category = categoryList.get(0);
                }

                Property property = null;
                Example example3 = new Example(Property.class);
                Example.Criteria criteria3 = example3.createCriteria();
                criteria3.andEqualTo("trcId", categoryPropertyRel.getCategoryId());
                List<Property> propertyList = propertyService.selectByExample(example3);
                if (!AssertUtil.collectionIsEmpty(propertyList)){
                     property = propertyList.get(0);
                }


//                Property property = propertyService.selectByPrimaryKey(Long.valueOf(categoryPropertyRels.getPropertyId()));
                if (null != category && null != property && category.getLevel() == 3) {
                    CategoryProperty categoryProperty = new CategoryProperty();
                    categoryProperty.setCategoryId(category.getId());
                    categoryProperty.setPropertyId(property.getId());
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

    public void saveCategory(Category category) throws Exception {
        /*category.setCategoryCode(serialUtilService.generateCode(FL_LENGTH, FL_SERIALNAME));
        category.setSource(SourceEnum.SCM.getCode());
        category.setIsLeaf(ZeroToNineEnum.ONE.getCode());
        category.setCreateTime(Calendar.getInstance().getTime());
        category.setUpdateTime(Calendar.getInstance().getTime());*/

        int count = categoryService.insert(category);
        if (count == 0) {
            String msg = "增加分类" + JSON.toJSONString(category) + "数据库操作失败";
            throw new CategoryException(ExceptionEnum.CATEGORY_CATEGORY_SAVE_EXCEPTION, msg);
        }
        //判断新增的分类等级
        if (category.getLevel() == 1) {
            category.setFullPathId(null);
        } else if (category.getLevel() == 2) {
            category.setFullPathId(category.getParentId().toString());
        } else {
            category.setFullPathId(queryPathId(category.getParentId()) + SupplyConstants.Symbol.FULL_PATH_SPLIT + category.getParentId());
        }
        updateCategory(category, true);
        //判断叶子节点,并更新
        if (category.getLevel() != 1 && isLeaf(category.getParentId()) != 0) {
            updateIsLeaf(category);
        }
    }

    public int isLeaf(Long id) throws Exception {
        Example example = new Example(Category.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("parentId", id);
        return categoryService.selectByExample(example).size();
    }
    public Long queryPathId(Long parentId) throws Exception {
        AssertUtil.notNull(parentId, "根据parentId查询父级分类,参数parentId为空");
        Category category = new Category();
        category.setId(parentId);
        return categoryService.selectOne(category).getParentId();
    }
    public void updateCategory(Category category, boolean isSave) throws Exception {
        AssertUtil.notNull(category.getId(), "修改分类参数ID为空");
        Category oldCategory = new Category();
        oldCategory.setId(category.getId());
        //判断是否叶子节点
        if (isLeaf(category.getId()) == 0) {
            category.setIsLeaf(ZeroToNineEnum.ONE.getCode());
        } else {
            category.setIsLeaf(ZeroToNineEnum.ZERO.getCode());
        }
        category.setUpdateTime(Calendar.getInstance().getTime());
        int count = categoryService.updateByPrimaryKeySelective(category);
        if (count == 0) {
            String msg = "修改分类" + JSON.toJSONString(category) + "数据库操作失败";
            throw new CategoryException(ExceptionEnum.CATEGORY_CATEGORY_UPDATE_EXCEPTION, msg);
        }
    }

    public void updateIsLeaf(Category category) throws Exception {
        AssertUtil.notNull(category.getParentId(), "根据分类ID查询分类父节点的参数id为空");
        Category categoryParent = new Category();
        categoryParent.setId(category.getParentId());
        categoryParent.setIsLeaf(ZeroToNineEnum.ZERO.getCode());
        int count = categoryService.updateByPrimaryKeySelective(categoryParent);
        if (count == 0) {
            String msg = "更新分类是否叶子节点" + JSON.toJSONString(category) + "操作失败";
            throw new CategoryException(ExceptionEnum.CATEGORY_CATEGORY_UPDATE_EXCEPTION, msg);
        }
    }
}
