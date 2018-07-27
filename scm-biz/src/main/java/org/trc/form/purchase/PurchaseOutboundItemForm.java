package org.trc.form.purchase;

import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;
import org.trc.util.QueryModel;

import javax.ws.rs.QueryParam;

/**
 * Description〈采购退货单详情查询条件〉
 *
 * @author hzliuwei
 * @create 2018/7/24
 */
public class PurchaseOutboundItemForm extends QueryModel {

    private static final long serialVersionUID = 9089099507343103134L;

    /**
     * 退货供应商
     */
    @QueryParam("supplierCode")
    @Length(max = 64)
    @ApiModelProperty(name = "退货供应商", required = true)
    private String supplierCode;

    /**
     * 退货仓库
     */
    @QueryParam("warehouseCode")
    @Length(max = 64)
    @ApiModelProperty(name = "退货仓库", required = true)
    private String warehouseCode;

    /**
     * 退货类型
     */
    @QueryParam("returnOrderType")
    @ApiModelProperty(name = "退货类型", required = true)
    private String returnOrderType;

    /**
     * sku 名称
     */
    @QueryParam("skuName")
    @Length(max = 128)
    @ApiModelProperty(name = "sku名称")
    private String skuName;

    /**
     * skucode
     */
    @QueryParam("skuCode")
    @ApiModelProperty(name = "skuCode，多个以英文格式[,]分隔")
    private String skuCode;

    /**
     * 品牌名称
     */
    @QueryParam("brandName")
    @ApiModelProperty(name = "品牌名称")
    private String brandName;

    /**
     * 条形码
     */
    @QueryParam("barCode")
    @ApiModelProperty(name = "条形码，多个以英文格式[,]分隔")
    private String barCode;

    public String getSupplierCode() {
        return supplierCode;
    }

    public void setSupplierCode(String supplierCode) {
        this.supplierCode = supplierCode;
    }

    public String getWarehouseCode() {
        return warehouseCode;
    }

    public void setWarehouseCode(String warehouseCode) {
        this.warehouseCode = warehouseCode;
    }

    public String getReturnOrderType() {
        return returnOrderType;
    }

    public void setReturnOrderType(String returnOrderType) {
        this.returnOrderType = returnOrderType;
    }

    public String getSkuName() {
        return skuName;
    }

    public void setSkuName(String skuName) {
        this.skuName = skuName;
    }

    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }
}
