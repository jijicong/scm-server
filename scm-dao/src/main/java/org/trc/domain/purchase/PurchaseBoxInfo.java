package org.trc.domain.purchase;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.trc.domain.BaseDO;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
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

    @FormParam("skuCode")
    @ApiModelProperty("sku编码")
    @ApiParam("sku编码")
    private String skuCode;

    @FormParam("purchaseOrderCode")
    @ApiModelProperty("采购单单号")
    @ApiParam("采购单单号")
    private String purchaseOrderCode;

    @FormParam("amountPerBox")
    @ApiParam("每箱数量")
    @ApiModelProperty("每箱数量")
    @Max(value = 100000, message = "每箱数量不能超过100000")
    @Min(value = 0, message = "每箱数量不能小于0")
    private Long amountPerBox;

    @FormParam("boxNumber")
    @ApiParam("箱号")
    @Length(max = 15, message = "箱号字母和数字不能超过15个")
//    @ApiModelProperty("箱号")
    @NotEmpty(message = "箱号为必填项")
    private String boxNumber;

    @FormParam("boxAmount")
    @ApiModelProperty("箱数")
    @Max(value = 100000, message = "箱数不能超过100000")
    @Min(value = 0, message = "箱数不能小于0")
    private Long boxAmount;

    @FormParam("amount")
    @ApiModelProperty("总数")
    @Max(value = 100000, message = "总数不能超过100000")
    @Min(value = 0, message = "总数不能小于0")
    private Long amount;

    @FormParam("grossWeight")
    @ApiModelProperty("毛重")
    @NotEmpty(message = "毛重为必填项")
    @Length(max = 20, message = "毛重数字不能超过20个")
    @Pattern(regexp = "^\\d+(\\.\\d{1,2})?$", message = "毛重为2位小数位浮点数")
    private String grossWeight;

    @FormParam("cartonSize")
    @Length(max = 20, message = "外箱尺寸字母和数字不能超过20个")
    @NotEmpty(message = "外箱尺寸为必填项")
    @ApiModelProperty("外箱尺寸")
    @Pattern(regexp = "^[\\d.*]?$", message = "外箱尺寸只能填数字.*")
    private String cartonSize;

    @FormParam("volume")
    @ApiModelProperty("体积")
    @NotEmpty(message = "体积为必填项")
    @Length(max = 20, message = "体积数字不能超过20个")
    @Pattern(regexp = "^\\d+(\\.\\d{1,4})?$", message = "体积为4位小数位浮点数")
    private String volume;

    @FormParam("remark")
    @ApiModelProperty("备注")
    @Length(max = 100, message = "备注字母和数字不能超过100个")
    private String remark;

}
