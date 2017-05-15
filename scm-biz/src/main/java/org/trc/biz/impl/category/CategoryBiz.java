package org.trc.biz.impl.category;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.trc.biz.category.ICategoryBiz;
import org.trc.domain.category.Category;
import org.trc.enums.ExceptionEnum;
import org.trc.enums.ValidEnum;
import org.trc.enums.ZeroToNineEnum;
import org.trc.exception.ConfigException;
import org.trc.form.category.TreeNode;
import org.trc.service.category.ICategoryService;
import org.trc.util.AssertUtil;
import org.trc.util.CommonUtil;
import org.trc.util.ParamsUtil;
import tk.mybatis.mapper.entity.Example;


import java.util.*;

/**
 * Created by hzszy on 2017/5/5.
 */
@Service("categoryBiz")
public class CategoryBiz implements ICategoryBiz {

    private final static Logger log = LoggerFactory.getLogger(CategoryBiz.class);

    @Autowired
    private ICategoryService categoryService;

    /**
     * 根据父类id，查找子分类，isRecursive为true，递归查找所有，否则只查一级子分类
     *  null, false
     *  null, true
     *  xx
     * @param parentId
     * @param isRecursive
     * @return
     */
    @Override
    public List<TreeNode> getNodes(Long parentId, boolean isRecursive) throws Exception{
        Example example = new Example(Category.class);
        Example.Criteria criteria = example.createCriteria();
        if(null == parentId) {
            criteria.andIsNull("parentId");
        }else{
            criteria.andEqualTo("parentId", parentId);
        }
        example.orderBy("sort").asc();
        List<Category> childCategoryList = categoryService.selectByExample(example);
        List<TreeNode> childNodeList = new ArrayList<>();
        for(Category category : childCategoryList){
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
        if(childNodeList.size()==0){
            return childNodeList;
        }
        if(isRecursive == true){
            for(TreeNode childNode : childNodeList){
                List<TreeNode> nextChildCategoryList = getNodes(Long.parseLong(childNode.getId()), isRecursive);
                if(nextChildCategoryList.size() > 0 ) {
                    childNode.setChildren(nextChildCategoryList);
                }
            }
        }
        return childNodeList;
    }

    /**
     * 修改分类
     * @param category
     * @throws Exception
     */
    @Override
    public void updateCategory(Category category) throws Exception {

        AssertUtil.notNull(category.getId(), "修改分类参数ID为空");
        int count = 0;
        category.setUpdateTime(Calendar.getInstance().getTime());
        count = categoryService.updateByPrimaryKeySelective(category);
        if (count == 0) {
            String msg = CommonUtil.joinStr("修改分类", JSON.toJSONString(category), "数据库操作失败").toString();
            log.error(msg);
            throw new ConfigException(ExceptionEnum.CONFIG_DICT_UPDATE_EXCEPTION, msg);
        }

    }

    /**
     * 添加分类
     * @param category
     * @throws Exception
     */
    @Override
    public void saveClassify(Category category) throws Exception {

        int count = 0;
//        AssertUtil.notNull(category.getId(),"根据分类ID修改分类参数ID为空");
        //add
        ParamsUtil.setBaseDO(category);
        count = categoryService.insert(category);

        if (count == 0) {
            String msg = CommonUtil.joinStr("保存分类", JSON.toJSONString(category), "到数据库失败").toString();
            log.error(msg);
            throw new ConfigException(ExceptionEnum.CONFIG_DICT_UPDATE_EXCEPTION, msg);
        }
    }

    /**
     * 查询树
     *
     * @return
     * @throws Exception
     */
    @Override
    public List<TreeNode> getTreeNode() throws Exception {
        /**
         * 1、查询根节点
         */
        Example example = new Example(Category.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIsNull("parentId");
        example.orderBy("sort").asc();
        List<Category> rootClassifyList = categoryService.selectByExample(example);
        List<TreeNode> rootTreeNodeList = new ArrayList<TreeNode>();
        List<Long> firstIds = new ArrayList<Long>();

        //查询节点数据库操作
        for (int i = 0; i < rootClassifyList.size(); i++) {

            TreeNode rootTreeNode1 = new TreeNode();
            rootTreeNode1.setId(rootClassifyList.get(i).getId().toString());
            rootTreeNode1.setName(rootClassifyList.get(i).getName());
            rootTreeNode1.setSort(rootClassifyList.get(i).getSort());
            rootTreeNode1.setIsValid(rootClassifyList.get(i).getIsValid());
            rootTreeNode1.setLevel(rootClassifyList.get(i).getLevel());
            rootTreeNode1.setFullPathId(rootClassifyList.get(i).getFullPathId());
            rootTreeNode1.setSource(rootClassifyList.get(i).getSource());
            rootTreeNode1.setCategoryCode(rootClassifyList.get(i).getCategoryCode());
            rootTreeNode1.setIsLeaf(rootClassifyList.get(i).getIsLeaf());
            rootTreeNodeList.add(rootTreeNode1);
            firstIds.add(rootClassifyList.get(i).getId());

        }

        Map<Long, List<TreeNode>> firstMap = handlerNextTreeNode(firstIds);

        /**
         * 2、查询第二级分类节点
         */
        //查询所有父节点是根节点ID的记录
        Example exampleLevel2 = new Example(Category.class);
        criteria = exampleLevel2.createCriteria();
        criteria.andEqualTo("level", ZeroToNineEnum.TWO.getCode().toString());
        exampleLevel2.orderBy("sort").asc();
        List<Category> secondClassifyList = categoryService.selectByExample(exampleLevel2);
        AssertUtil.notNull(secondClassifyList, "查询第二级分类节点为空");
        List<Long> secondIds = new ArrayList<Long>();
        for (Category cls : secondClassifyList) {

            secondIds.add(cls.getId());
        }
        /**
         * 3、查询和组装所有第二级节点相关的第三级节点数据
         */

        Map<Long, List<TreeNode>> map = handlerNextTreeNode(secondIds);

        for (Map.Entry<Long, List<TreeNode>> entry : firstMap.entrySet()) {

            List<TreeNode> secondList = entry.getValue();
            for (TreeNode treeNode : secondList) {
                treeNode.setChildren(map.get(Long.parseLong(treeNode.getId())));
            }

        }
        for (int i = 0; i < rootTreeNodeList.size(); i++) {

            rootTreeNodeList.get(i).setChildren(firstMap.get(firstIds.get(i)));

        }

        return rootTreeNodeList;

    }

    /**
     * 查询分类编码是否存在
     *
     * @param categoryCode
     * @return
     * @throws Exception
     */
    @Override
    public Category findCategoryByCategoryCode(String categoryCode) throws Exception {

        AssertUtil.notBlank(categoryCode,"根据分类编码查询分类的参数categoryCode为空");
        Category category = new Category();
        category.setCategoryCode(categoryCode);
        return categoryService.selectOne(category);

    }

    @Override
    public int isLeaf(Long id) throws Exception {

        AssertUtil.notNull(id,"根据分类ID查询分类的参数id为空");
        Example example = new Example(Category.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("parentId",id);
        return categoryService.selectByExample(example).size();
    }

    /**
     * 更新排序
     * @param categoryList
     * @throws Exception
     */
    @Override
    public void updateSort(List<Category> categoryList) throws Exception {
          categoryService.updateCategorySort(categoryList);
    }

    /**
     * 分类状态修改
     * @param category
     * @throws Exception
     */
    @Override
    public void updateState(Category category) throws Exception {
        AssertUtil.notNull(category.getId(),"类目管理模块修改分类信息失败，分类信息为空");
        Category updateCategory = new Category();
        updateCategory.setId(category.getId());
        if (category.getIsValid().equals(ValidEnum.VALID.getCode())) {
            updateCategory.setIsValid(ValidEnum.NOVALID.getCode());
        } else {
            updateCategory.setIsValid(ValidEnum.VALID.getCode());
        }
        updateCategory.setUpdateTime(Calendar.getInstance().getTime());
        int count=categoryService.updateByPrimaryKeySelective(updateCategory);
        if(count == 0){
            String msg = CommonUtil.joinStr("修改渠道",JSON.toJSONString(category),"数据库操作失败").toString();
            log.error(msg);
            throw new ConfigException(ExceptionEnum.SYSTEM_CHANNEL_UPDATE_EXCEPTION, msg);
        }

    }

    /**
     * 查询组装下一级ID
     *
     * @param parentIds 父节点ID
     * @return
     */
    private Map<Long, List<TreeNode>> handlerNextTreeNode(List<Long> parentIds) {

        //根据第上级节点ID列表批量查询下级节点
        Example example = new Example(Category.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("parentId", parentIds);
        example.orderBy("sort").asc();
        List<Category> nextCategoryList = categoryService.selectByExample(example);
        Map<Long, List<TreeNode>> map = new HashMap<Long, List<TreeNode>>();
        for (Long id : parentIds) {
            List<TreeNode> nextTreeNodeList = new ArrayList<TreeNode>();
            for (Category cls : nextCategoryList) {
                if (id.toString().equals(cls.getParentId())) {
                    TreeNode nextTreeNode = new TreeNode();
                    nextTreeNode.setId(cls.getId().toString());
                    nextTreeNode.setName(cls.getName());
                    nextTreeNode.setIsValid(cls.getIsValid());
                    nextTreeNode.setSort(cls.getSort());
                    nextTreeNode.setLevel(cls.getLevel());
                    nextTreeNode.setFullPathId(cls.getFullPathId());
                    nextTreeNode.setSource(cls.getSource());
                    nextTreeNode.setIsLeaf(cls.getIsLeaf());
                    nextTreeNode.setCategoryCode(cls.getCategoryCode());
                    nextTreeNode.setChildren(null);
                    nextTreeNodeList.add(nextTreeNode);
                }
            }
            map.put(id, nextTreeNodeList);
        }
        return map;
    }


}
