package org.trc.domain.category;

import org.hibernate.validator.constraints.NotEmpty;
import org.trc.domain.BaseDO;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.ws.rs.FormParam;

public class PropertyValue extends BaseDO {

    @FormParam("id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @FormParam("propertyId")
    private Long propertyId;
    @NotEmpty
    @FormParam("value")
    private String value;
    @FormParam("picture")
    private String picture;
    @NotNull
    @FormParam("sort")
    private Integer sort;
    @FormParam("status")
    @Transient
    private Integer status;
    @FormParam("source")
    @Transient
    private Integer source;
    @FormParam("sortStatus")
    @Transient
    private Integer sortStatus;
    @Transient
    private String picUrl;//图片url地址

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(Long propertyId) {
        this.propertyId = propertyId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value == null ? null : value.trim();
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture == null ? null : picture.trim();
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getSource() {
        return source;
    }

    public void setSource(Integer source) {
        this.source = source;
    }

    public Integer getSortStatus() {
        return sortStatus;
    }

    public void setSortStatus(Integer sortStatus) {
        this.sortStatus = sortStatus;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }
}