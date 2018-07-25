package org.trc.domain.purchase;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.ws.rs.FormParam;
import java.io.Serializable;

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

    @FormParam("gridValue")
    @ApiModelProperty("装箱信息")
    private String gridValue;

    @FormParam("logisticsCorporationName")
    @ApiModelProperty("物流公司名称")
    private String logisticsCorporationName;

    @FormParam("logisticsCode")
    @ApiModelProperty("物流公司编号")
    private String logisticsCode;

    @FormParam("packingType")
    @ApiModelProperty("装箱方式")
    private String packingType;
}
