package org.trc.form.purchase;

import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import javax.ws.rs.FormParam;

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
    @FormParam("id")
    @ApiModelProperty("采购退货单ID")
    private Long id;


    @FormParam("auditStatus")
    @Length(max = 1)
    @ApiModelProperty("审核状态")
    private String auditStatus;

    @FormParam("auditOpinion")
    @Length(max = 1500, message = "审核意见长度需在0~1500之间")
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
