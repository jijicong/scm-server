package org.trc.form.returnIn;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.trc.custom.CustomDateSerializer;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 退货入库单收货结果通知
 */
@Getter
@Setter
public class ReturnInWmsResponseForm implements Serializable {

    /**
     * 售后单编号
     */
    @ApiModelProperty(value="售后单编号")
    private String afterSaleCode;

    /**
     * 入库单编号
     */
    @ApiModelProperty(value="入库单编号")
    private String warehouseNoticeCode;

    /**
     * 理货结果录入备注
     */
    @ApiModelProperty(value="理货结果录入备注")
    private String recordRemark;

    /**
     * 理货结果上传图片, 多个图片路径用逗号分隔
     */
    @ApiModelProperty(value="理货结果上传图片, 多个图片路径用逗号分隔")
    private String recordPicture;

    /**
     * 确认到货备注
     */
    @ApiModelProperty(value="确认到货备注")
    private String confirmRemark;

    /**
     * 操作人
     */
    @ApiModelProperty(value="操作人")
    private String operator;

    /**
     * 入库时间
     */
    @ApiModelProperty(value="入库时间")
    @JsonSerialize(using = CustomDateSerializer.class)
    private Date warehouseTime;

    /**
     * 退货入库明细
     */
    @ApiModelProperty(value="退货入库明细")
    private List<ReturnInDetailWmsResponseForm> returnInDetailWmsResponseFormList;

}
