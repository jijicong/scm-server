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
@Getter
@Setter
public class PurchaseBoxInfoResultVO implements Serializable {

    @ApiModelProperty("物流公司名称")
    @ApiParam("物流公司名称")
    private String logisticsCorporationName;

    @ApiModelProperty("装箱方式")
    @ApiParam("装箱方式")
    private String packingType;

    @ApiModelProperty("包装信息明细")
    @ApiParam("包装信息明细")
    private List<PurchaseBoxInfoDetailResultVO> purchaseBoxInfoDetailResultVOList;
}
