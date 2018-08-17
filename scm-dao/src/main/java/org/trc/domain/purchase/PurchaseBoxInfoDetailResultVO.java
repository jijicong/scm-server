package org.trc.domain.purchase;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * Created by hzcyn on 2018/8/1.
 */
@Setter
@Getter
public class PurchaseBoxInfoDetailResultVO implements Serializable {

    @ApiModelProperty("sku名称")
    @ApiParam("sku名称")
    private String skuName;

    @ApiModelProperty("sku编码")
    @ApiParam("sku编码")
    private String skuCode;

    @ApiModelProperty("规格")
    @ApiParam("规格")
    private String specNatureInfo;

    @ApiModelProperty("条形码")
    @ApiParam("条形码")
    private String barCode;

    private String purchasingQuantity;

    @ApiModelProperty("包装信息详情")
    @ApiParam("包装信息详情")
    private List<PurchaseBoxInfo> purchaseBoxInfoList;
}
