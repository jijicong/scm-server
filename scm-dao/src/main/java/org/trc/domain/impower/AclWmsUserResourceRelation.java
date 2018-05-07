package org.trc.domain.impower;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.trc.custom.CustomDateSerializer;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.ws.rs.PathParam;
import java.io.Serializable;
import java.util.Date;

/**
 * wms用户资源关联
 */
public class AclWmsUserResourceRelation implements Serializable {
    /**
     * 主键ID
     */
    @PathParam("id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String wmsUserId;

    private Long wmsUserAccreditId;

    private Long resourceCode;

    /**
     * 创建人
     */
    private String createOperator;

    /**
     * 创建时间
     */
    @JsonSerialize(using = CustomDateSerializer.class)
    private Date createTime;

    /**
     * 更新时间
     */
    @JsonSerialize(using = CustomDateSerializer.class)
    private Date updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWmsUserId() {
        return wmsUserId;
    }

    public void setWmsUserId(String wmsUserId) {
        this.wmsUserId = wmsUserId;
    }

    public Long getWmsUserAccreditId() {
        return wmsUserAccreditId;
    }

    public void setWmsUserAccreditId(Long wmsUserAccreditId) {
        this.wmsUserAccreditId = wmsUserAccreditId;
    }

    public Long getResourceCode() {
        return resourceCode;
    }

    public void setResourceCode(Long resourceCode) {
        this.resourceCode = resourceCode;
    }

    public String getCreateOperator() {
        return createOperator;
    }

    public void setCreateOperator(String createOperator) {
        this.createOperator = createOperator;
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
