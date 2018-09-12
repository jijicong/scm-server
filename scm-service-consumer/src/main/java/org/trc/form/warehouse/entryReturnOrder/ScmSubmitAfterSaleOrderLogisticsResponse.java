package org.trc.form.warehouse.entryReturnOrder;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ScmSubmitAfterSaleOrderLogisticsResponse {

    /**
     *  是否提交成功：1-提交成功, 2-提交失败
     */
    private String flag;

    /**
     * 说明信息
     */
    private String message;

}
