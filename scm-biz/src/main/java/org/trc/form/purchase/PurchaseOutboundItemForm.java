package org.trc.form.purchase;

import io.swagger.annotations.ApiParam;
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
    @ApiParam(value = "退货供应商", required = true)
    private String supplierCode;

    /**
     * 退货仓库
     */
    @QueryParam("warehouseInfoId")
    @Length(max = 64)
    @ApiParam(value = "退货仓库id", required = true)
    private String warehouseInfoId;

    /**
     * 退货类型
     */
    @QueryParam("returnOrderType")
    @ApiParam(value = "退货类型", required = true)
    private String returnOrderType;

    /**
     * sku 名称
     */
    @QueryParam("skuName")
    @Length(max = 128)
    @ApiParam(value = "sku名称")
    private String skuName;

    /**
     * skucode
     */
    @QueryParam("skuCode")
    @ApiParam(value = "skuCode，多个以英文格式[,]分隔")
    private String skuCode;

    /**
     * 品牌ID
     */
    @QueryParam("brandId")
    @ApiParam(value = "品牌ID")
    private String brandId;

    /**
     * 条形码
     */
    @QueryParam("barCode")
    @ApiParam(value = "条形码，多个以英文格式[,]分隔")
    private String barCode;

    public String getSupplierCode() {
        return supplierCode;
    }

    public void setSupplierCode(String supplierCode) {
        this.supplierCode = supplierCode;
    }

    public String getWarehouseInfoId() {
        return warehouseInfoId;
    }

    public void setWarehouseInfoId(String warehouseInfoId) {
        this.warehouseInfoId = warehouseInfoId;
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

    public String getBrandId() {
        return brandId;
    }

    public void setBrandId(String brandId) {
        this.brandId = brandId;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }
}
