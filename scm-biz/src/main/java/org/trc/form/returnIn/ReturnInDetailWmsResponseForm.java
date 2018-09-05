package org.trc.form.returnIn;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 退货入库单收货结果通知
 */
@Getter
@Setter
public class ReturnInDetailWmsResponseForm implements Serializable {

    /**
     * 入库单编号
     */
    @ApiModelProperty(value="入库单编号")
    private String warehouseNoticeCode;

    /**
     * skuCode
     */
    @ApiModelProperty(value="skuCode")
    private String skuCode;

    /**
     * 正品入库数量
     */
    @ApiModelProperty(value="正品入库数量")
    private Integer inNum;

    /**
     * 残品入库数量
     */
    @ApiModelProperty(value="残品入库数量")
    private Integer defectiveInNum;


}
