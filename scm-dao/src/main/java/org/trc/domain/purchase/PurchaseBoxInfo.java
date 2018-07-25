package org.trc.domain.purchase;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.trc.domain.BaseDO;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.ws.rs.FormParam;
import javax.ws.rs.PathParam;

/**
 * Created by hzcyn on 2018/7/24.
 */
@Setter
@Getter
public class PurchaseBoxInfo extends BaseDO {

    private static final long serialVersionUID = 1L;

    @PathParam("id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    //主键
    private Long id;

    @FormParam("purchaseDetailId")
    @ApiModelProperty("采购单详情主键")
    private Long purchaseDetailId;

    @FormParam("purchaseOrderCode")
    @ApiModelProperty("采购单单号")
    private String purchaseOrderCode;

    @FormParam("amountPerBox")
    @ApiModelProperty("每箱数量")
    private Long amountPerBox;

    @FormParam("boxNumber")
    @Length(max = 15, message = "箱号字母和数字不能超过15个")
    @ApiModelProperty("箱号")
    private String boxNumber;

    @FormParam("boxAmount")
    @ApiModelProperty("箱数")
    private Long boxAmount;

    @FormParam("amount")
    @ApiModelProperty("总数")
    private Long amount;

    @FormParam("grossWeight")
    @ApiModelProperty("毛重")
    private Long grossWeight;

    @FormParam("cartonSize")
    @Length(max = 50, message = "外箱尺寸字母和数字不能超过50个")
    @ApiModelProperty("外箱尺寸")
    private String cartonSize;

    @FormParam("volume")
    @ApiModelProperty("体积")
    private Long volume;

    @FormParam("remark")
    @ApiModelProperty("备注")
    @Length(max = 100, message = "备注字母和数字不能超过100个")
    private String remark;

}
