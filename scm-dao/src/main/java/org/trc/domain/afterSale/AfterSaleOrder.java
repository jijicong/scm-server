package org.trc.domain.afterSale;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Date;


/**
 * <p>
 * 售后主表
 * </p>
 *
 * @author wangjie
 * @since 2018-08-27
 */
@Table(name="after_sale_order")
@Getter
@Setter
public class AfterSaleOrder  implements Serializable{

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ApiModelProperty("主键")
    @Id
	private String id;
    /**
     * 售后单编号
     */
    @ApiModelProperty("售后单编号")
    @Column(name="after_sale_code")
	private String afterSaleCode;
    /**
     * 店铺订单编号
     */
    @ApiModelProperty("店铺订单编号")
    @Column(name="shop_order_code")
	private String shopOrderCode;
    /**
     * 系统订单号
     */
    @ApiModelProperty("系统订单号")
    @Column(name="scm_shop_order_code")
	private String scmShopOrderCode;

	/**
	 * 业务线编码
	 */
    @ApiModelProperty("业务线编码")
	@Column(name="channel_code")
    private String channelCode;
	/**
	 * 业务线名称
	 */
    @ApiModelProperty("业务线名称")
	@Transient
	private String channelName;
	/**
	 * 销售渠道编码
	 */
    @ApiModelProperty("销售渠道编码")
	@Column(name="sell_code")
    private String sellCode;
	/**
	 * 销售渠道名称
	 */
    @ApiModelProperty("销售渠道名称")
	@Transient
	private String sellName;
    /**
     * 商铺图片路径（多个图片用逗号分隔开）
     */
    @ApiModelProperty("商铺图片路径（多个图片用逗号分隔开）")
	private String picture;
	/**
     * 订单所属的店铺id
     */
    @ApiModelProperty("订单所属的店铺id")
    @Column(name="shop_id")
	private long shopId;
    /**
     * 店铺名称
     */
    @ApiModelProperty("店铺名称")
    @Column(name="shop_name")
	private String shopName;
    /**
     * 用户id
     */
    @ApiModelProperty("用户id")
    @Column(name="user_id")
	private String userId;
    /**
     * 会员名称
     */
    @ApiModelProperty("会员名称")
    @Column(name="user_name")
	private String userName;

    /**
     * 收货人所在省
     */
    @ApiModelProperty("收货人所在省")
    @Column(name="receiver_province")
	private String receiverProvince;
    /**
     * 收货人所在城市
     */
    @ApiModelProperty("收货人所在城市")
    @Column(name="receiver_city")
	private String receiverCity;
    /**
     * 收货人所在地区
     */
    @ApiModelProperty("收货人所在地区")
    @Column(name="receiver_district")
	private String receiverDistrict;
    /**
     * 收货人详细地址
     */
    @ApiModelProperty("收货人详细地址")
    @Column(name="receiver_address")
	private String receiverAddress;
    /**
     * 收货人姓名
     */
    @ApiModelProperty("收货人姓名")
    @Column(name="receiver_name")
	private String receiverName;
    /**
     * 收货人身份证
     */
    @ApiModelProperty("收货人身份证")
    @Column(name="receiver_id_card")
	private String receiverIdCard;
    /**
     * 收货人电话号码
     */
    @ApiModelProperty("收货人电话号码")
    @Column(name="receiver_phone")
	private String receiverPhone;
    /**
     * 收货人手机号码
     */
    @ApiModelProperty("收货人手机号码")
    @Column(name="receiver_mobile")
	private String receiverMobile;
    /**
     * 收货人电子邮箱
     */
    @ApiModelProperty("收货人电子邮箱")
    @Column(name="receiver_email")
	private String receiverEmail;
    /**
     * 支付时间
     */
    @ApiModelProperty("支付时间")
    @Column(name="pay_time")
	private Date payTime;
    /**
     * 退货收货仓库编码
     */
    @ApiModelProperty("退货收货仓库编码")
    @Column(name="return_warehouse_code")
	private String returnWarehouseCode;
    /**
     * 退货收货仓库名称
     */
    @ApiModelProperty("退货收货仓库名称")
    @Transient
    private String returnWarehouseName;
    /**
     * 退货详细地址
     */
    @ApiModelProperty("退货详细地址")
    @Column(name="return_address")
	private String returnAddress;
    /**
     * 备注
     */
    @ApiModelProperty("备注")
	private String memo;
    /**
     * 快递公司编码
     */
    @ApiModelProperty("快递公司编码")
    @Column(name="logistics_corporation_code")
	private String logisticsCorporationCode;
    /**
     * 快递公司名称
     */
    @ApiModelProperty("快递公司名称")
    @Column(name="logistics_corporation")
	private String logisticsCorporation;
    /**
     * 运单号
     */
    @ApiModelProperty("运单号")
    @Column(name="waybill_number")
	private String waybillNumber;
    /**
     * 发起类型（0系统发起，1手动新建）
     */
    @ApiModelProperty("发起类型（0系统发起，1手动新建）")
    @Column(name="launch_type")
    private Integer launchType;
    /**
     * 售后类型(0取消发货,1退货)
     */
    @ApiModelProperty("售后类型(0取消发货,1退货)")
    @Column(name="after_sale_type")
    private Integer afterSaleType;

    /**
     * 售后单状态（0待客户发货，1客户已经发货,2已经完成，3已经取消）
     */
    @ApiModelProperty("售后单状态（0待客户发货，1客户已经发货,2已经完成，3已经取消）")
	private int status;
    /**
     * 创建时间（格式yyyy-mm-dd hh:mi:ss''）
     */
    @ApiModelProperty("创建时间（格式yyyy-mm-dd hh:mi:ss''）")
    @Column(name="create_time")
	private Date createTime;
    /**
     * 创建人员编号
     */
    @ApiModelProperty("创建人员编号")
    @Column(name="create_operator")
	private String createOperator;
    /**
     * 修改时间（格式yyyy-mm-dd hh:mi:ss''）
     */
    @ApiModelProperty("修改时间（格式yyyy-mm-dd hh:mi:ss''）")
    @Column(name="update_time")
	private Date updateTime;
    /**
     * 修改人员编号
     */
    @ApiModelProperty("修改人员编号")
    @Column(name="update_operator")
	private String updateOperator;




}
