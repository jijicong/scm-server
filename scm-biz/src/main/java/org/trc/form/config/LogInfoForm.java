package org.trc.form.config;

import org.trc.util.QueryModel;

import javax.ws.rs.QueryParam;

/**
 * Created by hzqph on 2017/7/17.
 */
public class LogInfoForm extends QueryModel {
    @QueryParam("entityType")
    private String entityType;//查询类型
    @QueryParam("entityId")
    private Long entityId;//查询类型id

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }
}
