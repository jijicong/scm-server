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
    private String entityId;//查询类型id
    @QueryParam("operateType")
    private String operateType;;//操作类型:0 申请者操作 1 审核者操作
    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getOperateType() {
        return operateType;
    }

    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }
}
