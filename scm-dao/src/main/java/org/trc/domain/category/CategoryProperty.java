package org.trc.domain.category;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.trc.custom.CustomDateSerializer;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.ws.rs.FormParam;
import javax.ws.rs.PathParam;
import java.util.Date;

/**
 * Created by hzwdx on 2017/5/18.
 */
public class CategoryProperty {

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
    private Long propertySort;

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

    public Long getPropertySort() {
        return propertySort;
    }

    public void setPropertySort(Long propertySort) {
        this.propertySort = propertySort;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

}
