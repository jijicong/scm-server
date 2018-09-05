package org.trc.domain.afterSale;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Table;
import javax.ws.rs.FormParam;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;


/**
 * <p>
 * 退货入库单子表
 * </p>
 *
 * @author wangjie
 * @since 2018-08-29
 */
@Table(name="after_sale_warehouse_notice_detail")
@Setter
@Getter
public class AfterSaleWarehouseNoticeDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ApiModelProperty(value="主键")
    @FormParam("id")
	private String id;
    /**
     * 订单编号
     */
    @ApiModelProperty(value="订单编号")
	@Column(name="shop_order_code")
    @FormParam("shopOrderCode")
	private String shopOrderCode;
    /**
     * 入库单编号
     */
    @ApiModelProperty(value="入库单编号")
	@Column(name="warehouse_notice_code")
    @FormParam("warehouseNoticeCode")
	private String warehouseNoticeCode;
	/**
     * 渠道商品订单号
     */
    @ApiModelProperty(value="渠道商品订单号")
	@Column(name="order_item_code")
    @FormParam("orderItemCode")
	private String orderItemCode;
    
    /**
     * spuCode
     */
    @ApiModelProperty(value="spuCode")
	@FormParam("spuCode")
	@Column(name="sku_code")
	private String spuCode;
    
    /**
     * skuCode
     */
    @ApiModelProperty(value="skuCode")
	@Column(name="sku_code")
    @FormParam("skuCode")
	private String skuCode;
	/**
     * 商品名称
     */
    @ApiModelProperty(value="商品名称")
	@Column(name="sku_name")
    @FormParam("skuName")
	private String skuName;
	/**
     * 商品品牌
     */
    @ApiModelProperty(value="商品品牌")
	@Column(name="brand_name")
    @FormParam("brandName")
	private String brandName;
    /**
     * 条形码
     */
    @ApiModelProperty(value="条形码")
	@Column(name="bar_code")
    @FormParam("barCode")
	private String barCode;
	/**
     * 商品规格描述
     */
    @ApiModelProperty(value="商品规格描述")
	@Column(name="spec_nature_info")
    @FormParam("specNatureInfo")
	private String specNatureInfo;
    /**
     * 拟退货数量
     */
    @ApiModelProperty(value="拟退货数量")
	@Column(name="return_num")
    @FormParam("returnNum")
	private Integer returnNum;
	/**
     * 总入库数量
     */
    @ApiModelProperty(value="总入库数量")
	@Column(name="total_in_num")
    @FormParam("totalInNum")
	private Integer totalInNum;
    /**
     * 正品入库数量
     */
    @ApiModelProperty(value="正品入库数量")
	@Column(name="in_num")
    @FormParam("inNum")
	private Integer inNum;
    /**
     * 残品入库数量
     */
    @ApiModelProperty(value="残品入库数量")
	@Column(name="defective_in_num")
    @FormParam("defectiveInNum")
	private Integer defectiveInNum;
	/**
     * 商品图片,多张图片逗号分隔
     */
    @ApiModelProperty(value="商品图片,多张图片逗号分隔")
	@Column(name="picture")
    @FormParam("picture")
	private String picture;
    /**
     * 修改时间
     */
    @ApiModelProperty(value="修改时间")
	@Column(name="update_time")
    @FormParam("updateTime")
	private Date updateTime;
    /**
     * 添加时间
     */
    @ApiModelProperty(value="添加时间")
	@Column(name="create_time")
    @FormParam("createTime")
	private Date createTime;


	
}
