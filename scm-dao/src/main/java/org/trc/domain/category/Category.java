package org.trc.domain.category;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.trc.domain.BaseDO;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.ws.rs.FormParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

/**
 * Created by hzszy on 2017/5/5.
 */
@Table(name = "category")
public class Category extends BaseDO {

    @PathParam("id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @FormParam("fullPathId")
    @NotEmpty
    @Length(max = 64, message = "全路径ID字母和数字不能超过64个,汉字不能超过32个")
    private String fullPathId;

    @FormParam("categoryCode")
    @NotEmpty
    @Length(max = 32, message = "分类编码字母和数字不能超过64个,汉字不能超过32个")
    private String categoryCode;

    @FormParam("sort")
    private int sort;

    @FormParam("source")
    @NotEmpty
    @Length(max = 32)
    private String source;

    @FormParam("level")
    private int level;

    @FormParam("name")
    @Length(max = 128, message = "节点内容字母和数字不能超过128个,汉字不能超过64个")
    private String name;

    @FormParam("isLeaf")
    @Length(max = 2)
    @NotEmpty
    private String isLeaf;

    @FormParam("parentId")
    private String parentId;

    @FormParam("classifyDescribe")
    @Length(max = 40, message = "分类描述字母和数字不能超过40个,汉字不能超过20个")
    private String classifyDescribe;

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullPathId() {
        return fullPathId;
    }

    public void setFullPathId(String fullPathId) {
        this.fullPathId = fullPathId;
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

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getIsLeaf() {
        return isLeaf;
    }

    public void setIsLeaf(String isLeaf) {
        this.isLeaf = isLeaf;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public String getClassifyDescribe() {
        return classifyDescribe;
    }

    public void setClassifyDescribe(String classifyDescribe) {
        this.classifyDescribe = classifyDescribe;
    }
}
