package org.trc.biz.impl.category;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.trc.biz.category.ICategoryBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.category.*;
import org.trc.enums.*;
import org.trc.exception.CategoryException;
import org.trc.form.category.CategoryBrandForm;
import org.trc.form.category.CategoryForm;
import org.trc.form.category.TableDate;
import org.trc.form.category.TreeNode;
import org.trc.service.category.*;
import org.trc.service.util.ISerialUtilService;
import org.trc.util.*;
import tk.mybatis.mapper.entity.Example;


import javax.annotation.Resource;
import java.util.*;

/**
 * Created by hzszy on 2017/5/5.
 */
@Service("categoryBiz")
public class CategoryBiz implements ICategoryBiz {

    private Logger log = LoggerFactory.getLogger(CategoryBiz.class);

    //分类名称全路径分割符号
    public static final String CATEGORY_NAME_SPLIT_SYMBOL = "/";

    @Autowired
    private ICategoryService categoryService;

    @Autowired
    private ICategoryBrandService categoryBrandService;

    @Autowired
    private IBrandService brandService;

    @Autowired
    private IPropertyService propertyService;

    @Autowired
    private ICategoryPropertyService categoryPropertyService;

    @Autowired
    private ISerialUtilService serialUtilService;

    private final static String SERIALNAME = "FL";

    private final static Integer LENGTH = 3;

    @Override
    public Pagenation<Category> categoryPage(CategoryForm queryModel, Pagenation<Category> page) throws Exception {
        Example example = new Example(Property.class);
        Example.Criteria criteria = example.createCriteria();
        if (!StringUtils.isBlank(queryModel.getName())) {
            criteria.andLike("name", "%" + queryModel.getName() + "%");
        }
        if (!StringUtils.isBlank(queryModel.getSort())) {
            criteria.andEqualTo("sort", queryModel.getSort());
        }
        if (!StringUtils.isBlank(queryModel.getLevel())) {
            criteria.andEqualTo("level", queryModel.getLevel());
        }
        if (!StringUtils.isBlank(queryModel.getIsValid())) {
            criteria.andEqualTo("isValid", queryModel.getIsValid());
        }
        example.orderBy("isValid").desc();
        example.orderBy("updateTime").desc();
        return categoryService.pagination(example, page, queryModel);
    }

    /**
     * 根据父类id，查找子分类，isRecursive为true，递归查找所有，否则只查一级子分类
     * null, false
     * null, true
     * xx
     *
     * @param parentId
     * @param isRecursive
     * @return
     */
    @Override
    public List<TreeNode> getNodes(Long parentId, boolean isRecursive) throws Exception {
        Example example = new Example(Category.class);
        Example.Criteria criteria = example.createCriteria();
        if (null == parentId) {
            criteria.andIsNull("parentId");
        } else {
            criteria.andEqualTo("parentId", parentId);
        }
        example.orderBy("sort").asc();
        List<Category> childCategoryList = categoryService.selectByExample(example);
        List<TreeNode> childNodeList = new ArrayList<>();
        for (Category category : childCategoryList) {
            TreeNode treeNode = new TreeNode();
            treeNode.setId(category.getId().toString());
            treeNode.setName(category.getName());
            treeNode.setSort(category.getSort());
            treeNode.setIsValid(category.getIsValid());
            treeNode.setLevel(category.getLevel());
            treeNode.setFullPathId(category.getFullPathId());
            treeNode.setSource(category.getSource());
            treeNode.setCategoryCode(category.getCategoryCode());
            treeNode.setIsLeaf(category.getIsLeaf());
            childNodeList.add(treeNode);
        }
        if (childNodeList.size() == 0) {
            return childNodeList;
        }
        if (isRecursive == true) {
            for (TreeNode childNode : childNodeList) {
                List<TreeNode> nextChildCategoryList = getNodes(Long.parseLong(childNode.getId()), isRecursive);
                if (nextChildCategoryList.size() > 0) {
                    childNode.setChildren(nextChildCategoryList);
                }
            }
        }
        return childNodeList;
    }

