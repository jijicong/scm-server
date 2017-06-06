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
import org.trc.enums.ExceptionEnum;
import org.trc.enums.ValidEnum;
import org.trc.enums.ZeroToNineEnum;
import org.trc.exception.CategoryException;
import org.trc.form.category.CategoryBrandForm;
import org.trc.form.category.CategoryForm;
import org.trc.form.category.TableDate;
import org.trc.form.category.TreeNode;
import org.trc.service.category.*;
import org.trc.util.*;
import tk.mybatis.mapper.entity.Example;


import java.util.*;

/**
 * Created by hzszy on 2017/5/5.
 */
@Service("categoryBiz")
public class CategoryBiz implements ICategoryBiz {

    private Logger  log = LoggerFactory.getLogger(CategoryBiz.class);

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
        category.setUpdateTime(Calendar.getInstance().getTime());
        int count = categoryService.updateByPrimaryKeySelective(category);
        if (count == 0) {
            String msg = CommonUtil.joinStr("修改分类", JSON.toJSONString(category), "数据库操作失败").toString();
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

        ParamsUtil.setBaseDO(category);
        int count = categoryService.insert(category);
        if (count == 0) {
            String msg = CommonUtil.joinStr("增加分类", JSON.toJSONString(category), "数据库操作失败").toString();
            log.error(msg);
            throw new CategoryException(ExceptionEnum.CATEGORY_CATEGORY_UPDATE_EXCEPTION, msg);
        }
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
        categoryService.updateByPrimaryKeySelective(categoryParent);
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
    public Long queryPathId(Long id) throws Exception {
        Category category = new Category();
        category.setId(id);
        return categoryService.selectOne(category).getParentId();
    }

    /**
     * 更新排序
     *
     * @param categoryList
     * @throws Exception
     */
    @Override
    public void updateSort(List<Category> categoryList) throws Exception {
        categoryService.updateCategorySort(categoryList);
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
        if (category.getIsValid().equals(ValidEnum.VALID.getCode())) {
            updateCategory.setIsValid(ValidEnum.NOVALID.getCode());
        } else {
            updateCategory.setIsValid(ValidEnum.VALID.getCode());
        }
        updateCategory.setUpdateTime(Calendar.getInstance().getTime());
        int count = categoryService.updateByPrimaryKeySelective(updateCategory);
        if (count == 0) {
            String msg = CommonUtil.joinStr("修改分类", JSON.toJSONString(category), "数据库操作失败").toString();
            log.error(msg);
            throw new CategoryException(ExceptionEnum.CATEGORY_CATEGORY_UPDATE_EXCEPTION, msg);
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
    public List<CategoryBrand> queryBrands(Long categoryId) {
        CategoryBrand categoryBrand = new CategoryBrand();
        categoryBrand.setCategoryId(categoryId);
        return categoryBrandService.select(categoryBrand);
    }

    @Override
    public List<CategoryProperty> queryProperties(Long categoryId) {
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
        List<Long> brandIdsList = Arrays.asList(StringUtil.splitByComma(brandIds));
        if (brandIdsList.size() > 0 && brandIdsList != null) {
            String[] delIds = delRecord.split(SupplyConstants.Symbol.COMMA);
            if (delIds.length > 0 && !StringUtils.equals(delRecord, "")) {
                for (String id : delIds) {
                    categoryBrandService.deleteByPrimaryKey(Long.parseLong(id));
                }
            }
            List<Brand> brands = brandService.selectBrandList(brandIdsList);
            Category category = new Category();
            category.setId(categoryId);
            category = categoryService.selectOne(category);
            List<CategoryBrand> categoryBrands = new ArrayList<>();
            for (Brand brand : brands) {
                CategoryBrand categoryBrand = new CategoryBrand();
                categoryBrand.setBrandCode(brand.getBrandCode());
                categoryBrand.setBrandId(brand.getId());
                categoryBrand.setCategoryId(category.getId());
                categoryBrand.setCategoryCode(category.getCategoryCode());
                categoryBrand.setCreateTime(Calendar.getInstance().getTime());
                categoryBrands.add(categoryBrand);
            }
            if (categoryBrands.size() > 0 && categoryBrands != null) {
                categoryBrandService.insertList(categoryBrands);
            }
        }
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
        List<CategoryProperty> insertProperties = new ArrayList<>();
        List<CategoryProperty> sortProperties = new ArrayList<>();
        List<CategoryProperty> delProperties = new ArrayList<>();
        for (TableDate tableDate : tableDates) {
            if (ZeroToNineEnum.ONE.getCode().equals(tableDate.getStatus())) {
                CategoryProperty categoryProperty = new CategoryProperty();
                categoryProperty.setPropertySort(tableDate.getIndex());
                categoryProperty.setCategoryId(categoryId);
                categoryProperty.setCreateTime(Calendar.getInstance().getTime());
                categoryProperty.setPropertyId(tableDate.getId());
                insertProperties.add(categoryProperty);
            } else if (ZeroToNineEnum.THREE.getCode().equals(tableDate.getStatus())) {
                CategoryProperty categoryProperty = new CategoryProperty();
                categoryProperty.setId(tableDate.getId());
                categoryProperty.setPropertyId(tableDate.getId());
                categoryProperty.setPropertySort(tableDate.getIndex());
                delProperties.add(categoryProperty);
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
        } else if (delProperties.size() > 0 && delProperties != null) {
            for (CategoryProperty categoryProperty : delProperties) {
                categoryPropertyService.deleteByPrimaryKey(categoryProperty.getId());
            }
        }
        for (CategoryProperty categoryProperty : sortProperties) {
            categoryPropertyService.updateByPrimaryKey(categoryProperty);
        }

    }

    @Override
    public String getCategoryName(Long categoryId) throws Exception {
        List<String> categoryNames = queryCategoryNamePath(categoryId);
        AssertUtil.notEmpty(categoryNames, String.format("根据分类ID[%s]查询分类全路径名称为空", categoryId.toString()));
        String categoryName = "";
        for(String name : categoryNames){
            categoryName = name + CATEGORY_NAME_SPLIT_SYMBOL + categoryName;
        }
        if(categoryName.length() > 0){
            categoryName = categoryName.substring(0, categoryName.length()-1);
        }
        return categoryName;
    }


}
