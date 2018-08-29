package org.trc.form.afterSale;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.trc.util.QueryModel;

/**
 * @Auther: hzluoxingcheng
 * @Date: 2018/8/29 14:28
 * @Description: 售后单字表（对接界面接受数据）
 */
@Setter
@Getter
public class AfterSaleOrderDetailForm  extends QueryModel{

    /**
     * skucode
     */
    @ApiModelProperty("skucode")
    private String skuCode;

    /**
     *sku名称
     */
    @ApiModelProperty("sku名称")
    private String skuName;








}
