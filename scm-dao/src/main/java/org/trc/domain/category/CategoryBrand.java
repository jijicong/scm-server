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
public class CategoryBrand{

    @PathParam("id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @FormParam("brandId")
    @NotEmpty
    private Long brandId;
    @FormParam("brandCode")
    @NotEmpty
    @Length(max = 32, message = "品牌编号长度不能超过32个")
    private String brandCode;
    @FormParam("categoryId")
    @NotEmpty
    private Long categoryId;
    @FormParam("categoryCode")
    @NotEmpty
    @Length(max = 32, message = "分类ID编号长度不能超过32个")
    private String categoryCode;

    @JsonSerialize(using = CustomDateSerializer.class)
    private Date createTime; //创建时间

    @JsonSerialize(using = CustomDateSerializer.class)
    private Date updateTime; //更新时间

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBrandId() {
        return brandId;
    }

    public void setBrandId(Long brandId) {
        this.brandId = brandId;
    }

    public String getBrandCode() {
        return brandCode;
    }

    public void setBrandCode(String brandCode) {
        this.brandCode = brandCode;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
