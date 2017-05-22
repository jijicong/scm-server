package org.trc.biz.category;

import org.trc.domain.category.Category;
import org.trc.domain.category.CategoryBrand;
import org.trc.domain.category.CategoryBrandExt;
import org.trc.form.category.CategoryBrandForm;
import org.trc.form.category.TreeNode;

import java.util.List;

/**
 * Created by hzszy on 2017/5/5.
 */
public interface ICategoryBiz {
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
    int checkCategoryCode(Long id,String categoryCategory) throws Exception;
    /**
     * 是否为叶子节点
     */
    int isLeaf(Long id) throws  Exception;

    /**
     *更新排序
     */
    void updateSort(List<Category> categoryList) throws  Exception;

    void updateState(Category category) throws Exception;

    void updateIsLeaf(Category category) throws  Exception;

    /**
     * 查询分类品牌列表
     * @param categoryBrandForm
     * @return
     * @throws Exception
     */
    List<CategoryBrandExt> queryCategoryBrands(CategoryBrandForm categoryBrandForm) throws  Exception;

    /**
     * 根据ID查询
     */
    Long queryPathId(Long id) throws  Exception;


}
