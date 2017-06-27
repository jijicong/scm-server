package org.trc.domain.config;

import javax.persistence.Table;
import java.util.Date;

/**
 * Created by hzqph on 2017/6/20.
 */
@Table(name = "log_information")
public class LogInfo {
    private Long id;
    private String entityType;
    private String entityId;
    private String operation;
    private String operatorUserid;
    private String operator;
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

    public String getOperatorUserid() {
        return operatorUserid;
    }

    public void setOperatorUserid(String operatorUserid) {
        this.operatorUserid = operatorUserid;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }
}
