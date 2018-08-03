package org.trc.form.purchase;

import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import javax.ws.rs.QueryParam;

/**
 * Description〈采购退货单审核form〉
 *
 * @author hzliuwei
 * @create 2018/8/2
 */
public class AuditPurchaseOrderForm {

    /**
     * 采购退货单编号
     */
    @QueryParam("id")
    @Length(max = 32)
    @ApiModelProperty("采购退货单ID")
    private Long id;


    @QueryParam("auditStatus")
    @Length(max = 1)
    @ApiModelProperty("审核状态")
    private String auditStatus;

    @QueryParam("auditOpinion")
    @Length(max = 3000)
    @ApiModelProperty("审核意见")
    private String auditOpinion;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(String auditStatus) {
        this.auditStatus = auditStatus;
    }

    public String getAuditOpinion() {
        return auditOpinion;
    }

    public void setAuditOpinion(String auditOpinion) {
        this.auditOpinion = auditOpinion;
    }
}
