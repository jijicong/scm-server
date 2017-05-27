package org.trc.biz.category;

import org.trc.domain.category.Category;
import org.trc.domain.category.CategoryBrandExt;
import org.trc.domain.category.CategoryProperty;
import org.trc.form.category.CategoryBrandForm;
import org.trc.form.category.CategoryForm;
import org.trc.form.category.TreeNode;
import org.trc.util.Pagenation;

import java.util.List;

/**
 * Created by hzszy on 2017/5/5.
 */
public interface ICategoryBiz {

    /**
     * 分类分页查询
     *
     * @param queryModel
     * @param page
     * @return
     * @throws Exception
     */
    Pagenation<Category> CategoryPage(CategoryForm queryModel, Pagenation<Category> page) throws Exception;

    /**
     * 获取分类树节点
     *
     * @return
     * @throws Exception
     */
    List<TreeNode> getNodes(Long parentId, boolean isRecursive) throws Exception;

    /**
     * 修改
     *
     * @param category
     * @return
     * @throws Exception
     */
    void updateCategory(Category category) throws Exception;

    /**
     * 保存
     */
    void saveClassify(Category category) throws Exception;


    /**
     * 查询分类编码是否存在
     *
     * @param categoryCategory
     * @return
     * @throws Exception
     */
    int checkCategoryCode(Long id, String categoryCategory) throws Exception;

    /**
     * 是否为叶子节点
     */
    int isLeaf(Long id) throws Exception;

    /**
     * 更新排序
     */
    void updateSort(List<Category> categoryList) throws Exception;

    void updateState(Category category) throws Exception;

    void updateIsLeaf(Category category) throws Exception;

    /**
     * 查询分类品牌列表
     *
     * @param categoryBrandForm
     * @return
     * @throws Exception
     */
    List<CategoryBrandExt> queryCategoryBrands(CategoryBrandForm categoryBrandForm) throws Exception;

    /**
     * 根据ID查询
     */
    Long queryPathId(Long id) throws Exception;

    List<String> queryCategoryNamePath(Long id) throws Exception;

    /**
     * 关联分类与品牌
     *
     * @param categoryId
     * @param brandIds
     * @throws Exception
     */
    void linkCategoryBrands(Long categoryId, String brandIds, String delRecord) throws Exception;


    /**
     * 查询已关联的分类属性
     *
     * @param id
     * @return
     * @throws Exception
     */
    List<CategoryProperty> queryCategoryProperty(Long id) throws Exception;

    /**
     * 关联分类品牌
     */
    void linkCategoryProperty(Long categoryId, Long propertyId) throws Exception;

    /**
     * 更新分类属性
     */
    void updateCategoryProperty(Long categoryId, String jsonDate) throws Exception;
}
