package org.trc.domain.category;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.trc.custom.CustomDateSerializer;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.ws.rs.FormParam;
import javax.ws.rs.PathParam;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by hzwdx on 2017/5/18.
 */
public class CategoryProperty implements Serializable{

    @PathParam("id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @FormParam("categoryId")
    @NotEmpty
    private Long categoryId;
    @FormParam("propertyId")
    @NotEmpty
    @Length(max = 32, message = "品牌编号长度不能超过32个")
    private Long propertyId;
    @FormParam("propertySort")
    @NotEmpty
    private Integer propertySort;

    @Transient
    private String name;
    @Transient
    private String typeCode;
    @Transient
    private String valueType;

    @FormParam("isValid")
    private String isValid; //是否有效:0-否,1-是

    @FormParam("isDeleted")
    private String isDeleted; //是否删除:0-否,1-是


    @JsonSerialize(using = CustomDateSerializer.class)
    private Date createTime; //创建时间

//    @JsonSerialize(using = CustomDateSerializer.class)
//    private Date updateTime; //更新时间

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Long getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(Long propertyId) {
        this.propertyId = propertyId;
    }

    public Integer getPropertySort() {
        return propertySort;
    }

    public void setPropertySort(Integer propertySort) {
        this.propertySort = propertySort;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public String getValueType() {
        return valueType;
    }

    public void setValueType(String valueType) {
        this.valueType = valueType;
    }

    public String getIsValid() {
        return isValid;
    }

    public void setIsValid(String isValid) {
        this.isValid = isValid;
    }

    public String getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(String isDeleted) {
        this.isDeleted = isDeleted;
    }
}
