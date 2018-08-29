package org.trc.domain.afterSale;

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
public class AfterSaleOrder  implements Serializable{

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Id
	private String id;
    /**
     * 售后单编号
     */
    @Column(name="after_sale_code")
	private String afterSaleCode;
    /**
     * 店铺订单编号
     */
    @Column(name="shop_order_code")
	private String shopOrderCode;
    /**
     * 系统订单号
     */
    @Column(name="scm_shop_order_code")
	private String scmShopOrderCode;

	/**
	 * 业务线编码
	 */
	@Column(name="channel_code")
    private String channelCode;
	/**
	 * 业务线名称
	 */
	@Transient
	private String channelName;
	/**
	 * 销售渠道编码
	 */
	@Column(name="sell_code")
    private String sellCode;
	/**
	 * 销售渠道名称
	 */
	@Transient
	private String sellName;
    /**
     * 商铺图片路径（多个图片用逗号分隔开）
     */
	private String picture;
    /**
     * 备注
     */
	private String memo;
    /**
     * 快递公司编码
     */
	@Column(name="logistics_corporation_code")
	private String logisticsCorporationCode;
    /**
     * 快递公司名称
     */
	@Column(name="logistics_corporation")
	private String logisticsCorporation;
    /**
     * 快递单号
     */
	@Column(name="express_number")
	private String expressNumber;
    /**
     * 入库仓库编号
     */
	@Column(name="wms_code")
	private String wmsCode;
    /**
     * 售后单状态（0待客户发货，1客户已经发货，2是待分配仓库,3已经完成，4已经取消）
     */
	private Integer status;
    /**
     * 创建时间（格式yyyy-mm-dd hh:mi:ss'）
     */
	@Column(name="create_time")
	private Date createTime;
    /**
     * 创建人员编号
     */
	@Column(name="create_operator")
	private String createOperator;
    /**
     * 修改时间（格式yyyy-mm-dd hh:mi:ss'）
     */
	@Column(name="update_time")
	private Date updateTime;
    /**
     * 修改人员编号
     */
	@Column(name="update_operator")
	private String updateOperator;


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAfterSaleCode() {
		return afterSaleCode;
	}

	public void setAfterSaleCode(String afterSaleCode) {
		this.afterSaleCode = afterSaleCode;
	}

	public String getShopOrderCode() {
		return shopOrderCode;
	}

	public void setShopOrderCode(String shopOrderCode) {
		this.shopOrderCode = shopOrderCode;
	}

	public String getScmShopOrderCode() {
		return scmShopOrderCode;
	}

	public void setScmShopOrderCode(String scmShopOrderCode) {
		this.scmShopOrderCode = scmShopOrderCode;
	}

	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public String getLogisticsCorporationCode() {
		return logisticsCorporationCode;
	}

	public void setLogisticsCorporationCode(String logisticsCorporationCode) {
		this.logisticsCorporationCode = logisticsCorporationCode;
	}

	public String getLogisticsCorporation() {
		return logisticsCorporation;
	}

	public void setLogisticsCorporation(String logisticsCorporation) {
		this.logisticsCorporation = logisticsCorporation;
	}

	public String getExpressNumber() {
		return expressNumber;
	}

	public void setExpressNumber(String expressNumber) {
		this.expressNumber = expressNumber;
	}

	public String getWmsCode() {
		return wmsCode;
	}

	public void setWmsCode(String wmsCode) {
		this.wmsCode = wmsCode;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getCreateOperator() {
		return createOperator;
	}

	public void setCreateOperator(String createOperator) {
		this.createOperator = createOperator;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getUpdateOperator() {
		return updateOperator;
	}

	public void setUpdateOperator(String updateOperator) {
		this.updateOperator = updateOperator;
	}


	@Override
	public String toString() {
		return "AfterSaleOrder{" +
			"id=" + id +
			", afterSaleCode=" + afterSaleCode +
			", shopOrderCode=" + shopOrderCode +
			", scmShopOrderCode=" + scmShopOrderCode +
			", picture=" + picture +
			", memo=" + memo +
			", logisticsCorporationCode=" + logisticsCorporationCode +
			", logisticsCorporation=" + logisticsCorporation +
			", expressNumber=" + expressNumber +
			", wmsCode=" + wmsCode +
			", status=" + status +
			", createTime=" + createTime +
			", createOperator=" + createOperator +
			", updateTime=" + updateTime +
			", updateOperator=" + updateOperator +
			"}";
	}
}
