package org.trc.domain.purchase;

import io.swagger.annotations.ApiParam;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Size;
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
    @ApiParam("采购单单号")
    private String purchaseOrderCode;

    @NotEmpty(message = "装箱信息详情不能为空")
    @FormParam("purchaseBoxInfoListJSON")
    @ApiParam("装箱信息详情,数据格式:{\n\"" +
            "                    \"skuCode\":\"sku编码\",\n " +
            "                    \"purchaseOrderCode\":\"采购单单号\",\n " +
            "                    \"amountPerBox\":\"每箱数量\",\n " +
            "                    \"boxNumber\":\"箱号\",\n " +
            "                    \"boxAmount\":\"箱数\"\n" +
            "                    \"amount\":\"总数\"\n" +
            "                    \"grossWeight\":\"毛重\"\n" +
            "                    \"cartonSize\":\"外箱尺寸\"\n" +
            "                    \"volume\":\"体积\"\n" +
            "                    \"remark\":\"备注\"\n" +
            "                    \"}")
    private String purchaseBoxInfoListJSON;

    @FormParam("logisticsCorporationName")
    @ApiParam("物流公司名称")
    private String logisticsCorporationName;

    @FormParam("logisticsCode")
    @ApiParam("物流公司编号")
    private String logisticsCode;

    @FormParam("packingType")
    @ApiParam("装箱方式")
    @Size(max=50, message = "装箱方式过长，超过50字符")
    private String packingType;
}
