package org.trc.domain.purchase;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hibernate.validator.constraints.Length;
import org.trc.custom.CustomDateSerializer;

import javax.ws.rs.FormParam;
import java.util.Date;

/**
 * Created by sone on 2017/6/20.
 */
public class PurchaseOrderAddAudit extends PurchaseOrder{

    //审核状态
    private String status;
    //提交人
    @FormParam("createOperator")
    @Length(max = 32, message = "创建人32个,汉字不能超过16个")
    private String createOperator; //创建人
    //提交审核时间
    @JsonSerialize(using = CustomDateSerializer.class)
    private Date submitTime;

    @Override
    public String getStatus() {
        return status;
    }

    @Override
    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String getCreateOperator() {
        return createOperator;
    }

    @Override
    public void setCreateOperator(String createOperator) {
        this.createOperator = createOperator;
    }

    public Date getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(Date submitTime) {
        this.submitTime = submitTime;
    }

}
