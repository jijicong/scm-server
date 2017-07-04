package org.trc.model;

/**
 * 分类
 * Created by hzdzf on 2017/5/25.
 */
public class CategoryToTrcDO {

    private Long parentId;//父级分类id

    private String classifyDescribe;//分类描述

    private String name;//分类名称

    private Integer sort;//排序

    private String isValid;

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getClassifyDescribe() {
        return classifyDescribe;
    }

    public void setClassifyDescribe(String classifyDescribe) {
        this.classifyDescribe = classifyDescribe;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public String getIsValid() {
        return isValid;
    }

    public void setIsValid(String isValid) {
        this.isValid = isValid;
    }
}
