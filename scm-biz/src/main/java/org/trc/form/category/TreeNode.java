package org.trc.form.category;

import java.io.Serializable;
import java.util.List;

/**
 * Created by hzszy on 2017/5/5.
 */
public class TreeNode implements Serializable{

    private String id;

    private String name;

    private boolean expanded = false;

    private List<TreeNode> children;

    private int sort;

    private String isValid;

    private int level;

    private String fullPathId;

    private String categoryCode;

    private String source;

    private String isLeaf;

    private String classifyDescribe;

    public String getClassifyDescribe() {
        return classifyDescribe;
    }

    public void setClassifyDescribe(String classifyDescribe) {
        this.classifyDescribe = classifyDescribe;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getIsLeaf() {
        return isLeaf;
    }

    public void setIsLeaf(String isLeaf) {
        this.isLeaf = isLeaf;
    }

    public int getLevel() {
        return level;
    }

    public String getFullPathId() {
        return fullPathId;
    }

    public void setFullPathId(String fullPathId) {
        this.fullPathId = fullPathId;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public String getIsValid() {
        return isValid;
    }

    public void setIsValid(String isValid) {
        this.isValid = isValid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public List<TreeNode> getChildren() {
        return children;
    }

    public void setChildren(List<TreeNode> children) {
        this.children = children;
    }
}