    /**
     * 修改分类
     *
     * @param category
     * @throws Exception
     */
    @Override
    public void updateCategory(Category category) throws Exception {
        AssertUtil.notNull(category.getId(), "修改分类参数ID为空");
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
            log.error(msg);
            throw new CategoryException(ExceptionEnum.CATEGORY_CATEGORY_UPDATE_EXCEPTION, msg);
        }
    }

    /**
     * 添加分类
     *
     * @param category
     * @throws Exception
     */
    @Override
    public void saveCategory(Category category) throws Exception {
        checkSaveCategory(category);
        category.setCategoryCode(serialUtilService.generateCode(LENGTH, SERIALNAME));
        AssertUtil.notNull(category.getCategoryCode(),"分类编码生成失败");
        category.setCreateOperator("test");
        category.setSource(SourceEnum.TRC.getCode());
        category.setIsLeaf(ZeroToNineEnum.ONE.getCode());
        category.setCreateTime(Calendar.getInstance().getTime());
        category.setUpdateTime(Calendar.getInstance().getTime());
        //先保存
        ParamsUtil.setBaseDO(category);
        int count = categoryService.insert(category);
        if (count == 0) {
            String msg = "增加分类" + JSON.toJSONString(category) + "数据库操作失败";
            log.error(msg);
            throw new CategoryException(ExceptionEnum.CATEGORY_CATEGORY_SAVE_EXCEPTION, msg);
        } else {
            if (StringUtils.equals(category.getIsValid(), ValidEnum.VALID.getCode()) && category.getParentId() != null) {
                updateLastCategory(category.getParentId());
            }
        }
        //判断新增的分类等级
        if (category.getLevel() == 1) {
            category.setFullPathId(null);
        } else if (category.getLevel() == 2) {
            category.setFullPathId(category.getParentId().toString());
        } else {
            category.setFullPathId(queryPathId(category.getParentId()) + SupplyConstants.Symbol.FULL_PATH_SPLIT + category.getParentId());
        }
        updateCategory(category);
        //判断叶子节点,并更新
        if (category.getLevel() != 1 && isLeaf(category.getParentId()) != 0) {
            updateIsLeaf(category);
        }

    }

    private void checkSaveCategory(Category category) {
        AssertUtil.notNull(category.getSort(), "新增分类参数sort不能为空");
        AssertUtil.notBlank(category.getIsValid(), "新增分类参数isValid不能为空");

    }

    /**
     * 查询分类编码是否存在
     *
     * @param categoryCode
     * @return
     * @throws Exception
     */
    @Override
    public int checkCategoryCode(Long id, String categoryCode) throws Exception {

        Example example = new Example(Category.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andNotEqualTo("id", id);
        criteria.andEqualTo("categoryCode", categoryCode);
        return categoryService.selectByExample(example).size();

    }

    /***
     * 判断是否叶子节点
     * @param id
     * @return
     * @throws Exception
     */
    @Override
    public int isLeaf(Long id) throws Exception {

        AssertUtil.notNull(id, "根据分类ID查询分类的参数id为空");
        Example example = new Example(Category.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("parentId", id);
        return categoryService.selectByExample(example).size();//需要用select（*） count
    }

    /**
     * 更新是否叶子节点
     */
    @Override
    public void updateIsLeaf(Category category) throws Exception {
        AssertUtil.notNull(category.getParentId(), "根据分类ID查询分类父节点的参数id为空");
        Category categoryParent = new Category();
        categoryParent.setId(category.getParentId());
        categoryParent.setIsLeaf(ZeroToNineEnum.ZERO.getCode());
        int count = categoryService.updateByPrimaryKeySelective(categoryParent);
        if (count == 0) {
            String msg = "更新分类是否叶子节点" + JSON.toJSONString(category) + "操作失败";
            log.error(msg);
            throw new CategoryException(ExceptionEnum.CATEGORY_CATEGORY_UPDATE_EXCEPTION, msg);
        }
    }

    @Override
    public List<CategoryBrandExt> queryCategoryBrands(CategoryBrandForm categoryBrandForm) throws Exception {
        AssertUtil.notBlank(categoryBrandForm.getCategoryId(), "查询分类相关品牌分类ID不能为空");
        String[] categoryIds = categoryBrandForm.getCategoryId().split(SupplyConstants.Symbol.COMMA);
        List<Long> categoryList = new ArrayList<Long>();
        for (String categoryId : categoryIds) {
            categoryList.add(Long.parseLong(categoryId));
        }
        return categoryBrandService.queryCategoryBrands(categoryList);
    }

    @Override
    public Long queryPathId(Long parentId) throws Exception {
        AssertUtil.notNull(parentId, "根据parentId查询父级分类,参数parentId为空");
        Category category = new Category();
        category.setId(parentId);
        return categoryService.selectOne(category).getParentId();
    }

    /**
     * 更新排序
     *
     * @param sortDate
     * @throws Exception
     */
    @Override
    public void updateSort(String sortDate) throws Exception {
        AssertUtil.notBlank(sortDate, "排序信息为空");
        List<Category> categoryList = JSON.parseArray(sortDate, Category.class);
        for (int i = 0; i < categoryList.size(); i++) {
            Category category = new Category();
            category.setId(categoryList.get(i).getId());
            category = categoryService.selectOne(category);
            AssertUtil.notNull(category, "查找需要更新排序的分类失败");
            if (category.getSort() == categoryList.get(i).getSort()) {
                categoryList.remove(i);
            }
        }
        int count = categoryService.updateCategorySort(categoryList);
        if (count == 0) {
            String msg = "修改分类排序操作失败";
            log.error(msg);
            throw new CategoryException(ExceptionEnum.CATEGORY_CATEGORY_UPDATE_EXCEPTION, msg);
        }

    }

    /**
     * 分类状态修改
     *
     * @param category
     * @throws Exception
     */
    @Override
    public void updateState(Category category) throws Exception {
        AssertUtil.notNull(category.getId(), "类目管理模块修改分类信息失败，分类信息为空");
        Category updateCategory = new Category();
        updateCategory.setId(category.getId());
        Category category1 = categoryService.selectOne(updateCategory);
        if (StringUtils.equals(category.getIsValid(), ValidEnum.VALID.getCode())) {
            updateCategory.setIsValid(ValidEnum.NOVALID.getCode());
        } else {
            updateCategory.setIsValid(ValidEnum.VALID.getCode());
        }
        if (category1.getParentId() != null && StringUtils.equals(category1.getIsValid(), ValidEnum.NOVALID.getCode())) {
            updateLastCategory(category1.getParentId());
        }
        updateCategory.setUpdateTime(Calendar.getInstance().getTime());
        int count = categoryService.updateByPrimaryKeySelective(updateCategory);
        if (count == 0) {
            String msg = "修改分类状态" + JSON.toJSONString(category) + "操作失败";
            log.error(msg);
            throw new CategoryException(ExceptionEnum.CATEGORY_CATEGORY_UPDATE_EXCEPTION, msg);
        }

    }

    /**
     * 要启动的分类如果上级分类处于停用状态时,启用分类
     *
     * @param parentId
     * @throws Exception
     */
    private void updateLastCategory(Long parentId) throws Exception {

        Category category = new Category();
        category.setId(parentId);
        category = categoryService.selectOne(category);
        if (StringUtils.equals(category.getIsValid(), ValidEnum.NOVALID.getCode())) {
            category.setIsValid(ValidEnum.VALID.getCode());
            category.setUpdateTime(Calendar.getInstance().getTime());
            int count = categoryService.updateByPrimaryKeySelective(category);
            if (count == 0) {
                String msg = "修改分类状态" + JSON.toJSONString(category) + "操作失败";
                log.error(msg);
                throw new CategoryException(ExceptionEnum.CATEGORY_CATEGORY_UPDATE_EXCEPTION, msg);
            }
        }
        if (category.getParentId() != null) {
            updateLastCategory(category.getParentId());
        }
    }

    /**
     * 根据ID查询三级分类的全路径名称，用于品牌，属性的接入
     */
    @Override
    public List<String> queryCategoryNamePath(Long id) throws Exception {
        List<String> categoryName = new ArrayList<>();
        AssertUtil.notNull(id, "根据ID查询分类参数为空");

        Category categoryLevel3 = new Category();
        categoryLevel3.setId(id);
        categoryLevel3 = categoryService.selectOne(categoryLevel3);
        categoryName.add(categoryLevel3.getName());

        Category categoryLevel2 = new Category();
        categoryLevel2.setId(categoryLevel3.getParentId());
        categoryLevel2 = categoryService.selectOne(categoryLevel2);
        categoryName.add(categoryLevel2.getName());

        Category categoryLevel1 = new Category();
        categoryLevel1.setId(categoryLevel2.getParentId());
        categoryLevel1 = categoryService.selectOne(categoryLevel1);
        categoryName.add(categoryLevel1.getName());
        return categoryName;
    }

    @Override
    public List<CategoryBrand> queryBrands(Long categoryId) throws Exception {
        AssertUtil.notNull(categoryId, "根据分类Id查询关联品牌,参数categoryId为空");
        CategoryBrand categoryBrand = new CategoryBrand();
        categoryBrand.setCategoryId(categoryId);
        return categoryBrandService.select(categoryBrand);
    }

    @Override
    public List<CategoryProperty> queryProperties(Long categoryId) {
        AssertUtil.notNull(categoryId, "根据分类Id查询关联属性,参数categoryId为空");
        CategoryProperty categoryProperty = new CategoryProperty();
        categoryProperty.setCategoryId(categoryId);
        return categoryPropertyService.select(categoryProperty);
    }

    /**
     * 数据关联
     *
     * @param categoryId
     * @param brandIds
     * @throws Exception
     */
    @Override
    public void linkCategoryBrands(Long categoryId, String brandIds, String delRecord) throws Exception {
//        AssertUtil.notBlank(brandIds, "分类关联品牌brandIdID为空");
        AssertUtil.notNull(categoryId, "分类关联品牌categoryId为空");
        //删除的BrandId

        List<Long> delBrandIdsList = new ArrayList<>();
        List<CategoryBrand> delCategoryBrands = new ArrayList<>();
        if (StringUtils.isNotBlank(delRecord)) {
            delBrandIdsList.addAll(filterId(delRecord));
            delCategoryBrands = assembleList(delBrandIdsList, categoryId);
        }
        //保存的BrandId
        List<Long> brandIdsList = new ArrayList<>();
        if (StringUtils.isNotBlank(brandIds)) {
            brandIdsList.addAll(filterId(brandIds));
        }
        List<CategoryBrand> saveCategoryBrands = assembleList(brandIdsList, categoryId);
        //新增的数据
        List<CategoryBrand> newCategoryBrands = new ArrayList<>();
        Example example = new Example(CategoryBrand.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("categoryId", categoryId);
        //查询该categoryId已经关联的数据
        List<CategoryBrand> oldCategoryBrands = categoryBrandService.selectByExample(example);
        if (oldCategoryBrands.size() > 0) {
            for (CategoryBrand categoryBrand : saveCategoryBrands) {
                boolean flag = false;
                for (CategoryBrand categoryB : oldCategoryBrands) {
                    if (categoryBrand.getBrandId() == categoryB.getBrandId()) {
                        flag = true;
                    }
                }
                if (!flag) {
                    newCategoryBrands.add(categoryBrand);
                }
            }
        } else {
            newCategoryBrands = saveCategoryBrands;
        }
        if (newCategoryBrands.size() > 0 && newCategoryBrands != null) {
            categoryBrandService.insertList(newCategoryBrands);
        }
        //删除的数据
        if (delBrandIdsList.size() > 0 && delBrandIdsList != null) {

            categoryBrandService.deleteCategoryBrand(delCategoryBrands);
        }
    }

    private List<CategoryBrand> assembleList(List<Long> brandIdsList, Long categoryId) throws Exception {
        List<Brand> brands = brandService.selectBrandList(brandIdsList);
        Category category = new Category();
        category.setId(categoryId);
        category = categoryService.selectOne(category);
        AssertUtil.notNull(category, "根据Id未查询到分类!");
        List<CategoryBrand> categoryBrands = new ArrayList<>();
        for (Brand brand : brands) {
            CategoryBrand categoryBrand = new CategoryBrand();
            categoryBrand.setBrandCode(brand.getBrandCode());
            categoryBrand.setBrandId(brand.getId());
            categoryBrand.setCategoryId(category.getId());
            categoryBrand.setIsValid(brand.getIsValid());
            categoryBrand.setIsDeleted(brand.getIsDeleted());
            categoryBrand.setCategoryCode(category.getCategoryCode());
            categoryBrand.setCreateTime(Calendar.getInstance().getTime());
            categoryBrands.add(categoryBrand);
        }
        return categoryBrands;
    }

    /**
     * 查询已经关联的属性
     */
    @Override
    public List<CategoryProperty> queryCategoryProperty(Long categoryId) throws Exception {
        AssertUtil.notNull(categoryId, "查询分类关联属性categoryId为空");
        Example example = new Example(CategoryProperty.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("categoryId", categoryId);
        example.orderBy("propertySort").asc();
        List<CategoryProperty> categoryProperties = categoryPropertyService.selectByExample(example);
        List<Long> propertyIds = new ArrayList<>();
        for (CategoryProperty categoryProperty : categoryProperties) {
            propertyIds.add(categoryProperty.getPropertyId());
        }
        List<Property> propertyList = new ArrayList<Property>();
        if (propertyIds.size() > 0) {
            propertyList = propertyService.queryPropertyList(propertyIds);
        }
        if (propertyList.size() > 0) {
            for (CategoryProperty c : categoryProperties) {
                for (Property p : propertyList) {
                    if (StringUtils.equals(c.getPropertyId().toString(), p.getId().toString())) {
                        c.setName(p.getName());
                        c.setTypeCode(p.getTypeCode());
                        c.setValueType(p.getValueType());
                    }
                }
            }
        }
        return categoryProperties;
    }

    /**
     * 数据关联
     *
     * @param categoryId
     * @param propertyId
     * @throws Exception
     */
    @Override
    public void linkCategoryProperty(Long categoryId, Long propertyId) throws Exception {
        AssertUtil.notNull(propertyId, "分类关联属性propertyId为空");
        AssertUtil.notNull(categoryId, "分类关联品牌categoryId为空");
        Category category = new Category();
        category.setId(categoryId);
        category = categoryService.selectOne(category);
        Property property = new Property();
        property.setId(category.getId());
        property = propertyService.selectOne(property);
        CategoryProperty categoryProperty = new CategoryProperty();
        categoryProperty.setCategoryId(category.getId());
        categoryProperty.setPropertyId(propertyId);
        categoryProperty.setPropertySort(property.getSort());
        categoryPropertyService.insert(categoryProperty);
    }

    @Override
    public void updateCategoryProperty(Long categoryId, String jsonDate) throws Exception {
        AssertUtil.notNull(categoryId, "分类关联品牌categoryId为空");
        AssertUtil.notBlank(jsonDate, "分类关联属性表格数据为空");
        List<TableDate> tableDates = JSONArray.parseArray(jsonDate, TableDate.class);
        //需要新增的的数据
        List<CategoryProperty> insertProperties = new ArrayList<>();
        //需要排序的数据
        List<CategoryProperty> sortProperties = new ArrayList<>();
        //需要删除的的数据
        List<CategoryProperty> delProperties = new ArrayList<>();
        //未改动的的数据
        List<CategoryProperty> defaultProperties = new ArrayList<>();
        //将数据分组
        for (TableDate tableDate : tableDates) {
            if (RecordStatusEnum.ADD.getCode().equals(tableDate.getStatus())) {
                CategoryProperty categoryProperty = new CategoryProperty();
                categoryProperty.setPropertySort(tableDate.getIndex());
                categoryProperty.setCategoryId(categoryId);
                categoryProperty.setCreateTime(Calendar.getInstance().getTime());
                categoryProperty.setPropertyId(tableDate.getId());
                insertProperties.add(categoryProperty);
            }
            if (RecordStatusEnum.DELETE.getCode().equals(tableDate.getStatus())) {
                CategoryProperty categoryProperty = new CategoryProperty();
                categoryProperty.setId(tableDate.getId());
                categoryProperty.setPropertyId(tableDate.getPropertyId());
                categoryProperty.setPropertySort(tableDate.getIndex());
                delProperties.add(categoryProperty);
            }
            if (RecordStatusEnum.DEFAULT.getCode().equals(tableDate.getStatus())) {
                CategoryProperty categoryProperty = new CategoryProperty();
                categoryProperty.setId(tableDate.getId());
                categoryProperty.setPropertyId(tableDate.getPropertyId());
                categoryProperty.setPropertySort(tableDate.getIndex());
                defaultProperties.add(categoryProperty);
            } else {
                CategoryProperty categoryProperty = new CategoryProperty();
                categoryProperty.setCategoryId(categoryId);
                categoryProperty.setId(tableDate.getId());
                categoryProperty.setPropertyId(tableDate.getPropertyId());
                categoryProperty.setPropertySort(tableDate.getIndex());
                sortProperties.add(categoryProperty);
            }
        }
        if (insertProperties.size() > 0 && insertProperties != null) {
            categoryPropertyService.insertList(insertProperties);
        }

        if (delProperties.size() > 0 && delProperties != null) {
            for (CategoryProperty categoryProperty : delProperties) {
                categoryPropertyService.deleteByPrimaryKey(categoryProperty.getId());
            }
        }
        for (CategoryProperty categoryProperty : sortProperties) {
            int count = categoryPropertyService.updateByPrimaryKey(categoryProperty);
            if (count == 0) {
                String msg = "修改分类属性" + JSON.toJSONString(categoryProperty) + "操作失败";
                log.error(msg);
                throw new CategoryException(ExceptionEnum.CATEGORY_CATEGORY_UPDATE_EXCEPTION, msg);
            }
        }

    }

    @Override
    public String getCategoryName(Long categoryId) throws Exception {
        List<String> categoryNames = queryCategoryNamePath(categoryId);
        AssertUtil.notEmpty(categoryNames, String.format("根据分类ID[%s]查询分类全路径名称为空", categoryId.toString()));
        String categoryName = "";
        for (String name : categoryNames) {
            categoryName = name + CATEGORY_NAME_SPLIT_SYMBOL + categoryName;
        }
        if (categoryName.length() > 0) {
            categoryName = categoryName.substring(0, categoryName.length() - 1);
        }
        return categoryName;
    }

    /**
     * 过滤重复Id
     */
    private Set filterId(String ids) throws Exception {
        Set<Long> set = new LinkedHashSet<Long>();
        Long[] idsArray = StringUtil.splitByComma(ids);
        for (Long longId : idsArray) {
            set.add(longId);
        }
        return set;
    }
}
