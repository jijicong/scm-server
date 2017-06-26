package org.trc.domain.config;

import org.trc.domain.BaseDO;

import java.util.Date;

/**
 * Created by hzqph on 2017/6/20.
 */
public class LogInfo {
    private Long id;
    private String entityType;
    private String entityId;
    private String operation;
    private String operatiorUserid;
    private String params;
    private String remark;
    private Date operateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getOperatiorUserid() {
        return operatiorUserid;
    }

    public void setOperatiorUserid(String operatiorUserid) {
        this.operatiorUserid = operatiorUserid;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }
}
