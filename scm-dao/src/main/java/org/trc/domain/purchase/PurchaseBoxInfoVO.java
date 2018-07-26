package org.trc.domain.purchase;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import javax.ws.rs.FormParam;
import java.io.Serializable;
import java.util.List;

/**
 * Created by hzcyn on 2018/7/25.
 */
@Setter
@Getter
public class PurchaseBoxInfoVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @FormParam("purchaseOrderCode")
    @ApiModelProperty("采购单单号")
    private String purchaseOrderCode;

    @NotEmpty(message = "装箱信息详情不能为空")
    @ApiModelProperty("装箱信息")
    @Valid
    private List<PurchaseBoxInfo> purchaseBoxInfoList;

    @FormParam("logisticsCorporationName")
    @ApiModelProperty("物流公司名称")
    private String logisticsCorporationName;

    @FormParam("logisticsCode")
    @ApiModelProperty("物流公司编号")
    private String logisticsCode;

    @FormParam("packingType")
    @ApiModelProperty("装箱方式")
    @NotEmpty(message = "装箱方式不能为空")
    @Size(max=50, message = "装箱方式过长，超过50字符")
    private String packingType;
}
