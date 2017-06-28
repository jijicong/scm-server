package org.trc.domain.purchase;

import org.hibernate.validator.constraints.NotEmpty;
import org.trc.domain.BaseDO;
import org.trc.domain.util.CommonDO;

import javax.persistence.*;
import javax.ws.rs.FormParam;
import javax.ws.rs.PathParam;
import java.io.Serializable;

/**
 * Created by sone on 2017/6/20.
 */
@Table(name="apply_for_purchase_order")
public class PurchaseOrderAudit extends CommonDO implements Serializable {
    @PathParam("id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //'申请编号'
    //private String applyCode;
    //'采购单编号'
    @FormParam("purchaseOrderCode")
    @NotEmpty
    private String purchaseOrderCode;
    // '采购单id'
    @FormParam("purchaseId")
    private Long purchaseOrderId;
    //'申请说明'
    private String description;
    //'状态:0-暂存,1-提交审核,2-审核通过,3-审核驳回'
    @FormParam("status")
    @NotEmpty
    private String status;
    //'审核意见'
    @FormParam("auditOpinion")
    private  String auditOpinion;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPurchaseOrderCode() {
        return purchaseOrderCode;
    }

    public void setPurchaseOrderCode(String purchaseOrderCode) {
        this.purchaseOrderCode = purchaseOrderCode;
    }

    public Long getPurchaseOrderId() {
        return purchaseOrderId;
    }

    public void setPurchaseOrderId(Long purchaseOrderId) {
        this.purchaseOrderId = purchaseOrderId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAuditOpinion() {
        return auditOpinion;
    }

    public void setAuditOpinion(String auditOpinion) {
        this.auditOpinion = auditOpinion;
    }

}
