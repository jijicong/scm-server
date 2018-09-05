package org.trc.domain.afterSale;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.trc.custom.CustomDateSerializer;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.ws.rs.FormParam;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 售后单明细表
 * </p>
 *
 * @author wangjie
 * @since 2018-08-27
 */
@Table(name="after_sale_order_detail")
@Setter
@Getter
public class AfterSaleOrderDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Id
    @ApiModelProperty(value="主键")
	@FormParam("id")
	private String id;
    /**
     * 售后单code(对应售后主表)
     */
    @ApiModelProperty(value="售后单code(对应售后主表)")
	@FormParam("afterSaleCode")
    @Column(name="after_sale_code")
	private String afterSaleCode;
    
    /**
     * 店铺订单编号
     */
    @ApiModelProperty(value="店铺订单编号")
	@FormParam("shopOrderCode")
    @Column(name="shop_order_code")
	private String shopOrderCode;
	/**
	 * 系统订单号
	 */
	@Column(name="scm_shop_order_code")
	private String scmShopOrderCode;
    /**
     * 渠道商品订单号
     */
    @ApiModelProperty(value="渠道商品订单号")
	@FormParam("orderItemCode")
    @Column(name="order_item_code")
	private String orderItemCode;
    
    /**
     * spuCode
     */
    @ApiModelProperty(value="spuCode")
	@FormParam("spuCode")
	@Column(name="sku_code")
	private String spuCode;
    
    /**
     * skucode
     */
    @ApiModelProperty(value="skuCode")
	@FormParam("skuCode")
	@Column(name="sku_code")
	private String skuCode;
    
	/**
     * 商品名称
     */
    @ApiModelProperty(value="商品名称")
	@FormParam("skuName")
	@Column(name="sku_name")
	private String skuName;
    

	/**
     * 条形码
     */
    @ApiModelProperty(value="条形码")
	@FormParam("barCode")
	@Column(name="bar_code")
	private String barCode;
    
    /**
     * 商品品牌
     */
    @ApiModelProperty(value="商品品牌")
	@FormParam("brandName")
	@Column(name="brand_name")
	private String brandName;
    
	/**
     * 商品规格描述
     */
    @ApiModelProperty(value="商品规格描述")
	@FormParam("specNatureInfo")
	@Column(name="spec_nature_info")
	private String specNatureInfo;
    
	/**
     * 商品数量
     */
    @ApiModelProperty(value="商品数量")
	@FormParam("num")
	private Integer num;

    /**
     * 最大可退数量
     */
    @ApiModelProperty(value="最大可退数量")
	@FormParam("maxReturnNum")
	@Column(name="max_return_num")
	private Integer maxReturnNum;
	/**
     * 拟退货数量
     */
    @ApiModelProperty(value="拟退货数量")
	@FormParam("returnNum")
	@Column(name="return_num")
	private Integer returnNum;
	/**
     * 正品入库数量
     */
    @ApiModelProperty(value="正品入库数量")
	@FormParam("inNum")
	@Column(name="in_num")
	private Integer inNum;
	/**
     * 残品入库数量
     */
    @ApiModelProperty(value="残品入库数量")
	@FormParam("defectiveInNum")
	@Column(name="defective_in_num")
	private Integer defectiveInNum;
	/**
     * 退款金额
     */
    @ApiModelProperty(value="退款金额")
	@FormParam("refundAmont")
	@Column(name="refund_amont")
	private BigDecimal refundAmont;
    
    /**
     * 商品图片,多张图片逗号分隔
     */
    @ApiModelProperty(value="商品图片,多张图片逗号分隔")
	@FormParam("picture")
	@Column(name="picture")
	private String picture;
	/**
     * 发货仓库编码
     */
    @ApiModelProperty(value="发货仓库编码")
	@FormParam("deliverWarehouseCode")
    @Column(name="deliver_warehouse_code")
	private String deliverWarehouseCode;
    /**
     * 发货仓库名称
     */
    @ApiModelProperty(value="发货仓库名称")
	@FormParam("deliverWarehouseName")
    @Column(name="deliver_warehouse_name")
	private String deliverWarehouseName;
    /**
     * 创建时间
     */
    @ApiModelProperty(value="创建时间")
	@JsonSerialize(using = CustomDateSerializer.class)
	@FormParam("createTime")
	@Column(name="create_time")
	private Date createTime;
    /**
     * 修改时间
     */
    @ApiModelProperty(value="修改时间")
	@JsonSerialize(using = CustomDateSerializer.class)
	@FormParam("updateTime")
	@Column(name="update_time")
	private Date updateTime;





	
}
